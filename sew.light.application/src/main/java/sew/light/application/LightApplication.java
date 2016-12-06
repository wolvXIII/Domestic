/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application;

import java.io.IOException;

import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.microui.MicroUI;
import ej.microui.display.GraphicsContext;
import ej.style.State;
import ej.style.Stylesheet;
import ej.style.background.NoBackground;
import ej.style.background.PlainBackground;
import ej.style.background.SimpleRoundedPlainBackground;
import ej.style.border.ComplexRectangularBorder;
import ej.style.border.ComplexRectangularColoredBorder;
import ej.style.border.SimpleRoundedBorder;
import ej.style.outline.ComplexOutline;
import ej.style.outline.SimpleOutline;
import ej.style.selector.ClassSelector;
import ej.style.selector.StateSelector;
import ej.style.selector.TypeOrSubtypeSelector;
import ej.style.selector.TypeSelector;
import ej.style.selector.combinator.DescendantCombinator;
import ej.style.text.ComplexTextManager;
import ej.style.util.EditableStyle;
import ej.style.util.StyleHelper;
import ej.widget.StyledDesktop;
import ej.widget.StyledDialog;
import ej.widget.StyledPanel;
import ej.widget.composed.Button;
import ej.widget.navigation.navigator.HistorizedNavigator;
import ej.widget.navigation.page.Page;
import ej.widget.navigation.transition.HorizontalScreenshotTransitionManager;
import sew.lifx.LIFXManager;
import sew.light.DefaultLight;
import sew.light.LightManager;
import sew.light.application.page.LightsListPage;
import sew.light.application.style.ClassSelectors;
import sew.light.application.util.MicroEJColors;
import sew.light.application.widget.HuePicker;
import sew.light.util.Color;
import sew.philipshue.PhilipsHueManager;

/**
 *
 */
public class LightApplication {

	private static HistorizedNavigator navigator;

	public static void main(String[] args) throws IOException {
		MicroUI.start();

		initializeStylesheet();

		navigator = new HistorizedNavigator();
		navigator.setTransitionManager(new HorizontalScreenshotTransitionManager());
		navigator.addClassSelector(ClassSelectors.BODY);
		// StyledPanel panel = new StyledPanel();
		//
		// Image colorPickerImage = Image.createImage("/images/huepicker.png");
		// Image colorPickerCursorImage = Image.createImage("/images/huepickerCursor.png");
		//
		// HuePicker colorPicker = new HuePicker(colorPickerImage, colorPickerCursorImage);
		// panel.setWidget(colorPicker);
		//
		// panel.show(desktop, true);
		final StyledDesktop desktop = new StyledDesktop();
		StyledPanel panel = new StyledPanel();
		panel.setWidget(navigator);
		panel.show(desktop, true);
		desktop.show();
		navigator.show(LightsListPage.class.getName());

		Timer timer = ServiceLoaderFactory.getServiceLoader().getService(Timer.class);

		LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);

		lightManager.addMessageListener(new MessageManager(desktop));

		DefaultLight fakeLight = new DefaultLight("My Light");
		fakeLight.setColor(new Color(236.81941f, 0.8f, 0.8f));
		lightManager.addLight(fakeLight);

