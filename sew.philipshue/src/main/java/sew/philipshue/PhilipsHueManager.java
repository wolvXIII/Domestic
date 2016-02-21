/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.philipshue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sew.upnp.Device;
import sew.upnp.DeviceListener;
import sew.upnp.UpnpManager;

public class PhilipsHueManager implements DeviceListener {

	private final Collection<Device> devices;
	private final Map<String, DeviceManager> devicesManagers;
	private final UpnpManager upnpManager;

	public PhilipsHueManager() throws IOException {
		this.devices = new ArrayList<>();
		this.devicesManagers = new HashMap<>();

		this.upnpManager = new UpnpManager();
		this.upnpManager.addDeviceListener(this);
	}

	public void scan() {
		this.upnpManager.discover("urn:schemas-upnp-org:device:basic:1");// "urn:schemas-upnp-org:device:basic:1"
	}

	@Override
	public void newDevice(Device device) {
		update(device);
	}

	@Override
	public void updateDevice(Device device) {
		update(device);
	}

	private void update(Device device) {
		String friendlyName = device.getFriendlyName();
		if (friendlyName.startsWith("Philips hue")) {
			DeviceManager deviceManager = this.devicesManagers.get(friendlyName);
			if (deviceManager == null) {
				deviceManager = new DeviceManager(this, device);
				this.devicesManagers.put(friendlyName, deviceManager);
				this.devices.add(device);
			} else {
				deviceManager.update();
			}
		}
	}

}
