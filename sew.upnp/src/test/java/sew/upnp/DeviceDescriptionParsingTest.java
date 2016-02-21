/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import com.is2t.testsuite.support.CheckHelper;

import sew.upnp.Device;

/**
 *
 */
public class DeviceDescriptionParsingTest {

	public static void main(String[] args) {
		new DeviceDescriptionParsingTest().test();
	}

	private void test() {
		CheckHelper.startCheck(getClass());

		InputStream inputStream = getClass()
				.getResourceAsStream("/sew/upnp/devicedescriptionparsingtest/sonos1_description.xml");
		try {
			Device device = new Device(inputStream);
			CheckHelper.check(getClass(), "Bad friendly name", device.getFriendlyName(), "192.168.0.11 - Sonos PLAY:1");
			CheckHelper.check(getClass(), "Bad extra display name", device.getExtra().get("displayName"), "PLAY:1");
			CheckHelper.check(getClass(), "Bad children count", device.getChildren().size(), 2);
			CheckHelper.check(getClass(), "Bad services count", device.getServices().size(), 7);
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
			CheckHelper.check(getClass(), "Parsing error", false);
		}

		CheckHelper.endCheck(getClass());
	}

}
