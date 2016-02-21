/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Represents a UPNP device.
 */
public class Device {

	private final InetAddress address;

	private String deviceType;
	private String friendlyName;
	private String manufacturer;
	private String manufacturerURL;
	private String modelNumber;
	private String modelDescription;
	private String modelName;
	private String modelURL;
	private String softwareVersion;
	private String hardwareVersion;
	private String serialNumber;
	private String UDN;

	private final Device parent;
	private final Collection<Device> children;
	private final Collection<Service> services;

	private final Map<String, String> extra;

	private final Map<String, String> attributes;

	public Device(InetAddress address, Map<String, String> attributes) {
		this.address = address;
		this.attributes = attributes;
		this.parent = null;
		this.children = new ArrayList<>();
		this.services = new ArrayList<>();
		this.extra = new HashMap<>();

		loadDescription();
	}

	public Device(Device parent) {
		this.attributes = new HashMap<>();
		this.address = parent.address;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.services = new ArrayList<>();
		this.extra = new HashMap<>();
	}

	public Device(InputStream deviceDescriptionXML) throws IOException, XmlPullParserException {
		this.attributes = new HashMap<>();
		this.address = null;
		this.parent = null;
		this.children = new ArrayList<>();
		this.services = new ArrayList<>();
		this.extra = new HashMap<>();
		parseDescription(deviceDescriptionXML);
	}

	private void loadDescription() {
		String location = Device.this.attributes.get("LOCATION");
		try {
			System.out.println("Get UPnP device description " + location);
			URL url = new URL(location);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			parseDescription(inputStream);
			System.out.println("UPnP device description received & decoded.");
		} catch (IOException | XmlPullParserException e) {
			System.out.println(
					"Cannot read device description for device at " + Device.this.address + " (" + location + ".");
			System.out.println(e.toString());
		}
	}

	private void parseDescription(InputStream inputStream) throws XmlPullParserException, IOException {
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(inputStream, "utf-8");
		Document document = new Document();
		document.parse(parser);
		Element rootElement = document.getRootElement();
		int childCount = rootElement.getChildCount();
		for (int i = -1; ++i < childCount;) {
			if (rootElement.getType(i) != Node.ELEMENT) {
				continue;
			}
			Element element = rootElement.getElement(i);
			String name = element.getName();
			switch (name) {
			case "device":
				parseDevice(element, this);
				break;
			}
		}
	}

	private void parseDevice(Element rootElement, Device device) throws IOException, XmlPullParserException {
		int childCount = rootElement.getChildCount();
		for (int i = -1; ++i < childCount;) {
			if (rootElement.getType(i) != Node.ELEMENT) {
				continue;
			}
			Element element = rootElement.getElement(i);
			String name = element.getName();
			switch (name) {
			case "deviceType":
				device.deviceType = getTextContent(element);
				break;
			case "friendlyName":
				device.friendlyName = getTextContent(element);
				break;
			case "manufacturer":
				device.manufacturer = getTextContent(element);
				break;
			case "manufacturerURL":
				device.manufacturerURL = getTextContent(element);
				break;
			case "modelNumber":
				device.modelNumber = getTextContent(element);
				break;
			case "modelDescription":
				device.modelDescription = getTextContent(element);
				break;
			case "modelName":
				device.modelName = getTextContent(element);
				break;
			case "softwareVersion":
				device.softwareVersion = getTextContent(element);
				break;
			case "hardwareVersion":
				device.hardwareVersion = getTextContent(element);
				break;
			case "serialNumber":
				device.serialNumber = getTextContent(element);
				break;
			case "UDN":
				device.UDN = getTextContent(element);
				break;
			case "serviceList":
				parseServiceList(element, this);
				break;
			case "deviceList":
				parseDeviceList(element);
				break;
			default:
				this.extra.put(name, getTextContent(element));
			}
		}

	}

	private void parseDeviceList(Element rootElement) throws IOException, XmlPullParserException {
		int childCount = rootElement.getChildCount();
		for (int i = -1; ++i < childCount;) {
			if (rootElement.getType(i) != Node.ELEMENT) {
				continue;
			}
			Element element = rootElement.getElement(i);
			String name = element.getName();
			switch (name) {
			case "device":
				Device childDevice = new Device(this);
				parseDevice(element, childDevice);
				this.children.add(childDevice);
				break;
			}
		}
	}

	private void parseServiceList(Element rootElement, Device device) {
		int childCount = rootElement.getChildCount();
		for (int i = -1; ++i < childCount;) {
			if (rootElement.getType(i) != Node.ELEMENT) {
				continue;
			}
			Element element = rootElement.getElement(i);
			String name = element.getName();
			switch (name) {
			case "service":
				Service service = parseService(element);
				this.services.add(service);
				break;
			}
		}
	}

	private Service parseService(Element rootElement) {
		String serviceType = "";
		String serviceId = "";
		String controlURL = "";
		String eventSubURL = "";
		String scpdURL = "";
		int childCount = rootElement.getChildCount();
		for (int i = -1; ++i < childCount;) {
			if (rootElement.getType(i) != Node.ELEMENT) {
				continue;
			}
			Element element = rootElement.getElement(i);
			String name = element.getName();
			switch (name) {
			case "serviceType":
				serviceType = getTextContent(element);
			case "serviceId":
				serviceId = getTextContent(element);
			case "controlURL":
				controlURL = getTextContent(element);
			case "eventSubURL":
				eventSubURL = getTextContent(element);
			case "SCPDURL":
				scpdURL = getTextContent(element);
			}
		}
		return new Service(serviceType, serviceId, controlURL, eventSubURL, scpdURL);
	}

	/**
	 * Gets the address.
	 *
	 * @return the address.
	 */
	public InetAddress getAddress() {
		return this.address;
	}

	/**
	 * Gets the friendlyName.
	 *
	 * @return the friendlyName.
	 */
	public String getFriendlyName() {
		return this.friendlyName;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children.
	 */
	public Collection<Device> getChildren() {
		return this.children;
	}

	/**
	 * Gets the services.
	 *
	 * @return the services.
	 */
	public Collection<Service> getServices() {
		return this.services;
	}

	/**
	 * Gets the extra.
	 *
	 * @return the extra.
	 */
	public Map<String, String> getExtra() {
		return this.extra;
	}

	public static String getTextContent(Node node) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < node.getChildCount(); i++) {
			if (node.getType(i) != Node.TEXT) {
				continue; // skip non-text nodes
			}
			String child = node.getText(i);
			buffer.append(child);
		}
		return buffer.toString();
	}

	public void fill(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
	}

}
