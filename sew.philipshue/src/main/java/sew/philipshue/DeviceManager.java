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

import org.json.me.JSONException;
import org.json.me.JSONObject;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import sew.light.LightManager;
import sew.light.util.Color;
import sew.upnp.Device;
import sun.net.www.protocol.http.HttpURLConnection;

public class DeviceManager {

	private final PhilipsHueManager manager;
	private final Map<String, PhilipsHueLight> lights;
	private final Device device;

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
			HttpURLConnection httpURLConnection = new HttpURLConnection(new URL("http://"
					+ this.device.getAddress().getHostAddress() + "/api/" + UserInformation.USERNAME + "/lights"));
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
				JSONObject jsonObject = new JSONObject(string);
				JSONObject light1 = jsonObject.getJSONObject("1");
				readLight(light1, "1");
				JSONObject light2 = jsonObject.getJSONObject("2");
				readLight(light2, "2");
				JSONObject light3 = jsonObject.getJSONObject("3");
				readLight(light3, "3");
				System.out.println("Philips Hue lights request sent.");
			} finally {
				httpURLConnection.disconnect();
			}
		} catch (IOException | JSONException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
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

	public void updateLightColor(String id, Color color) throws IOException {
		HttpURLConnection httpURLConnection = new HttpURLConnection(
				new URL("http://" + this.device.getAddress().getHostAddress() + "/api/" + UserInformation.USERNAME
						+ "/lights/" + id + "/state"));
		try {
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("PUT");
			// System.out.println("Send Philips Hue set color request.");
			httpURLConnection.connect();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
			String frame = "{\"on\":true, \"sat\":" + (int) (color.getSaturation() * 254) + ", \"bri\":"
					+ (int) (color.getValue() * 254) + ",\"hue\":" + (int) (color.getHue() * 65535 / 360) + "}";
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
		if (philipsHueLight == null) {
			philipsHueLight = new PhilipsHueLight(id, name, this);
			this.lights.put(name, philipsHueLight);
		}
		philipsHueLight.simpleUpdate(new Color(hue, saturation, brightness), 10000);
		LightManager lightManager = ServiceLoaderFactory.getServiceLoader().getService(LightManager.class);
		if (on) {
			lightManager.addLight(philipsHueLight);
		} else {
			lightManager.removeLight(philipsHueLight);
		}
	}

}
