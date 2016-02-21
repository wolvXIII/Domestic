/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;

import ej.components.dependencyinjection.ServiceLoaderFactory;

public class UpnpManager {

	/**
	 * The SSDP port
	 */
	public static final int PORT = 1900;

	/**
	 * The broadcast address to use when trying to contact UPnP devices.
	 */
	public static final String IP = "239.255.255.250";

	private final InetAddress address;
	private final DatagramSocket socket;

	private volatile boolean running;
	private Thread readThread;

	private final Map<InetAddress, Device> devices;

	private final Collection<DeviceListener> listeners;

	public UpnpManager() throws UnknownHostException, SocketException {
		this.address = InetAddress.getByName(IP);
		this.devices = new HashMap<>();
		this.listeners = new ArrayList<>();
		this.socket = new DatagramSocket(PORT);

		synchronized (this) {
			if (this.running) {
				return;
			}
			this.running = true;
		}
		this.readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				read(UpnpManager.this.socket);
			}

		}, "UPnP Reader");
		this.readThread.start();
	}

	public void addDeviceListener(DeviceListener listener) {
		this.listeners.add(listener);
	}

	public void removeDeviceListener(DeviceListener listener) {
		this.listeners.remove(listener);
	}

	public void stop() {
		this.running = false;
		this.socket.close();
	}

	public void discover() {
		discover("ssdp:all");
	}

	public void discover(final String type) {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("Send UPnP request.");
				Request searchRequest = new Request("M-SEARCH");
				searchRequest.append("HOST", IP + ":" + PORT);
				searchRequest.append("ST", type);
				searchRequest.append("MAN", "ssdp:discover");
				searchRequest.append("MX", "2");
				try {
					send(searchRequest.getValue());
				} catch (IOException e) {
					System.out.println(e.toString());
					// e.printStackTrace();
				}
				System.out.println("UPnP request sent.");
			}
		});
	}

	private void send(String notifyRequest) throws IOException {
		byte[] notifyFrame = notifyRequest.getBytes();
		DatagramPacket packet = new DatagramPacket(notifyFrame, notifyFrame.length, this.address, PORT);
		this.socket.send(packet);
	}

	private void read(final DatagramSocket socket) {
		try {
			while (this.running) {
				byte[] frame = new byte[256];
				// System.out.println("Wait for it…");
				DatagramPacket packet = new DatagramPacket(frame, frame.length);
				socket.receive(packet);
				InetAddress address = packet.getAddress();
				// System.out.println("*** Receive from " + address);
				String reply = new String(packet.getData(), 0, packet.getLength());
				parse(address, reply);
			}
		} catch (IOException e) {
			if (this.running) {
				System.out.println(e.toString());
			}
		}
	}

	private void parse(InetAddress address, String reply) {
		StringTokenizer tokenizer = new StringTokenizer(reply, "\n");
		// System.out.println("Data (" + address + "): " + reply);
		// System.out.println();
		Map<String, String> attributes = new HashMap<>();
		while (tokenizer.hasMoreElements()) {
			String line = tokenizer.nextToken().trim();
			if (line.isEmpty() || line.startsWith("HTTP/1.1") || line.startsWith("NOTIFY *")) {
				// Ignore.
				continue;
			}

			int separatorIndex = line.indexOf(':');
			String key;
			String value;
			if (separatorIndex != -1) {
				key = line.substring(0, separatorIndex);
				if (line.length() > separatorIndex + 1) {
					value = line.substring(separatorIndex + 1);
				} else {
					value = null;
				}
			} else {
				key = line;
				value = null;
			}

			key = key.trim();
			if (value != null) {
				value = value.trim();
			}

			attributes.put(key, value);
			// System.out.println(key + ": " + value);
		}
		receiveInformation(address, attributes);
	}

	private void receiveInformation(final InetAddress address, final Map<String, String> attributes) {
		ServiceLoaderFactory.getServiceLoader().getService(Executor.class).execute(new Runnable() {
			@Override
			public void run() {
				Device device = UpnpManager.this.devices.get(address);
				if (device == null) {
					device = new Device(address, attributes);
					UpnpManager.this.devices.put(address, device);

					for (DeviceListener deviceListener : UpnpManager.this.listeners) {
						deviceListener.newDevice(device);
					}
				} else {
					device.fill(attributes);
				}
			}
		});
	}

	/**
	 * Gets the devices.
	 *
	 * @return the devices.
	 */
	public Collection<Device> getDevices() {
		return this.devices.values();
	}

}
