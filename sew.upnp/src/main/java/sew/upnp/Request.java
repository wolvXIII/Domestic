/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.upnp;

class Request {

	private final StringBuilder builder;

	Request(String type) {
		this.builder = new StringBuilder(type + " * HTTP/1.1\r\n");
	}

	void append(String key, String value) {
		this.builder.append(key).append(": ").append(value).append("\r\n");
	}

	String getValue() {
		this.builder.append("\r\n");
		return this.builder.toString();
	}

}
