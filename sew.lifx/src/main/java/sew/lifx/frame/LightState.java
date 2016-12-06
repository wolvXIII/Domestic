/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx.frame;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

import sew.lifx.FrameHeader;
import sew.lifx.FrameHelper;
import sew.lifx.FrameReader;
import sew.lifx.Frames;
import sew.lifx.LIFXFrame;
import sew.light.util.Color;

public class LightState implements LIFXFrame {

	private final InetAddress address;
	private final FrameHeader header;
	private final Color color;
	private final int power;
	private final String label;
	private final int kelvin;

	public LightState(InetAddress address, FrameHeader header, ByteArrayInputStream inputStream, int payloadLength) {
		this.address = address;
		this.header = header;
		// System.out.println("LightState.LightState() " + payloadLength);
		assert payloadLength == 52;
		FrameReader.readUInt16(inputStream); // Useless?
		float hue = FrameReader.readUInt16(inputStream) * 360f / FrameHelper.MAX_U16;
		float saturation = (float) FrameReader.readUInt16(inputStream) / FrameHelper.MAX_U16;
		float value = (float) FrameReader.readUInt16(inputStream) / FrameHelper.MAX_U16;
		this.kelvin = FrameReader.readUInt16(inputStream);
		this.color = new Color(hue, saturation, value);
		FrameReader.readUInt16(inputStream); // Reserved.
		this.power = FrameReader.readUInt16(inputStream);
		byte[] labelBytes = new byte[32];
		// // Skip first two bytes…
		// FrameReader.readUInt8(inputStream);
		// FrameReader.readUInt8(inputStream);
		int labelLength;
		for (labelLength = -1; ++labelLength < labelBytes.length;) {
			byte b = (byte) FrameReader.readUInt8(inputStream);
			if (b == '\0') {
				break;
			}
			labelBytes[labelLength] = b;
		}
		this.label = new String(labelBytes, 0, labelLength);
		System.out.println("hue=" + hue);
		System.out.println("saturation=" + saturation);
		System.out.println("value=" + value);
		System.out.println("kelvin=" + this.kelvin);
		System.out.println("power=" + this.power);
		// System.out.println("label=" + this.label + " " + this.label.length());
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
	 * Gets the color.
	 *
	 * @return the color.
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Gets the kelvin.
	 *
	 * @return the kelvin.
	 */
	public int getKelvin() {
		return this.kelvin;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Gets the power.
	 *
	 * @return the power.
	 */
	public int getPower() {
		return this.power;
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
		return Frames.LIGHT_STATE;
	}

	@Override
	public byte[] getPayload() {
		return EMPTY_BYTEARRAY;
	}

}
