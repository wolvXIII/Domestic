/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light.util;

public class Color {

	private float hue;
	private float saturation;
	private float value;

	/**
	 * @param hue
	 * @param saturation
	 * @param value
	 */
	public Color(float hue, float saturation, float value) {
		super();
		this.hue = hue % 360f;
		this.saturation = saturation;
		this.value = value;
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
		this.hue = hue % 360f;
	}

	/**
	 * Gets the saturation.
	 *
	 * @return the saturation.
	 */
	public float getSaturation() {
		return this.saturation;
	}

	/**
	 * Sets the saturation.
	 *
	 * @param saturation
	 *            the saturation to set.
	 */
	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value.
	 */
	public float getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the value to set.
	 */
	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Color) {
			Color other = (Color) obj;
			return other.hue == this.hue && other.saturation == this.saturation && other.value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

}
