/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx.frame;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

import sew.lifx.FrameHeader;
import sew.lifx.FrameReader;
import sew.lifx.Frames;
import sew.lifx.LIFXFrame;

public class StateService implements LIFXFrame {

	private final InetAddress address;
	private final FrameHeader header;
	private final int service;
	private final long port;

	public StateService(InetAddress address, FrameHeader header, ByteArrayInputStream inputStream, int payloadLength) {
		this.address = address;
		this.header = header;
		// System.out.println("StateService.StateService() " + payloadLength);
		assert payloadLength == 5;
		this.service = FrameReader.readUInt8(inputStream);
		this.port = FrameReader.readUInt32(inputStream);
		// System.out.println("service=" + this.service);
		// System.out.println("port=" + this.port);
	}

	@Override
	public boolean isTagged() {
		return this.header.isTagged();
	}

	@Override
	public boolean isAddressable() {
		return this.header.isAddressable();
	}

	@Override
	public long getSource() {
		return this.header.getSource();
	}

	@Override
	public byte[] getMac() {
		return this.header.getMac();
	}

	@Override
	public int getProtocol() {
		return this.header.getProtocol();
	}

	@Override
	public boolean ackRequired() {
		return this.header.ackRequired();
	}

	@Override
	public boolean resRequired() {
		return this.header.resRequired();
	}

	@Override
	public int getSequence() {
		return this.header.getSequence();
	}

	@Override
	public int getType() {
		return Frames.STATE_SERVICE;
	}

	@Override
	public byte[] getPayload() {
		return EMPTY_BYTEARRAY;
	}

}
