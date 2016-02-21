/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.lifx;

public interface FrameHelper {

	int ACK_SHIFT = 1;
	int RES_SHIFT = 0;
	int PROTOCOL_MASK = 0xfff;
	int ADDRESSABLE_SHIFT = 12;
	int TAGGED_SHIFT = 13;
	int MAX_U16 = 0xffff;

	int PROTOCOL = 1024;

	int FRAME_LENGTH = 8;
	int FRAMEADDRESS_LENGTH = 16;
	int PROTOCOLHEADER_LENGTH = 12;

}
