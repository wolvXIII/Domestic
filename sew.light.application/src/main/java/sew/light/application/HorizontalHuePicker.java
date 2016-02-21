/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application;

import ej.bon.XMath;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.event.generator.Pointer;
import ej.style.Style;
import ej.style.container.Rectangle;
import ej.style.util.StyleHelper;
import ej.widget.basic.Image;

public class HorizontalHuePicker extends Image {

	// private static final int[] COLORS = { Colors.RED, Colors.YELLOW, Colors.GREEN, Colors.CYAN, Colors.BLUE,
	// Colors.MAGENTA, Colors.RED };

	private int x;

	public HorizontalHuePicker(ej.microui.display.Image source) {
		super(source);
	}

	/**
	 * Gets the hue.
	 *
	 * @return the hue.
	 */
	public float getHue() {
		Rectangle contentBounds = StyleHelper.computeContentBounds(new Rectangle(0, 0, getWidth(), getHeight()),
				getStyle());
		return this.x * 360f / contentBounds.getWidth();
	}

	/**
	 * Sets the hue.
	 *
	 * @param hue
	 *            the hue to set.
	 */
	public void setHue(float hue) {
		Rectangle contentBounds = StyleHelper.computeContentBounds(new Rectangle(0, 0, getWidth(), getHeight()),
				getStyle());
		this.x = (int) (hue * contentBounds.getWidth() / 360f);
	}

	@Override
	public void renderContent(GraphicsContext g, Style style, Rectangle availableBounds) {
		super.renderContent(g, style, availableBounds);

		int height = availableBounds.getHeight();

		g.setColor(Colors.WHITE);
		// AntiAliasedShapes.Singleton.setThickness(height);
		// AntiAliasedShapes.Singleton.drawPoint(g, this.x, height / 2);
		g.drawVerticalLine(this.x, 0, height);
		g.setColor(Colors.BLACK);
		g.drawVerticalLine(this.x + 1, 0, height);

		// int width = availableBounds.getWidth();
		// int height = availableBounds.getHeight();
		//
		// float steps = ((float) width / (COLORS.length - 1));
		// int start = COLORS[0];
		// float startRed = ColorHelper.getRed(start);
		// float startGreen = ColorHelper.getGreen(start);
		// float startBlue = ColorHelper.getBlue(start);
		// int currentX = 0;
		// float cumulSteps = 0;
		// for (int c = 0; ++c < COLORS.length;) {
		// int end = COLORS[c];
		// float endRed = ColorHelper.getRed(end);
		// float endGreen = ColorHelper.getGreen(end);
		// float endBlue = ColorHelper.getBlue(end);
		// float currentRed = startRed;
		// float currentGreen = startGreen;
		// float currentBlue = startBlue;
		// float stepRed = (endRed - startRed) / steps;
		// float stepGreen = (endGreen - startGreen) / steps;
		// float stepBlue = (endBlue - startBlue) / steps;
		//
		// cumulSteps += steps;
		// for (int i = currentX; ++i < cumulSteps;) {
		// int color = ColorHelper.getColor((int) currentRed, (int) currentGreen, (int) currentBlue);
		// g.setColor(color);
		// g.drawVerticalLine(currentX, 0, height);
		//
		// currentRed += stepRed;
		// currentGreen += stepGreen;
		// currentBlue += stepBlue;
		// currentX++;
		// }
		//
		// startRed = endRed;
		// startGreen = endGreen;
		// startBlue = endBlue;
		// }
	}

	@Override
	public boolean onPointerPressed(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return super.onPointerPressed(pointer, pointerX, pointerY, event);
	}

	@Override
	public boolean onPointerDragged(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return super.onPointerDragged(pointer, pointerX, pointerY, event);
	}

	@Override
	public boolean onPointerReleased(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return super.onPointerReleased(pointer, pointerX, pointerY, event);
	}

	private void updatePosition(int x, int y) {
		x = XMath.limit(x, 0, getSource().getWidth() - 1);
		this.x = x;
		repaint();
	}

}
