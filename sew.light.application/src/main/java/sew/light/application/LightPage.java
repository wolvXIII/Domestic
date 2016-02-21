/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application;

import java.io.IOException;

import ej.container.List;
import ej.container.OppositeBars;
import ej.microui.display.Image;
import ej.mwt.MWT;
import ej.navigation.page.Page;
import ej.widget.basic.Label;
import sew.light.Light;
import sew.light.util.Color;
import sew.light.util.ColorListener;

public class LightPage extends Page implements ColorListener {

	private final Label title;
	private Light light;
	private final HuePicker huePicker;

	public LightPage() {
		OppositeBars borderComposite = new OppositeBars(false);

		this.title = new Label();
		this.title.addClassSelector(ClassSelectors.TITLE);

		borderComposite.add(this.title, MWT.NORTH);

		List list = new List(true);
		this.huePicker = createHuePicker();
		list.add(this.huePicker);
		borderComposite.add(list, MWT.CENTER);

		setWidget(borderComposite);
	}

	private HuePicker createHuePicker() {
		try {
			Image colorPickerImage = Image.createImage("/images/huepicker.png");
			Image colorPickerCursorImage = Image.createImage("/images/huepickerCursor.png");

			HuePicker huePicker = new HuePicker(colorPickerImage, colorPickerCursorImage);
			huePicker.addHueListener(new ColorListener() {
				@Override
				public void onColorChanged(Color color) {
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
		this.light.addColorListener(this);
	}

	@Override
	public void hideNotify() {
		super.hideNotify();
		this.light.removeColorListener(this);
	}

	public void setLight(Light light) {
		this.light = light;
		this.title.setText(light.getName());
		this.huePicker.setHue(light.getColor().getHue());
	}

	@Override
	public void onColorChanged(Color color) {
		this.huePicker.setHue(color.getHue());
	}

}
