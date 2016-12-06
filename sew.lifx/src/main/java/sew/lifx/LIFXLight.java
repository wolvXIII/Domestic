/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

import java.net.InetAddress;

import sew.lifx.frame.LightSetColor;
import sew.light.DefaultLight;
import sew.light.util.Color;

public class LIFXLight extends DefaultLight {

	private static final int DURATION = 100;
	private final InetAddress address;
	private final LIFXManager manager;

	public LIFXLight(String name, InetAddress address, LIFXManager manager) {
		super(name);
		this.address = address;
		this.manager = manager;
		super.setIntensity(MAX_INTENSITY / 2);
	}

	void simpleUpdate(Color color, int intensity, boolean on) {
		super.setColor(color);
		super.setIntensity(intensity);
		super.setOn(on);
	}

	// @Override
	// public void update(Color color, int intensity) {
	// super.update(color, intensity);
	// this.manager.getFrameSender().send(this.address, LIFXManager.PORT,
	// new LightSetColor(color, intensity, DURATION));
	// }

	@Override
	public void setColor(Color color) {
		super.setColor(color);
		this.manager.getFrameSender().send(this.address, LIFXManager.PORT,
				new LightSetColor(color, getIntensity(), DURATION));
	}

	@Override
	public void setIntensity(int intensity) {
		super.setIntensity(intensity);
		this.manager.getFrameSender().send(this.address, LIFXManager.PORT,
				new LightSetColor(getColor(), intensity, DURATION));
	}

}
