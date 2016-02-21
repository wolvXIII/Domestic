/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx.frame;

import sew.lifx.FrameHelper;
import sew.lifx.Frames;
import sew.lifx.LIFXFrame;

public class GetService implements LIFXFrame {

	@Override
	public boolean isTagged() {
		return true;
	}

	@Override
	public boolean isAddressable() {
		return false;
	}

	@Override
	public int getProtocol() {
		return FrameHelper.PROTOCOL;
	}

	@Override
	public long getSource() {
		return 0;
	}

	@Override
	public byte[] getMac() {
		return EMPTY_MAC;
	}

	@Override
	public boolean ackRequired() {
		return false;
	}

	@Override
	public boolean resRequired() {
		return false;
	}

	@Override
	public int getSequence() {
		return 0;
	}

	@Override
	public int getType() {
		return Frames.GET_SERVICE;
	}

	@Override
	public byte[] getPayload() {
		return EMPTY_BYTEARRAY;
	}

}
