package de.owlhq.remotebox.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import de.owlhq.remotebox.data.AudioInfo;
import de.owlhq.remotebox.device.BlinkDevice;

public class AudioControl {
	
	private static final String AUDIO_ENDPOINT = "/audio";
	
	private BlinkDevice device = null;

	public AudioControl(BlinkDevice device) {
		super();
		this.device = device;
	}
	
	private JsonObject callEndpoint(String urlString, String method) {
		String data = null;
		JsonObject json = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method);
			BufferedReader br = null;
			if (100 <= con.getResponseCode() && con.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} 
			else {
			    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			char[] buff = new char[con.getContentLengthLong() >= 0 ? con.getContentLength() : 24576];
			int read = br.read(buff);
			buff = Arrays.copyOf(buff, read);
			data = new String(buff);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (data != null) {
			System.out.println(data);
			JsonParser parser = new JsonParser();
			try {
				json = (JsonObject) parser.parse(data);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public List<String> getAudioFiles() {
		List<String> audioFiles = new LinkedList<>();
		System.out.println(device.getUrlRoot() + AUDIO_ENDPOINT);
		JsonObject json = callEndpoint(device.getUrlRoot() + AUDIO_ENDPOINT, "GET");
		if (json != null && json.get("audio") != null) {
			try {
				AudioInfo aInfo = new Gson().fromJson(json.get("audio"), AudioInfo.class);
				audioFiles.addAll(aInfo.getRandom_playback().getList());
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
		return audioFiles;
	}
	
	public boolean playAudio(String name) {
		return true;
	}

}
