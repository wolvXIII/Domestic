/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import sew.lifx.frame.LightGet;
import sew.light.LightManager;

public class LIFXManager {

	public static final int PORT = 56700;

	private final InetAddress address;
	private final DatagramSocket socket;
	private final FrameSender frameSender;

	private final Map<InetAddress, LIFXLight> lights;

	public LIFXManager() throws IOException {
		this.address = InetAddress.getByName("192.168.0.255");
		this.socket = new DatagramSocket(PORT);
		this.lights = new HashMap<>();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new FrameReader(LIFXManager.this).receive(LIFXManager.this.socket);
				} catch (IOException e) {
				}
			}
		}, "LIFX reader").start();
		this.frameSender = new FrameSender(this.socket);
	}

	public LIFXLight getLight(String name, InetAddress address) {
		LIFXLight light = this.lights.get(address);
		if (light == null) {
			light = new LIFXLight(name, address, this);
			LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
			lightManager.addLight(light);
			this.lights.put(address, light);
		}
		return light;
	}

	/**
	 * Gets the lights.
	 *
	 * @return the lights.
	 */
	public Collection<LIFXLight> getLights() {
		return this.lights.values();
	}

	public void scan() {
		this.frameSender.send(this.address, PORT, new LightGet());
	}

	/**
	 * Gets the frame sender.
	 *
	 * @return the frame sender.
	 */
	public FrameSender getFrameSender() {
		return this.frameSender;
	}

}
