/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

public class Request {

	private final StringBuilder builder;

	public Request(String type) {
		this.builder = new StringBuilder(type + " * HTTP/1.1\r\n");
	}

	public void append(String key, String value) {
		this.builder.append(key).append(": ").append(value).append("\r\n");
	}

	public void appendData(String data) {
		this.builder.append(data).append("\r\n");
	}

	public String getValue() {
		this.builder.append("\r\n");
		return this.builder.toString();
	}

}
