/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

/**
 *
 */
public class Service {

	private final String serviceType;
	private final String serviceId;
	private final String controlURL;
	private final String eventSubURL;
	private final String scpdURL;

	public Service(String serviceType, String serviceId, String controlURL, String eventSubURL, String scpdURL) {
		this.serviceType = serviceType;
		this.serviceId = serviceId;
		this.controlURL = controlURL;
		this.eventSubURL = eventSubURL;
		this.scpdURL = scpdURL;
	}

	/**
	 * Gets the serviceType.
	 * 
	 * @return the serviceType.
	 */
	public String getServiceType() {
		return this.serviceType;
	}

	/**
	 * Gets the serviceId.
	 * 
	 * @return the serviceId.
	 */
	public String getServiceId() {
		return this.serviceId;
	}

	/**
	 * Gets the controlURL.
	 * 
	 * @return the controlURL.
	 */
	public String getControlURL() {
		return this.controlURL;
	}

	/**
	 * Gets the eventSubURL.
	 * 
	 * @return the eventSubURL.
	 */
	public String getEventSubURL() {
		return this.eventSubURL;
	}

	/**
	 * Gets the scpdURL.
	 * 
	 * @return the scpdURL.
	 */
	public String getScpdURL() {
		return this.scpdURL;
	}

}
