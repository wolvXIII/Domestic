/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

public interface LIFXFrame {

	byte[] EMPTY_BYTEARRAY = new byte[0];
	byte[] EMPTY_MAC = new byte[] { 0, 0, 0, 0, 0, 0 };

	boolean isTagged();

	boolean isAddressable();

	int getProtocol();

	long getSource();

	byte[] getMac();

	boolean ackRequired();

	boolean resRequired();

	int getSequence();

	int getType();

	byte[] getPayload();

}
