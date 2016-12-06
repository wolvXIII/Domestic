/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.philipshue;

import java.io.IOException;
import java.util.concurrent.Executor;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import sew.light.DefaultLight;
import sew.light.util.Color;

public class PhilipsHueLight extends DefaultLight {

	private static final int DURATION = 100;
	private final String id;
	private final DeviceManager manager;
	private boolean dirty;

	public PhilipsHueLight(String id, String name, DeviceManager deviceManager) {
		super(name);
		this.id = id;
		this.manager = deviceManager;
		super.setIntensity(MAX_INTENSITY);
	}

	void simpleUpdate(Color color, int intensity, boolean on) {
		super.setColor(color);
		super.setIntensity(intensity);
		super.setOn(on);
	}

	// @Override
	// public void update(Color color, int intensity) {
	// // if (!color.equals(getColor()) || intensity != getIntensity()) {
	// super.update(color, intensity);
	// requestUpdateOnDevice();
	// // }
	// }

	@Override
	public void setColor(Color color) {
		// if (!color.equals(getColor())) {
		System.out.println("PhilipsHueLight.setColor()");
		super.setColor(color);
		requestUpdateOnDevice();
	}

	private void requestUpdateOnDevice() {
		System.out.println("PhilipsHueLight.requestUpdateOnDevice() " + this.dirty);
		if (this.dirty) {
			return;
		}
		this.dirty = true;
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				updateOnDevice();
			}

		});
	}

	private void updateOnDevice() {
		try {
			System.out.println("PhilipsHueLight.updateOnDevice()");
			this.dirty = false;
			this.manager.updateLight(this.id, isOn(), getColor());
			// TODO update intensity?
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setIntensity(int intensity) {
		super.setIntensity(intensity);
		requestUpdateOnDevice();
	}

	@Override
	public void setOn(boolean on) {
		super.setOn(on);
		requestUpdateOnDevice();
	}

}
