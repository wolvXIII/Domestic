/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

import java.io.IOException;
import java.util.Collection;

/**
 *
 */
public class TestUpnp {

	public static void main(String[] args) throws IOException, InterruptedException {
		UpnpManager upnpService = new UpnpManager();
		upnpService.addDeviceListener(new DeviceListener() {
			@Override
			public void newDevice(Device device) {
				printDevice(device, 0);
			}

			private void printDevice(Device device, int shift) {
				String shiftString = "";
				for (int i = -1; ++i < shift;) {
					shiftString += "\t";
				}
				System.out.println(shiftString + "### Device ###");
				System.out.println(shiftString + device.getFriendlyName());
				Collection<Service> services = device.getServices();
				for (Service service : services) {
					System.out.println(shiftString + "*** Service ***");
					System.out.println(shiftString + service.getServiceType());
					System.out.println(shiftString + service.getServiceId());
				}
				Collection<Device> children = device.getChildren();
				for (Device child : children) {
					printDevice(child, shift + 1);
				}
			}

			@Override
			public void updateDevice(Device device) {

			}
		});
		upnpService.discover();

		Thread.sleep(5000);

		// Collection<Device> devices = upnpService.getDevices();

		//
		// upnpService.stop();
	}

}
