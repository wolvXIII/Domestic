/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

public class FrameHeader implements LIFXFrame {

	boolean isTagged;
	boolean isAddressable;
	int protocol;
	long source;
	byte[] mac;
	boolean ackRequired;
	boolean resRequired;
	int sequence;

	public FrameHeader(boolean isTagged, boolean isAddressable, int protocol, long source, byte[] mac,
			boolean ackRequired, boolean resRequired, int sequence) {
		super();
		this.isTagged = isTagged;
		this.isAddressable = isAddressable;
		this.protocol = protocol;
		this.source = source;
		this.mac = mac;
		this.ackRequired = ackRequired;
		this.resRequired = resRequired;
		this.sequence = sequence;
	}

	@Override
	public boolean isTagged() {
		return this.isTagged;
	}

	@Override
	public boolean isAddressable() {
		return this.isAddressable;
	}

	@Override
	public int getProtocol() {
		return this.protocol;
	}

	@Override
	public long getSource() {
		return this.source;
	}

	@Override
	public byte[] getMac() {
		return this.mac;
	}

	@Override
	public boolean ackRequired() {
		return this.ackRequired;
	}

	@Override
	public boolean resRequired() {
		return this.resRequired;
	}

	@Override
	public int getSequence() {
		return this.sequence;
	}

	@Override
	public int getType() {
		// Unused.
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getPayload() {
		// Unused.
		throw new UnsupportedOperationException();
	}

}
