/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application;

import java.util.HashMap;
import java.util.Map;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.container.List;
import ej.container.Scroll;
import ej.mwt.Widget;
import ej.navigation.page.Page;
import ej.widget.composed.Button;
import ej.widget.listener.OnClickListener;
import sew.light.Light;
import sew.light.LightManager;
import sew.light.LightsListener;

public class LightsListPage extends Page implements LightsListener {

	private final List listComposite;
	private final Map<Light, Widget> lightsWidgets;
	private final LightManager lightManager;

	public LightsListPage() {
		this.lightsWidgets = new HashMap<Light, Widget>();

		this.listComposite = new List(false);

		setWidget(new Scroll(false, this.listComposite, true));

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
		Button lightButton = new Button(light.getName());
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

	@Override
	public void onRemoveLight(Light light) {
		this.lightsWidgets.remove(light);
		revalidate();
	}

}
