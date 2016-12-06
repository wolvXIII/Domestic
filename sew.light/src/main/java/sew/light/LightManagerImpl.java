/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

import java.util.ArrayList;
import java.util.Collection;

public class LightManagerImpl implements LightManager {

	private final Collection<Light> lights;
	private final Collection<LightsListener> lightsListeners;
	private final Collection<MessageListener> messageListeners;

	public LightManagerImpl() {
		this.lights = new ArrayList<>();
		this.lightsListeners = new ArrayList<>();
		this.messageListeners = new ArrayList<>();
	}

	@Override
	public void addLightsListener(LightsListener lightsListener) {
		this.lightsListeners.add(lightsListener);
	}

	@Override
	public void removeLightsListener(LightsListener lightsListener) {
		this.lightsListeners.remove(lightsListener);
	}

	@Override
	public void addLight(Light light) {
		if (!this.lights.contains(light)) {
			this.lights.add(light);
			for (LightsListener lightsListener : this.lightsListeners) {
				lightsListener.onAddLight(light);
			}
		}
	}

	@Override
	public void removeLight(Light light) {
		if (this.lights.remove(light)) {
			for (LightsListener lightsListener : this.lightsListeners) {
				lightsListener.onRemoveLight(light);
			}
		}
	}

	@Override
	public Iterable<Light> getLights() {
		return this.lights;
	}

	@Override
	public void addMessageListener(MessageListener messageListener) {
		this.messageListeners.add(messageListener);
	}

	@Override
	public void removeMessageListener(MessageListener messageListener) {
		this.messageListeners.remove(messageListener);
	}

	@Override
	public void sendMessage(String message) {
		for (MessageListener messageListener : this.messageListeners) {
			messageListener.onNewMessage(message);
		}
	}

	@Override
	public void discardMessage(String message) {
		for (MessageListener messageListener : this.messageListeners) {
			messageListener.onDiscardMessage(message);
		}
	}

}
