/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.light;

public interface LightManager {

	void addLightsListener(LightsListener lightsListener);

	void removeLightsListener(LightsListener lightsListener);

	void addLight(Light light);

	void removeLight(Light light);

	Iterable<Light> getLights();

}
