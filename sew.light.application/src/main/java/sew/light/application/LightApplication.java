/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application;

import java.io.IOException;

import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.microui.MicroUI;
import ej.microui.display.Colors;
import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.navigation.desktop.NavigationDesktop;
import ej.navigation.page.Page;
import ej.style.State;
import ej.style.Stylesheet;
import ej.style.background.PlainBackground;
import ej.style.border.ComplexRectangularBorder;
import ej.style.font.FontProfile;
import ej.style.font.FontProfile.FontSize;
import ej.style.outline.SimpleOutline;
import ej.style.selector.ClassSelector;
import ej.style.selector.StateSelector;
import ej.style.selector.TypeSelector;
import ej.style.util.EditableStyle;
import ej.style.util.StyleHelper;
import sew.lifx.LIFXManager;
import sew.light.DefaultLight;
import sew.light.LightManager;
import sew.philipshue.PhilipsHueManager;

/**
 *
 */
public class LightApplication {

	private static NavigationDesktop desktop;

	public static void main(String[] args) throws IOException {
		MicroUI.start();

		initializeStylesheet();

		desktop = new NavigationDesktop();
		desktop.addClassSelector(ClassSelectors.BODY);
		// StyledPanel panel = new StyledPanel();
		//
		// Image colorPickerImage = Image.createImage("/images/huepicker.png");
		// Image colorPickerCursorImage = Image.createImage("/images/huepickerCursor.png");
		//
		// HuePicker colorPicker = new HuePicker(colorPickerImage, colorPickerCursorImage);
		// panel.setWidget(colorPicker);
		//
		// panel.show(desktop, true);
		desktop.show(LightsListPage.class.getName());
		desktop.show();

		Timer timer = ServiceLoaderFactory.getServiceLoader().getService(Timer.class);

		LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
		lightManager.addLight(new DefaultLight("My Light"));

		final LIFXManager lifxManager = new LIFXManager();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				lifxManager.scan();
			}
		};
		timer.schedule(timerTask, 4000, 10000);

		final PhilipsHueManager philipsHueManager = new PhilipsHueManager();
		TimerTask hueTask = new TimerTask() {
			@Override
			public void run() {
				philipsHueManager.scan();
			}
		};
		timer.schedule(hueTask, 2000, 10000);

		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// philipsHueManager.scan();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// System.out.println("End Philou");
		// }
		// }.start();
		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// sleep(1000);
		// } catch (InterruptedException e1) {
		// }
		// try {
		// lifxManager.scan();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// System.out.println("End LIFX");
		// }
		// }.start();
	}

	public static void show(Page page) {
		desktop.show(page);
	}

	public static void back() {
		desktop.back();
	}

	private static void initializeStylesheet() {
		Stylesheet stylesheet = StyleHelper.getStylesheet();

		EditableStyle defaultStyle = new EditableStyle();
		defaultStyle.setBorder(new SimpleOutline());
		stylesheet.setDefaultStyle(defaultStyle);

		EditableStyle backgroundStyle = new EditableStyle();
		backgroundStyle.setBorder(new PlainBackground(0x2e2f42));
		backgroundStyle.setForegroundColor(Colors.WHITE);
		stylesheet.addRule(new ClassSelector(ClassSelectors.BODY), backgroundStyle);

		EditableStyle titleStyle = new EditableStyle();
		titleStyle.setPadding(new SimpleOutline(8));
		titleStyle.setFontProfile(new FontProfile("", FontSize.LARGE, Font.STYLE_PLAIN));
		ComplexRectangularBorder titleBorder = new ComplexRectangularBorder();
		titleBorder.setBottom(1, Colors.SILVER);
		titleStyle.setBorder(titleBorder);
		stylesheet.addRule(new ClassSelector(ClassSelectors.TITLE), titleStyle);

		EditableStyle listItemStyle = new EditableStyle();
		listItemStyle.setPadding(new SimpleOutline(8));
		ComplexRectangularBorder listItemBorder = new ComplexRectangularBorder();
		listItemBorder.setBottom(1, Colors.SILVER);
		listItemStyle.setBorder(listItemBorder);
		stylesheet.addRule(new ClassSelector(ClassSelectors.LIST_ITEM), listItemStyle);

		EditableStyle activeStyle = new EditableStyle();
		activeStyle.setBorder(new PlainBackground(0x4a9eff));
		activeStyle.setForegroundColor(Colors.WHITE);
		stylesheet.addRule(new StateSelector(State.Active), activeStyle);

		EditableStyle huePickerStyle = new EditableStyle();
		huePickerStyle.setAlignment(GraphicsContext.HCENTER | GraphicsContext.VCENTER);
		stylesheet.addRule(new TypeSelector(HuePicker.class), huePickerStyle);

	}

}
