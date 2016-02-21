/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx.frame;

import java.io.ByteArrayOutputStream;

import sew.lifx.FrameHelper;
import sew.lifx.FrameSender;
import sew.lifx.Frames;
import sew.lifx.LIFXFrame;
import sew.light.Light;
import sew.light.util.Color;

public class LightSetColor implements LIFXFrame {

	private final Color color;
	private final int intensity;
	private final long duration;

	public LightSetColor(Color color, int intensity, long duration) {
		this.color = color;
		this.intensity = intensity;
		this.duration = duration;
	}

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
		return Frames.LIGHT_SET_COLOR;
	}

	@Override
	public byte[] getPayload() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		FrameSender.writeUInt8(stream, 0);
		FrameSender.writeUInt16(stream, (int) (this.color.getHue() * FrameHelper.MAX_U16 / 360f));
		FrameSender.writeUInt16(stream, (int) (this.color.getSaturation() * FrameHelper.MAX_U16));
		FrameSender.writeUInt16(stream, (int) (this.color.getValue() * FrameHelper.MAX_U16));
		FrameSender.writeUInt16(stream, this.intensity * FrameHelper.MAX_U16 / Light.MAX_INTENSITY);
		FrameSender.writeUInt32(stream, this.duration);
		return stream.toByteArray();
	}
}
