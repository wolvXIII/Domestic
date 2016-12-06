/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application.widget;

import ej.bon.XMath;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.event.Event;
import ej.microui.event.generator.Pointer;
import ej.style.Style;
import ej.style.container.Rectangle;
import ej.widget.basic.Image;

public class ColorPicker extends Image {

	private static final int HALF_CROSS_SIZE = 5;
	private int x;
	private int y;
	private int color;

	public ColorPicker(ej.microui.display.Image source) {
		super(source);
	}

	/**
	 * Gets the color.
	 *
	 * @return the color.
	 */
	public int getColor() {
		return this.color;
	}

	/**
	 * Sets the color.
	 *
	 * @param color
	 *            the color to set.
	 */
	public void setColor(int color) {
		this.color = color;
		// TODO Compute & update cursor position.
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public void renderContent(GraphicsContext g, Style style, Rectangle availableBounds) {
		super.renderContent(g, style, availableBounds);

		// boolean lightColor = LightHelper.isLightColor(this.color);
		// if (lightColor) {
		// g.setColor(Colors.BLACK);
		// } else {
		// g.setColor(Colors.WHITE);
		// }
		g.setColor(Colors.WHITE);
		g.drawHorizontalLine(this.x - HALF_CROSS_SIZE, this.y - 1, HALF_CROSS_SIZE << 1);
		g.drawHorizontalLine(this.x - HALF_CROSS_SIZE, this.y + 1, HALF_CROSS_SIZE << 1);
		g.drawVerticalLine(this.x - 1, this.y - HALF_CROSS_SIZE, HALF_CROSS_SIZE << 1);
		g.drawVerticalLine(this.x + 1, this.y - HALF_CROSS_SIZE, HALF_CROSS_SIZE << 1);
		g.setColor(Colors.BLACK);
		g.drawHorizontalLine(this.x - HALF_CROSS_SIZE, this.y, HALF_CROSS_SIZE << 1);
		g.drawVerticalLine(this.x, this.y - HALF_CROSS_SIZE, HALF_CROSS_SIZE << 1);
	}

	@Override
	public boolean handleEvent(int event) {
		if (Event.getType(event) == Event.POINTER) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			int pointerX = pointer.getX();
			int pointerY = pointer.getY();
			int action = Pointer.getAction(event);
			switch (action) {
			case Pointer.PRESSED:
				return onPointerPressed(pointer, pointerX, pointerY, event);
			case Pointer.RELEASED:
				return onPointerReleased(pointer, pointerX, pointerY, event);
			case Pointer.DRAGGED:
				return onPointerDragged(pointer, pointerX, pointerY, event);
			}
		}
		return super.handleEvent(event);
	}

	public boolean onPointerPressed(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return false;
	}

	public boolean onPointerDragged(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return true;
	}

	public boolean onPointerReleased(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return true;
	}

	private void updatePosition(int x, int y) {
		x = XMath.limit(x, 0, getSource().getWidth() - 1);
		y = XMath.limit(y, 0, getSource().getHeight() - 1);
		this.x = x;
		this.y = y;
		this.color = getSource().readPixel(x, y);
		repaint();
	}

}
