/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

import java.util.ArrayList;
import java.util.Collection;

import sew.light.util.Color;
import sew.light.util.ColorListener;

public class DefaultLight implements Light {

	private final String name;
	private final Collection<ColorListener> listeners;
	private Color color;
	private int intensity;

	public DefaultLight(String name) {
		this.name = name;
		this.listeners = new ArrayList<>();
		this.color = new Color(0f, 0f, 0f);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void addColorListener(ColorListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeColorListener(ColorListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void update(Color color, int intensity) {
		setColor(color);
		setIntensity(intensity);
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
		for (ColorListener colorListener : this.listeners) {
			colorListener.onColorChanged(color);
		}
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

	@Override
	public int getIntensity() {
		return this.intensity;
	}

}
