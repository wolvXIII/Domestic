/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.sonos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sew.upnp.Device;
import sew.upnp.DeviceListener;
import sew.upnp.UpnpManager;

public class SonosManager implements DeviceListener {

	private final Collection<SonosEntity> entities;
	private final Map<Device, SonosEntity> devices;
	private final UpnpManager upnpManager;

	public SonosManager() throws IOException {
		this.entities = new ArrayList<>();
		this.devices = new HashMap<>();

		this.upnpManager = new UpnpManager();
		this.upnpManager.addDeviceListener(this);
	}

	public void scan() {
		this.upnpManager.discover(SonosEntity.URN_AV_TRANSPORT);
	}

	@Override
	public void newDevice(Device device) {
		update(device);
	}

	@Override
	public void updateDevice(Device device) {
		update(device);
	}

	public Collection<SonosEntity> getSonosDevices() {
		return this.entities;
	}

	private void update(Device device) {
		String friendlyName = device.getFriendlyName();
		if (friendlyName.contains("Sonos") && !this.devices.containsKey(device)) {
			SonosEntity sonosEntity = new SonosEntity(device);
			this.devices.put(device, sonosEntity);
			this.entities.add(sonosEntity);
		}
	}

}
