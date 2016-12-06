/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

import java.io.IOException;
import java.util.Collection;

import sew.light.Light;
import sew.light.util.Color;

/**
 *
 */
public class TestLifx {

	private static final int DURATION = 2500;
	private static final int PORT = 56700;

	public static void main(String[] args) throws IOException {
		LIFXManager lifxManager = new LIFXManager();
		lifxManager.scan();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		Collection<LIFXLight> lights = lifxManager.getLights();
		for (Light light : lights) {
			light.setColor(new Color((float) Math.random(), 1.0f, 1.0f));
		}
		// InetAddress address = InetAddress.getByName("192.168.0.255");
		// final DatagramSocket socket = new DatagramSocket(PORT);
		// // MulticastSocket socket = new MulticastSocket(address);
		// // socket.setBroadcast(true);
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// new FrameReader().receive(socket);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }).start();
		//
		// FrameSender frameSender = new FrameSender();
		// frameSender.send(address, PORT, socket, new GetService());
		//
		// while (true) {
		// // frameSender.send(address, PORT, socket, new LightSetColor(new LIFXColor(0.0f, 1.0f, 1.0f, 6500),
		// // DURATION));
		// // try {
		// // Thread.sleep(DURATION*2);
		// // } catch (InterruptedException e) {
		// // }
		// // frameSender.send(address, PORT, socket,
		// // new LightSetColor(new LIFXColor(0.667f, 1.0f, 1.0f, 6500), DURATION));
		// // try {
		// // Thread.sleep(DURATION*2);
		// // } catch (InterruptedException e) {
		// // }
		// // frameSender.send(address, PORT, socket,
		// // new LightSetColor(new LIFXColor(0.333f, 1.0f, 1.0f, 6500), DURATION));
		// // try {
		// // Thread.sleep(DURATION*2);
		// // } catch (InterruptedException e) {
		// // }
		//
		// frameSender.send(address, PORT, socket, new LightGet());
		// frameSender.send(address, PORT, socket, new LightSetColor(new Color(0.14f, 0.16f, 1.0f), DURATION));
		// try {
		// Thread.sleep(DURATION * 2);
		// } catch (InterruptedException e) {
		// }
		// frameSender.send(address, PORT, socket, new LightGet());
		// frameSender.send(address, PORT, socket, new LightSetColor(new Color(0.14f, 0.16f, 1.0f), DURATION));
		// try {
		// Thread.sleep(DURATION * 2);
		// } catch (InterruptedException e) {
		// }
		// }
	}

}
