/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application.page;

import java.util.HashMap;
import java.util.Map;

import ej.color.ColorHelper;
import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.mwt.Widget;
import ej.style.util.EditableStyle;
import ej.widget.basic.Image;
import ej.widget.basic.Label;
import ej.widget.composed.ButtonWrapper;
import ej.widget.container.Dock;
import ej.widget.container.List;
import ej.widget.container.Scroll;
import ej.widget.container.SimpleDock;
import ej.widget.listener.OnClickListener;
import ej.widget.navigation.page.Page;
import sew.light.Light;
import sew.light.LightListener;
import sew.light.LightManager;
import sew.light.LightsListener;
import sew.light.application.LightApplication;
import sew.light.application.style.ClassSelectors;
import sew.light.util.Color;

public class LightsListPage extends Page implements LightsListener {

	private final List listComposite;
	private final Map<Light, Widget> lightsWidgets;
	private final LightManager lightManager;

	public LightsListPage() {
		this.lightsWidgets = new HashMap<Light, Widget>();

		SimpleDock borderComposite = new SimpleDock(false);

		Label title = new Label("My Home");
		title.addClassSelector(ClassSelectors.TITLE);

		borderComposite.setFirst(title);

		this.listComposite = new List(false);

		Scroll scroll = new Scroll(false, true);
		scroll.setWidget(this.listComposite);
		borderComposite.setCenter(scroll);

		setWidget(borderComposite);

		this.lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
	}

	@Override
	public void showNotify() {
		Iterable<Light> lights = this.lightManager.getLights();
		for (Light light : lights) {
			addLight(light);
		}
		this.lightManager.addLightsListener(this);
		super.showNotify();
	}

	@Override
	public void hideNotify() {
		this.listComposite.removeAllWidgets();
		this.lightManager.removeLightsListener(this);
		super.hideNotify();
	}

	@Override
	public void onAddLight(Light light) {
		addLight(light);
		revalidate();
	}

	private void addLight(final Light light) {
		ButtonWrapper lightButton = new ButtonWrapper();
		Dock dock = new Dock();
		final Image lightImage = new Image();
		updateLight(light, lightImage);
		dock.addLeft(lightImage);
		light.addListener(new LightListener() {
			@Override
			public void onLightUpdate(Light light) {
				updateLight(light, lightImage);
			}
		});
		Label label = new Label(light.getName());
		dock.setCenter(label);
		lightButton.setWidget(dock);
		lightButton.addClassSelector(ClassSelectors.LIST_ITEM);
		this.listComposite.add(lightButton);
		this.lightsWidgets.put(light, lightButton);
		lightButton.addOnClickListener(new OnClickListener() {
			@Override
			public void onClick() {
				LightPage lightPage = new LightPage();
				lightPage.setLight(light);
				LightApplication.show(lightPage);
			}
		});
	}

	private void updateLight(Light light, Image image) {
		String imagePath;
		if (light.isOn()) {
			imagePath = "/images/lighton.png";
		} else {
			imagePath = "/images/lightoff.png";
		}
		image.setSource(imagePath);

		EditableStyle imageStyle = new EditableStyle();
		imageStyle.setForegroundColor(getRGB(light.getColor()));
		image.mergeStyle(imageStyle);
	}

	@Override
	public void onRemoveLight(Light light) {
		this.lightsWidgets.remove(light);
		revalidate();
	}

	private int getRGB(Color color) {
		float hue = color.getHue() / 360f;
		float saturation = color.getSaturation();
		float value = color.getValue();
		float r, g, b;

		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		if (h == 0) {
			r = value;
			g = t;
			b = p;
		} else if (h == 1) {
			r = q;
			g = value;
			b = p;
		} else if (h == 2) {
			r = p;
			g = value;
			b = t;
		} else if (h == 3) {
			r = p;
			g = q;
			b = value;
		} else if (h == 4) {
			r = t;
			g = p;
			b = value;
		} else if (h <= 6) {
			r = value;
			g = p;
			b = q;
		} else {
			throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
					+ saturation + ", " + value);
		}

		int rgb = ColorHelper.getColor((int) (r * 255), (int) (g * 255), (int) (b * 255));
		// System.out.println("LightsListPage.getRGB() 0x" + Integer.toHexString(rgb) + ", Input was " + hue + ", "
		// + saturation + ", " + value);
		return rgb;
	}

}
