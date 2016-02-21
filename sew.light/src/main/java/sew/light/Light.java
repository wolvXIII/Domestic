/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

import sew.light.util.Color;
import sew.light.util.ColorListener;

public interface Light {

	public static final int MAX_INTENSITY = 10000;

	/**
	 * Gets the light name.
	 *
	 * @return the light name.
	 */
	String getName();

	void addColorListener(ColorListener listener);

	void removeColorListener(ColorListener listener);

	/**
	 * Sets the color and intensity.
	 *
	 * @param color
	 *            the color to set.
	 * @param intensity
	 *            the intensity to set.
	 */
	void update(Color color, int intensity);

	/**
	 * Sets the color.
	 *
	 * @param color
	 *            the color to set.
	 */
	void setColor(Color color);

	/**
	 * Gets the color.
	 *
	 * @return the color.
	 */
	Color getColor();

	/**
	 * Sets the intensity.
	 *
	 * @param intensity
	 *            the intensity to set.
	 */
	void setIntensity(int intensity);

	/**
	 * Gets the intensity.
	 *
	 * @return the intensity.
	 */
	int getIntensity();

}
