/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

public interface MessageListener {

	void onNewMessage(String message);

	void onDiscardMessage(String message);

}
