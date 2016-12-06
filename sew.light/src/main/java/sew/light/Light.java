/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

import sew.light.util.Color;

public interface Light {

	public static final int MAX_INTENSITY = 10000;

	/**
	 * Gets the light name.
	 *
	 * @return the light name.
	 */
	String getName();

	void addListener(LightListener listener);

	void removeListener(LightListener listener);

	// /**
	// * Sets the color and intensity.
	// *
	// * @param on
	// * <code>true</code> if the light is on, <code>false</code> otherwise.
	// * @param color
	// * the color to set.
	// * @param intensity
	// * the intensity to set.
	// */
	// void update(boolean on, Color color, int intensity);

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

	/**
	 * Gets whether the light is on or off.
	 *
	 * @return <code>true</code> if the light is on, <code>false</code> otherwise.
	 */
	boolean isOn();

	/**
	 * Turns on or off the light.
	 *
	 * @param on
	 *            <code>true</code> to turn on the light, <code>false</code> otherwise.
	 */
	void setOn(boolean on);

}
