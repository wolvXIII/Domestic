/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application.page;

import java.io.IOException;

import ej.microui.display.Image;
import ej.widget.basic.Label;
import ej.widget.container.List;
import ej.widget.container.SimpleDock;
import ej.widget.navigation.page.Page;
import sew.light.Light;
import sew.light.LightListener;
import sew.light.application.style.ClassSelectors;
import sew.light.application.util.ColorListener;
import sew.light.application.widget.HuePicker;
import sew.light.util.Color;

public class LightPage extends Page implements LightListener {

	private final Label title;
	private Light light;
	private final HuePicker huePicker;

	public LightPage() {
		SimpleDock borderComposite = new SimpleDock(false);

		this.title = new Label();
		this.title.addClassSelector(ClassSelectors.TITLE);

		borderComposite.setFirst(this.title);

		List list = new List(true);
		this.huePicker = createHuePicker();
		list.add(this.huePicker);
		borderComposite.setCenter(list);

		setWidget(borderComposite);
	}

	private HuePicker createHuePicker() {
		try {
			Image colorPickerImage = Image.createImage("/images/huepicker.png");
			Image colorPickerCursorImage = Image.createImage("/images/huepickerCursor.png");

			HuePicker huePicker = new HuePicker(colorPickerImage, colorPickerCursorImage);
			huePicker.addHueListener(new ColorListener() {

				@Override
				public void onColorUpdate(Color color) {
					LightPage.this.light.setColor(color);
				}
			});
			return huePicker;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void showNotify() {
		super.showNotify();
		this.light.addListener(this);
	}

	@Override
	public void hideNotify() {
		super.hideNotify();
		this.light.removeListener(this);
	}

	public void setLight(Light light) {
		this.light = light;
		this.title.setText(light.getName());
		this.huePicker.setHue(light.getColor().getHue());
	}

	@Override
	public void onLightUpdate(Light light) {
		this.huePicker.setHue(light.getColor().getHue());
	}

}