		final LIFXManager lifxManager = new LIFXManager();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				lifxManager.scan();
			}
		};
		timer.schedule(timerTask, 1000, 10000);

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

		// timer.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// new MessageManager(desktop).onNewMessage("New Philips Hue bridge detected.\nPlease press the button.");
		// }
		// }, 1000);
	}

	public static void show(Page page) {
		navigator.show(page);
	}

	public static void back() {
		navigator.back();
	}

	private static void initializeStylesheet() {
		Stylesheet stylesheet = StyleHelper.getStylesheet();

		EditableStyle defaultStyle = new EditableStyle();
		defaultStyle.setBackground(NoBackground.NO_BACKGROUND);
		stylesheet.setDefaultStyle(defaultStyle);

		EditableStyle backgroundStyle = new EditableStyle();
		backgroundStyle.setBackgroundColor(MicroEJColors.WHITE);
		backgroundStyle.setBackground(new PlainBackground());
		backgroundStyle.setForegroundColor(MicroEJColors.CONCRETE_BLACK_75);
		stylesheet.addRule(new ClassSelector(ClassSelectors.BODY), backgroundStyle);

		EditableStyle titleStyle = new EditableStyle();
		titleStyle.setPadding(new SimpleOutline(8));
		// titleStyle.setFontProfile(new FontProfile("", FontSize.LARGE, Font.STYLE_PLAIN));
		ComplexRectangularColoredBorder titleBorder = new ComplexRectangularColoredBorder();
		titleBorder.setBottom(1, MicroEJColors.CONCRETE_BLACK_25);
		titleStyle.setBorder(titleBorder);
		titleStyle.setAlignment(GraphicsContext.HCENTER | GraphicsContext.VCENTER);
		titleStyle.setBackgroundColor(MicroEJColors.CONCRETE_WHITE_50);
		titleStyle.setBackground(new PlainBackground());
		stylesheet.addRule(new ClassSelector(ClassSelectors.TITLE), titleStyle);

		EditableStyle listItemStyle = new EditableStyle();
		// listItemStyle.setPadding(new SimpleOutline(8));
		listItemStyle.setAlignment(GraphicsContext.LEFT | GraphicsContext.VCENTER);
		listItemStyle.setPadding(new ComplexOutline(2, 2, 2, 5));
		// ComplexRectangularColoredBorder listItemBorder = new ComplexRectangularColoredBorder();
		// listItemBorder.setBottom(1, MicroEJColors.CONCRETE_BLACK_25);
		// listItemStyle.setBorder(listItemBorder);
		stylesheet.addRule(new ClassSelector(ClassSelectors.LIST_ITEM), listItemStyle);

		EditableStyle activeStyle = new EditableStyle();
		backgroundStyle.setBackgroundColor(MicroEJColors.CONCRETE_WHITE_50);
		backgroundStyle.setBackground(new PlainBackground());
		// activeStyle.setForegroundColor(Colors.WHITE);
		stylesheet.addRule(new StateSelector(State.Active), activeStyle);

		EditableStyle huePickerStyle = new EditableStyle();
		huePickerStyle.setAlignment(GraphicsContext.HCENTER | GraphicsContext.VCENTER);
		stylesheet.addRule(new TypeSelector(HuePicker.class), huePickerStyle);

		EditableStyle dialogStyle = new EditableStyle();
		dialogStyle.setBackground(new SimpleRoundedPlainBackground(10));
		dialogStyle.setBorder(new SimpleRoundedBorder(10, 1));
		dialogStyle.setBorderColor(MicroEJColors.CONCRETE_WHITE_75);
		dialogStyle.setPadding(new SimpleOutline(10));
		stylesheet.addRule(new TypeOrSubtypeSelector(StyledDialog.class), dialogStyle);

		EditableStyle buttonInDialogStyle = new EditableStyle();
		buttonInDialogStyle.setAlignment(GraphicsContext.HCENTER | GraphicsContext.VCENTER);
		buttonInDialogStyle.setMargin(new ComplexOutline(6, 0, 0, 0));
		buttonInDialogStyle.setPadding(new ComplexOutline(6, 0, 0, 0));
		buttonInDialogStyle.setBorderColor(MicroEJColors.CONCRETE_WHITE_75);
		buttonInDialogStyle.setBorder(new ComplexRectangularBorder(1, 0, 0, 0));
		stylesheet.addRule(
				new DescendantCombinator(new TypeOrSubtypeSelector(StyledDialog.class), new TypeSelector(Button.class)),
				buttonInDialogStyle);

		EditableStyle multilineStyle = new EditableStyle();
		multilineStyle.setTextManager(new ComplexTextManager());
		stylesheet.addRule(new ClassSelector(ClassSelectors.MULTILINE), multilineStyle);

	}

}
