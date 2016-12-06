/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.philipshue;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.components.dependencyinjection.ServiceLoaderFactory;
import sew.light.LightManager;
import sew.light.util.Color;
import sew.upnp.Device;
import sun.net.www.protocol.http.HttpURLConnection;

public class DeviceManager {

	private static final String NEW_BRIDGE_MESSAGE = "New Philips Hue bridge detected.\nPlease press the button.";

	private final PhilipsHueManager manager;
	private final Map<String, PhilipsHueLight> lights;
	private final Device device;
	private String username;

	public DeviceManager(PhilipsHueManager manager, Device device) {
		this.manager = manager;
		this.device = device;
		this.lights = new HashMap<>();
		update();
	}

	public void update() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				updateSynchronous();
			}
		});
	}

	private void updateSynchronous() {
		try {
			HttpURLConnection httpURLConnection = new HttpURLConnection(new URL(
					"http://" + this.device.getAddress().getHostAddress() + "/api/" + this.username + "/lights"));
			try {
				System.out.println("Send Philips Hue lights request.");
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.connect();
				InputStream inputStream = httpURLConnection.getInputStream();
				// Cannot use content length since it returns -1…
				// byte[] content = new byte[contentLength];
				// new DataInputStream(inputStream).readFully(content);
				// String string = new String(content);
				String string = new String(readFully(inputStream));
				System.out.println("DeviceManager.updateSynchronous() " + string);
				JSONObject jsonObject = new JSONObject(string);
				JSONObject light1 = jsonObject.getJSONObject("1");
				readLight(light1, "1");
				JSONObject light2 = jsonObject.getJSONObject("2");
				readLight(light2, "2");
				// JSONObject light3 = jsonObject.getJSONObject("3");
				// readLight(light3, "3");
			} finally {
				httpURLConnection.disconnect();
			}
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			// Only known cause is that the base is not yet associated.
			launchAssociation();
		}
	}

	private void launchAssociation() {
		Timer timer = ServiceLoaderFactory.getServiceLoader().getService(Timer.class);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (associate()) {
					cancel();
					update();
				}
			}
		}, 0, 1000);
	}

	private boolean associate() {
		try {
			HttpURLConnection httpURLConnection = new HttpURLConnection(
					new URL("http://" + this.device.getAddress().getHostAddress() + "/api/"));
			try {
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestMethod("POST");
				// System.out.println("Send Philips Hue set color request.");
				httpURLConnection.connect();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
				String frame = "{\"devicetype\":\"MicroEJ device\"}";
				outputStreamWriter.write(frame);
				outputStreamWriter.flush();
				InputStream inputStream = httpURLConnection.getInputStream();
				String string = new String(readFully(inputStream));
				// System.out.println("DeviceManager.associate() " + string);
				JSONArray array = new JSONArray(string);
				JSONObject message = array.getJSONObject(0);
				try {
					JSONObject successObject = message.getJSONObject("success"); //$NON-NLS-1$
					String username = successObject.getString("username"); //$NON-NLS-1$
					this.username = username;
					LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
					lightManager.discardMessage(NEW_BRIDGE_MESSAGE);
					return true;
				} catch (JSONException e) {
					// Link button not pressed.
					LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
					lightManager.sendMessage(NEW_BRIDGE_MESSAGE);
					System.out.println(e.toString());
					// e.printStackTrace();
				}
			} finally {
				httpURLConnection.disconnect();
			}
		} catch (IOException | JSONException e) {
			System.out.println(e.toString());
			// e.printStackTrace();
		}
		return false;
	}

	public static byte[] readFully(InputStream input) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(input);
		int length = 0;
		byte[] responseBytes = new byte[0];
		while (true) {
			int b = dataInputStream.read();
			if (b == -1) {
				// end of stream
				break;
			}
			int available = input.available();
			int currentOffset = length;
			length += available + 1;
			System.arraycopy(responseBytes, 0, responseBytes = new byte[length], 0, currentOffset);
			responseBytes[currentOffset++] = (byte) b;
			dataInputStream.readFully(responseBytes, currentOffset, available);
		}

		input.close();
		return responseBytes;
	}

	public void updateLight(String id, boolean on, Color color) throws IOException {
		System.out.println("DeviceManager.updateLight()");
		HttpURLConnection httpURLConnection = new HttpURLConnection(new URL("http://"
				+ this.device.getAddress().getHostAddress() + "/api/" + this.username + "/lights/" + id + "/state"));
		try {
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("PUT");
			// System.out.println("Send Philips Hue set color request.");
			httpURLConnection.connect();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
			String frame = "{\"on\":" + Boolean.toString(on) + ", \"sat\":" + (int) (color.getSaturation() * 254)
					+ ", \"bri\":" + (int) (color.getValue() * 254) + ",\"hue\":" + (int) (color.getHue() * 65535 / 360)
					+ "}";
			outputStreamWriter.write(frame);
			outputStreamWriter.flush();
			InputStream inputStream = httpURLConnection.getInputStream();
			// System.out.println("Philips Hue set color request sent.");
			// String string = new String(StreamUtilities.readFully(inputStream));
			// System.out.println(
			// "DeviceManager.updateLightColor() " + color.getHue() + " REQ\n" + frame /* + "\nREP\n" + string */);
		} finally {
			httpURLConnection.disconnect();
		}
	}

	public void readLight(JSONObject lightObject, String id) throws JSONException {
		String name = lightObject.getString("name");
		JSONObject state = lightObject.getJSONObject("state");
		float hue = (float) Integer.parseInt(state.getString("hue")) * 360 / 65535;
		float saturation = (float) Integer.parseInt(state.getString("sat")) / 255;
		float brightness = (float) Integer.parseInt(state.getString("bri")) / 255;
		boolean on = Boolean.parseBoolean(state.getString("on"));
		PhilipsHueLight philipsHueLight = this.lights.get(id);
		System.out.println("DeviceManager.readLight() " + id + " " + hue + " " + saturation + " " + brightness);
		if (philipsHueLight == null) {
			philipsHueLight = new PhilipsHueLight(id, name, this);
			this.lights.put(name, philipsHueLight);
		}
		philipsHueLight.simpleUpdate(new Color(hue, saturation, brightness), 10000, on);
		LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
		// if (on) {
		lightManager.addLight(philipsHueLight);
		// } else {
		// lightManager.removeLight(philipsHueLight);
		// }
	}

}
