/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sew.sonos;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.concurrent.Executor;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import sew.upnp.Device;
import sun.net.www.protocol.http.HttpURLConnection;

public class SonosEntity {

	public static final String URN_AV_TRANSPORT = "urn:schemas-upnp-org:service:AVTransport:1";
	public static final String URN_RENDERING_CONTROL = "urn:schemas-upnp-org:service:RenderingControl:1";

	private static final String S_ENVELOPE = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">";
	private static final String S_BODY = "<s:Body>";
	private static final String S_ENVELOPE_E = "</s:Envelope>";
	private static final String S_BODY_E = "</s:Body>";
	// TODO Manage groups.
	private final Device device;

	private int volume;

	public SonosEntity(Device device) {
		this.device = device;
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				updateVolume();
			}
		});
	}

	public void play() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				playSync();
			}
		});
	}

	public void pause() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				pauseSync();
			}
		});
	}

	public void setVolume(final int volume) {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				setVolumeSync(volume);
				updateVolume();
			}
		});
	}

	public int getVolume() {
		return this.volume;
	}

	private void playSync() {
		sendCommand("MediaRenderer/AVTransport/Control", "Play", URN_AV_TRANSPORT,
				"<InstanceID>0</InstanceID><Speed>1</Speed>", null);
	}

	private void pauseSync() {
		sendCommand("MediaRenderer/AVTransport/Control", "Pause", URN_AV_TRANSPORT,
				"<InstanceID>0</InstanceID><Speed>1</Speed>", null);
	}

	private void setVolumeSync(int volume) {
		sendCommand("MediaRenderer/RenderingControl/Control", "SetVolume", URN_RENDERING_CONTROL,
				"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredVolume>" + volume + "</DesiredVolume>",
				null);
	}

	private void updateVolume() {
		this.volume = getVolumeSync();
	}

	private int getVolumeSync() {
		String result = sendCommand("MediaRenderer/RenderingControl/Control", "GetVolume", URN_RENDERING_CONTROL,
				"<InstanceID>0</InstanceID><Channel>Master</Channel>", "CurrentVolume");
		return Integer.parseInt(result);
	}

	private String sendCommand(String url, String action, String service, String arguments, String xmlResult) {
		try {
			HttpURLConnection connection = new HttpURLConnection(new URL(getAVTransportURL(url)));
			connection.setRequestMethod("POST");
			connection.addRequestProperty("SOAPACTION", service + "#" + action);
			connection.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(S_ENVELOPE);
			outputStreamWriter.write(S_BODY);
			outputStreamWriter.write("<u:");
			outputStreamWriter.write(action);
			outputStreamWriter.write(" xmlns:u=\"");
			outputStreamWriter.write(service);
			outputStreamWriter.write("\">");
			outputStreamWriter.write(arguments);
			outputStreamWriter.write(S_BODY_E);
			outputStreamWriter.write(S_ENVELOPE_E);
			outputStreamWriter.flush();
			connection.connect();
			DataInputStream inputStream = new DataInputStream(connection.getInputStream());
			if (xmlResult != null) {
				byte[] bytes = new byte[connection.getContentLength()];
				inputStream.readFully(bytes);
				String result = new String(bytes);
				String startTag = '<' + xmlResult + '>';
				String endTag = "</" + xmlResult + '>';
				int indexOfStartTag = result.indexOf(startTag, 0);
				int indexOfEndTag = result.indexOf(endTag, indexOfStartTag);
				return result.substring(indexOfStartTag + startTag.length(), indexOfEndTag);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getAVTransportURL(String url) {
		return "http:/" + this.device.getAddress() + ":1400/" + url;
	}

	// /**
	// * Set volume value (0-100)
	// */
	// public function SetVolume($volume)
	// {
	// $url = '/MediaRenderer/RenderingControl/Control';
	// $action = 'SetVolume';
	// $service = 'urn:schemas-upnp-org:service:RenderingControl:1';
	// $args = '<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredVolume>'.$volume.'</DesiredVolume>';
	// return $this->Upnp($url,$service,$action,$args);
	// }
	//
	// /**
	// * Play
	// */
	// public function Play()
	// {
	// $url = '/MediaRenderer/AVTransport/Control';
	// $action = 'Play';
	// $service = 'urn:schemas-upnp-org:service:AVTransport:1';
	// $args = '<InstanceID>0</InstanceID><Speed>1</Speed>';
	// return $this->Upnp($url,$service,$action,$args);
	// }
}
