/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sew.sonos;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.concurrent.Executor;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import sew.upnp.Device;
import sun.net.www.protocol.http.HttpURLConnection;

public class SonosEntity {

	private static final String URL_AV_TRANSPORT = "MediaRenderer/AVTransport/Control";
	private static final String URL_CONTENT_DIRECTORY = "MediaServer/ContentDirectory/Control";
	private static final String URL_RENDERING_CONTROL = "MediaRenderer/RenderingControl/Control";
	public static final String URN_AV_TRANSPORT = "urn:schemas-upnp-org:service:AVTransport:1";
	public static final String URN_RENDERING_CONTROL = "urn:schemas-upnp-org:service:RenderingControl:1";
	public static final String URN_CONTENT_DIRECTORY = "urn:schemas-upnp-org:service:ContentDirectory:1";

	private static final String S_ENVELOPE = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">";
	private static final String S_BODY = "<s:Body>";
	private static final String S_ENVELOPE_E = "</s:Envelope>";
	private static final String S_BODY_E = "</s:Body>";
	// TODO Manage groups.
	private final Device device;

	private int volume;
	private boolean mute;
	private String trackArtist;
	private String trackAlbum;
	private String trackTitle;
	private String trackRelTime; // TODO Convert it into seconds and make it progress.
	private String trackDuration;
	private String type;

	public SonosEntity(Device device) {
		this.device = device;
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				updateVolume();
				updateMute();
				updateTrackSync();
				updateQueue();
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
		updateTrack();
	}

