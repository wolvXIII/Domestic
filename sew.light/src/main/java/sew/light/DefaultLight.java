/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

import java.util.ArrayList;
import java.util.Collection;

import sew.light.util.Color;

public class DefaultLight implements Light {

	private final String name;
	private final Collection<LightListener> listeners;
	private Color color;
	private int intensity;
	private boolean on;

	public DefaultLight(String name) {
		this.name = name;
		this.listeners = new ArrayList<>();
		this.color = new Color(0f, 1f, 1f);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void addListener(LightListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(LightListener listener) {
		this.listeners.remove(listener);
	}

	// @Override
	// public void update(boolean on, Color color, int intensity) {
	//
	// setColor(color);
	// setIntensity(intensity);
	// }

	@Override
	public void setColor(Color color) {
		this.color = color;
		for (LightListener colorListener : this.listeners) {
			colorListener.onLightUpdate(this);
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

	@Override
	public boolean isOn() {
		return this.on;
	}

	@Override
	public void setOn(boolean on) {
		this.on = on;
	}

}
