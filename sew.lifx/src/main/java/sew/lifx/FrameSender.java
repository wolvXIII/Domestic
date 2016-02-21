/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executor;

import ej.components.dependencyinjection.ServiceLoaderFactory;

/**
 *
 */
public class FrameSender {

	private final DatagramSocket socket;

	public FrameSender(DatagramSocket socket) {
		this.socket = socket;
	}

	public void send(final InetAddress address, final int port, final LIFXFrame lifxFrame) {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				boolean tagged = lifxFrame.isTagged();
				boolean ackRequired = lifxFrame.ackRequired();
				boolean resRequired = lifxFrame.resRequired();
				int type = lifxFrame.getType();
				byte[] payload = lifxFrame.getPayload();

				int length = FrameHelper.FRAME_LENGTH + FrameHelper.FRAMEADDRESS_LENGTH
						+ FrameHelper.PROTOCOLHEADER_LENGTH + payload.length;
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// Frame.
				writeUInt16(stream, length); // Length.
				writeProtocol(stream, tagged); // Protocol.
				writeUInt32(stream, 0);// Source (useless for client).
				// System.out.println("Frame:" + stream.size());

				// Frame address.
				writeUInt64(stream, 0, 0);// Target (MAC).
				for (int i = -1; ++i < 6;) { // Reserved.
					writeUInt8(stream, 0);
				}
				writeAckStateFlags(stream, ackRequired, resRequired); // Ack / Res
				writeUInt8(stream, 0); // Sequence.

				// Protocol header.
				writeUInt64(stream, 0, 0);// Reserved.
				writeUInt16(stream, type);// Type.
				writeUInt16(stream, 0);// Reserved.

				// Payload.
				try {
					stream.write(payload);

					byte[] frame = stream.toByteArray();

					System.out.println("Send " + lifxFrame.getClass().getSimpleName() + ".");
					DatagramPacket packet = new DatagramPacket(frame, frame.length, address, port);
					FrameSender.this.socket.send(packet);
					System.out.println(lifxFrame.getClass().getSimpleName() + " sent.");
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
				}
			}
		});
	}

	public static void writeUInt64(ByteArrayOutputStream stream, long value1, long value2) {
		writeUInt32(stream, value1);
		writeUInt32(stream, value2);
	}

	public static void writeUInt32(ByteArrayOutputStream stream, long value) {
		stream.write((byte) (value & 0xff));
		stream.write((byte) ((value >> 8) & 0xff));
		stream.write((byte) ((value >> 16) & 0xff));
		stream.write((byte) ((value >> 24) & 0xff));
	}

	public static void writeUInt16(ByteArrayOutputStream stream, int value) {
		stream.write((byte) (value & 0xff));
		stream.write((byte) ((value >> 8) & 0xff));
	}

	public static void writeUInt8(ByteArrayOutputStream stream, int value) {
		stream.write((byte) (value & 0xff));
	}

	public static void writeProtocol(ByteArrayOutputStream stream, boolean isTagged) {
		// 12bits: 1024, 1bit addressable, 1bit tagged.
		int protocol = 1024 | (0x1 << FrameHelper.ADDRESSABLE_SHIFT)
				| (isTagged ? (0x1 << FrameHelper.TAGGED_SHIFT) : 0x0);
		writeUInt16(stream, protocol);
	}

	public static void writeAckStateFlags(ByteArrayOutputStream stream, boolean ackRequired, boolean resRequired) {
		// 6bits: 0, 1bit ack_required, 1bit res_required.
		int ackState = (ackRequired ? (0x1 << FrameHelper.ACK_SHIFT) : 0x0)
				| (resRequired ? (0x1 << FrameHelper.RES_SHIFT) : 0x0);
		writeUInt8(stream, ackState);
	}

}
