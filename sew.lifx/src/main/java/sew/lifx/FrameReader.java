/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import sew.lifx.frame.LightGet;
import sew.lifx.frame.LightState;
import sew.lifx.frame.StateService;

public class FrameReader {

	private final LIFXManager lifxManager;

	public FrameReader(LIFXManager lifxManager) {
		this.lifxManager = lifxManager;
	}

	public void receive(DatagramSocket socket) throws IOException {
		while (true) {
			byte[] frame = new byte[256];
			System.out.println("Wait for it…");
			DatagramPacket packet = new DatagramPacket(frame, frame.length);
			socket.receive(packet);
			InetAddress address = packet.getAddress();
			// System.out.println("** Received from " + address + ":");
			// for (byte b : frame) {
			// System.out.print(Integer.toHexString(b) + " ");
			// }
			// System.out.println();

			ByteArrayInputStream inputStream = new ByteArrayInputStream(frame);

			// Frame.
			int length = readUInt16(inputStream);
			// System.out.println("length=" + length);
			int protocol = readUInt16(inputStream);
			boolean addressable = ((protocol >> FrameHelper.ADDRESSABLE_SHIFT) & 0x1) == 0x1;
			boolean tagged = ((protocol >> FrameHelper.TAGGED_SHIFT) & 0x1) == 0x1;
			protocol &= FrameHelper.PROTOCOL_MASK;
			// System.out.println("protocal=" + protocol);
			// System.out.println("addressable=" + addressable);
			// System.out.println("tagged=" + tagged);
			long source = readUInt32(inputStream);
			// System.out.println("source=" + source);

			// Frame address.
			byte[] mac = new byte[6];
			mac[0] = (byte) readUInt8(inputStream);
			mac[1] = (byte) readUInt8(inputStream);
			mac[2] = (byte) readUInt8(inputStream);
			mac[3] = (byte) readUInt8(inputStream);
			mac[4] = (byte) readUInt8(inputStream);
			mac[5] = (byte) readUInt8(inputStream);
			readUInt8(inputStream);// Spare.
			readUInt8(inputStream);// Spare.

			for (int i = -1; ++i < 6;) {// Reserved.
				readUInt8(inputStream);
			}
			int ackRes = readUInt8(inputStream);
			boolean ackRequired = ((ackRes >> FrameHelper.ACK_SHIFT) & 0x1) == 0x1;
			boolean resRequired = ((ackRes >> FrameHelper.RES_SHIFT) & 0x1) == 0x1;
			// System.out.println("ackRequired=" + ackRequired);
			// System.out.println("resRequired=" + resRequired);
			int sequence = readUInt8(inputStream);
			// System.out.println("sequence=" + sequence);

			// Protocol header.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			readUInt8(inputStream);// Reserved.
			int type = readUInt16(inputStream);
			// System.out.println("type=" + type);

			// System.out.println();
			int payloadLength = length
					- (FrameHelper.FRAME_LENGTH + FrameHelper.FRAMEADDRESS_LENGTH + FrameHelper.PROTOCOLHEADER_LENGTH);
			FrameHeader frameHeader = new FrameHeader(tagged, addressable, protocol, source, mac, ackRequired,
					resRequired, sequence);
			createFrame(address, type, frameHeader, inputStream, payloadLength);
		}
	}

	private void createFrame(InetAddress address, int type, FrameHeader frameHeader, ByteArrayInputStream inputStream,
			int payloadLength) {
		switch (type) {
		case Frames.STATE_SERVICE:
			StateService stateService = new StateService(address, frameHeader, inputStream, payloadLength);
			System.out.println("Received state service from " + address);
			this.lifxManager.getFrameSender().send(address, LIFXManager.PORT, new LightGet());
			break;
		case Frames.LIGHT_STATE:
			System.out.println("Received light state from " + address);
			LightState lightState = new LightState(address, frameHeader, inputStream, payloadLength);
			LIFXLight light = this.lifxManager.getLight(lightState.getLabel(), address);
			light.simpleUpdate(lightState.getColor(), lightState.getKelvin(), lightState.getPower() == 65535);
			break;

		default:
			break;
		}
	}

	public static long readUInt32(ByteArrayInputStream stream) {
		return stream.read() | (stream.read() << 8) | (stream.read() << 16) | (stream.read() << 24);
	}

	public static int readUInt16(ByteArrayInputStream stream) {
		return stream.read() | (stream.read() << 8);
	}

	public static int readUInt8(ByteArrayInputStream stream) {
		return stream.read();
	}

}
