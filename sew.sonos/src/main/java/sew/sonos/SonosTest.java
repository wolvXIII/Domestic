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
			printCurrentTrack(device);
			// System.out.println("get position");
			// device.getPosition();
			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("seek");
			// device.seek("00:02:30");
			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("next");
			// device.next();
			// try {
			// Thread.sleep(5000);
			// } catch (InterruptedException e) {
			// }
			// printCurrentTrack(device);
			// System.out.println("previous");
			// device.previous();
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
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
			// System.out.println("get volume " + device.getVolume());
			// System.out.println("volume up");
			// device.setVolume(device.getVolume() + 5);
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("get volume " + device.getVolume());
			// System.out.println("volume down");
			// device.setVolume(device.getVolume() - 5);
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("get mute " + device.isMute());
			// System.out.println("mute");
			// device.setMute(true);
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("get mute " + device.isMute());
			// System.out.println("un mute");
			// device.setMute(false);
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// }
			// System.out.println("get mute " + device.isMute());
		}
	}

	private static void printCurrentTrack(SonosEntity device) {
		System.out.println(device.getTrackArtist() + " \\ " + device.getTrackAlbum() + " \\ " + device.getTrackTitle()
				+ " " + device.getTrackDuration());
	}

}