	public void pause() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				pauseSync();
			}
		});
		updateTrack();
	}

	public void previous() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				previousSync();
			}
		});
		updateTrack();
	}

	public void next() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				nextSync();
			}
		});
		updateTrack();
	}

	/**
	 * @param position
	 *            format: "xx:xx:xx".
	 */
	public void seek(final String position) {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				seekSync(position);
			}
		});
		updateTrack();
	}

	private void updateTrack() {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				updateTrackSync();
			}
		});
	}

	/**
	 * Gets the trackArtist.
	 *
	 * @return the trackArtist.
	 */
	public String getTrackArtist() {
		return this.trackArtist;
	}

	/**
	 * Gets the trackAlbum.
	 *
	 * @return the trackAlbum.
	 */
	public String getTrackAlbum() {
		return this.trackAlbum;
	}

	/**
	 * Gets the trackTitle.
	 *
	 * @return the trackTitle.
	 */
	public String getTrackTitle() {
		return this.trackTitle;
	}

	/**
	 * Gets the trackRelTime.
	 *
	 * @return the trackRelTime.
	 */
	public String getTrackRelTime() {
		return this.trackRelTime;
	}

	/**
	 * Gets the trackDuration.
	 *
	 * @return the trackDuration.
	 */
	public String getTrackDuration() {
		return this.trackDuration;
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

	public void setMute(final boolean mute) {
		Executor executor = ServiceLoaderFactory.getServiceLoader().getService(Executor.class);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				setMuteSync(mute);
				updateMute();
			}
		});
	}

	public boolean isMute() {
		return this.mute;
	}

	private void playSync() {
		sendCommand(URL_AV_TRANSPORT, "Play", URN_AV_TRANSPORT, "<InstanceID>0</InstanceID><Speed>1</Speed>", null);
	}

	private void pauseSync() {
		sendCommand(URL_AV_TRANSPORT, "Pause", URN_AV_TRANSPORT, "<InstanceID>0</InstanceID>", null);
	}

	private void previousSync() {
		sendCommand(URL_AV_TRANSPORT, "Previous", URN_AV_TRANSPORT, "<InstanceID>0</InstanceID>", null);
	}

	private void nextSync() {
		sendCommand(URL_AV_TRANSPORT, "Next", URN_AV_TRANSPORT, "<InstanceID>0</InstanceID>", null);
	}

	private void seekSync(String position) {
		sendCommand(URL_AV_TRANSPORT, "Seek", URN_AV_TRANSPORT,
				"<InstanceID>0</InstanceID><Unit>REL_TIME</Unit><Target>" + position + "</Target>", null);
	}

	private void updateTrackSync() {
		String result = sendCommand(URL_AV_TRANSPORT, "GetPositionInfo", URN_AV_TRANSPORT, "<InstanceID>0</InstanceID>",
				"u:GetPositionInfoResponse");
		// System.out.println(result);
		// System.out.println("Track " + getTag("Track", result));
		// System.out.println("TrackURI " + getTag("TrackURI", result));
		// System.out.println("TrackDuration " + getTag("TrackDuration", result));
		// System.out.println("RelTime " + getTag("RelTime", result));
		// System.out.println("TrackMetaData " + getTag("TrackMetaData", result));
		// System.out.println("Title " + getInnerTag("dc:title", result));
		// System.out.println("AlbumArtist " + getInnerTag("r:albumArtist", result));
		// System.out.println("Album " + getInnerTag("upnp:album", result));
		// System.out.println("Artist " + getInnerTag("dc:creator", result));
		this.trackArtist = getInnerTag("dc:creator", result);
		this.trackAlbum = getInnerTag("upnp:album", result);
		this.trackTitle = getInnerTag("dc:title", result);
		this.trackRelTime = getInnerTag("RelTime", result);
		this.trackDuration = getInnerTag("TrackDuration", result);
		this.type = getInnerTag("upnp:class", result);
		System.out.println("Type: " + this.type);
		String albumArt = getInnerTag("upnp:albumArtURI", result);
		albumArt = albumArt.replace("&lt;", "<");
		albumArt = albumArt.replace("&gt;", ">");
		albumArt = albumArt.replace("&quot;", "\"");
		albumArt = albumArt.replace("&amp;", "&");
		albumArt = albumArt.replace("%3a", ":");
		albumArt = albumArt.replace("%2f", "/");
		albumArt = albumArt.replace("%25", "%");
		System.out.println("SonosEntity.updateTrackSync() " + albumArt);
	}

	private void updateQueue() {
		String result = sendCommand(URL_CONTENT_DIRECTORY, "Browse", URN_CONTENT_DIRECTORY,
				"<ObjectID>Q:0</ObjectID><BrowseFlag>BrowseDirectChildren</BrowseFlag><Filter>dc:title,res,dc:creator,upnp:artist,upnp:album,upnp:albumArtURI</Filter><StartingIndex>0</StartingIndex><RequestedCount>10</RequestedCount><SortCriteria></SortCriteria>",
				"s:Envelope");
		result = result.replace("&lt;", "<");
		result = result.replace("&gt;", ">");
		result = result.replace("&quot;", "\"");
		result = result.replace("&amp;", "&");
		result = result.replace("%3a", ":");
		result = result.replace("%2f", "/");
		result = result.replace("%25", "%");
		System.out.println("SonosEntity.updateQueue() \n" + result);
	}

	private void setVolumeSync(int volume) {
		sendCommand(URL_RENDERING_CONTROL, "SetVolume", URN_RENDERING_CONTROL,
				"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredVolume>" + volume + "</DesiredVolume>",
				null);
	}

	private void updateVolume() {
		this.volume = getVolumeSync();
	}

	private int getVolumeSync() {
		String result = sendCommand(URL_RENDERING_CONTROL, "GetVolume", URN_RENDERING_CONTROL,
				"<InstanceID>0</InstanceID><Channel>Master</Channel>", "CurrentVolume");
		return Integer.parseInt(result);
	}

	private void setMuteSync(boolean mute) {
		sendCommand(URL_RENDERING_CONTROL, "SetMute", URN_RENDERING_CONTROL,
				"<InstanceID>0</InstanceID><Channel>Master</Channel><DesiredMute>" + mute + "</DesiredMute>", null);
	}

	private void updateMute() {
		this.mute = getMuteSync();
	}

	private boolean getMuteSync() {
		String result = sendCommand(URL_RENDERING_CONTROL, "GetMute", URN_RENDERING_CONTROL,
				"<InstanceID>0</InstanceID><Channel>Master</Channel>", "CurrentMute");
		return Integer.parseInt(result) != 0;
	}

	private String sendCommand(String url, String action, String service, String arguments, String xmlTag) {
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
			if (xmlTag != null) {
				byte[] bytes = readFully(inputStream);
				String xml = new String(bytes);
				System.out.println(xml);
				return getTag(xmlTag, xml);
			} else {
				byte[] bytes = new byte[connection.getContentLength()];
				inputStream.readFully(bytes);
				String result = new String(bytes);
				System.out.println(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static byte[] readFully(InputStream input) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(input);
		int length = 0;
		byte[] responseBytes = new byte[0];
		int b;
		while (true) {
			try {
				b = dataInputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
				// FIXME end of stream?!
				break;
			}
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
			// while (available > 0) {
			// int nbRead = input.read(responseBytes, currentOffset, available);
			// if (nbRead == -1) {
			// break;
			// }
			// available -= nbRead;
			// }
		}

		input.close();
		return responseBytes;
	}

	private String getTag(String xmlTag, String xml) {
		return getTag(xmlTag, xml, "<", ">");
	}

	private String getInnerTag(String xmlTag, String xml) {
		return getTag(xmlTag, xml, "&lt;", "&gt;");
	}

	private String getTag(String xmlTag, String xml, String openTag, String closeTag) {
		String startTag = openTag + xmlTag + closeTag;
		String endTag = openTag + '/' + xmlTag + closeTag;
		int indexOfStartTag = xml.indexOf(startTag, 0);
		int indexOfCloseStartTag;
		if (indexOfStartTag == -1) {
			startTag = openTag + xmlTag + " ";
			indexOfStartTag = xml.indexOf(startTag, 0);
			indexOfCloseStartTag = xml.indexOf(closeTag, indexOfStartTag);
		} else {
			indexOfCloseStartTag = indexOfStartTag + openTag.length() + xmlTag.length();
		}
		if (indexOfStartTag != -1) {
			int indexOfEndTag = xml.indexOf(endTag, indexOfCloseStartTag);
			if (indexOfEndTag != -1) {
				return xml.substring(indexOfCloseStartTag + closeTag.length(), indexOfEndTag);
			}
		}
		return "";
	}

	private String getAVTransportURL(String url) {
		return "http:/" + this.device.getAddress() + ":1400/" + url;
	}

}
