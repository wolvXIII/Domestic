/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.application.widget;

import java.util.ArrayList;
import java.util.Collection;

import ej.bon.XMath;
import ej.color.ColorHelper;
import ej.color.LightHelper;
import ej.microui.display.GraphicsContext;
import ej.microui.event.Event;
import ej.microui.event.generator.Pointer;
import ej.style.Style;
import ej.style.container.AlignmentHelper;
import ej.style.container.Rectangle;
import ej.style.util.StyleHelper;
import ej.widget.basic.Image;
import sew.light.application.util.ColorListener;
import sew.light.util.Color;

public class HuePicker extends Image {

	private final ej.microui.display.Image cursor;
	private final Collection<ColorListener> listeners;

	private float hue;

	public HuePicker(ej.microui.display.Image source, ej.microui.display.Image cursor) {
		super(source);
		this.cursor = cursor;
		this.listeners = new ArrayList<>(1);

		setEnabled(true);

		// Assert source is square.
		if (source.getWidth() != source.getHeight()) {
			throw new IllegalArgumentException();
		}
	}

	public void addHueListener(ColorListener listener) {
		this.listeners.add(listener);
	}

	public void removeHueListener(ColorListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Gets the hue.
	 *
	 * @return the hue.
	 */
	public float getHue() {
		return this.hue;
	}

	/**
	 * Sets the hue.
	 *
	 * @param hue
	 *            the hue to set.
	 */
	public void setHue(float hue) {
		if (hue != this.hue) {
			this.hue = hue;

			for (ColorListener hueListener : this.listeners) {
				hueListener.onColorUpdate(new Color(hue, 0.8f, 0.8f));
			}

			repaint();
		}
	}

	@Override
	public void renderContent(GraphicsContext g, Style style, Rectangle availableBounds) {
		super.renderContent(g, style, availableBounds);

		ej.microui.display.Image source = getSource();
		int width = source.getWidth();
		int halfWidth = width >> 1;
		int quartWidth = width >> 2;

		int xLeftCorner = AlignmentHelper.computeXLeftCorner(width, 0, availableBounds.getWidth(),
				getStyle().getAlignment());
		int yTopCorner = AlignmentHelper.computeYTopCorner(width, 0, availableBounds.getHeight(),
				getStyle().getAlignment());

		double hueRadians = Math.toRadians(this.hue);
		double cos = Math.cos(hueRadians);
		double sin = Math.sin(hueRadians);

		int x = XMath.limit(halfWidth + (int) (quartWidth * cos), 0, width - 1);
		int y = XMath.limit(halfWidth + (int) (quartWidth * sin), 0, width - 1);
		int color = source.readPixel(x, y);
		g.setColor(grayedInvertColor(color));
		g.drawImage(this.cursor, xLeftCorner + x, yTopCorner + y, GraphicsContext.HCENTER | GraphicsContext.VCENTER);

	}

	private static int grayedInvertColor(int color) {
		int light = LightHelper.getLight(color);
		return ColorHelper.getColor(0xff - light, 0xff - light, 0xff - light);
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

	private boolean onPointerPressed(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return false;
	}

	private boolean onPointerDragged(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return true;
	}

	private boolean onPointerReleased(Pointer pointer, int pointerX, int pointerY, int event) {
		updatePosition(pointerX, pointerY);
		return true;
	}

	private void updatePosition(int x, int y) {
		x -= getAbsoluteX();
		y -= getAbsoluteY();
		Rectangle contentBounds = StyleHelper.computeContentBounds(new Rectangle(0, 0, getWidth(), getHeight()),
				getStyle());

		ej.microui.display.Image source = getSource();
		int width = source.getWidth();
		int halfWidth = width >> 1;
		int xLeftCorner = AlignmentHelper.computeXLeftCorner(width, 0, contentBounds.getWidth(),
				getStyle().getAlignment());
		int yTopCorner = AlignmentHelper.computeYTopCorner(width, 0, contentBounds.getHeight(),
				getStyle().getAlignment());
		float hue = 360 + 90 - (float) Math.toDegrees(Math.atan2(x - xLeftCorner - contentBounds.getX() - halfWidth,
				y - yTopCorner - contentBounds.getY() - halfWidth));

		setHue(hue);
	}

}
