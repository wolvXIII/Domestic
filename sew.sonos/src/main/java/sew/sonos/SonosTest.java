/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sew.sonos;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class SonosTest {

	public static void main(String[] args) throws IOException {
		SonosManager sonosManager = new SonosManager();
		sonosManager.scan();

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}

		Collection<SonosEntity> sonosDevices = sonosManager.getSonosDevices();
		Iterator<SonosEntity> iterator = sonosDevices.iterator();
		if (iterator.hasNext()) {
			SonosEntity device = iterator.next();
			// System.out.println("pause");
			// device.pause();
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("play");
			// device.play();
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
			System.out.println("get volume " + device.getVolume());
			System.out.println("volume up");
			device.setVolume(device.getVolume() + 3);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			System.out.println("get volume " + device.getVolume());
			System.out.println("volume down");
			device.setVolume(device.getVolume() - 3);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			System.out.println("get volume " + device.getVolume());
		}
	}

}
