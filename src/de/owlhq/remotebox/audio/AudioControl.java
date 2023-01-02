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

import de.owlhq.remotebox.data.info.AudioInfo;
import de.owlhq.remotebox.device.RtBoxDevice;

public class AudioControl {
	
	private static final String AUDIO_ENDPOINT = "/audio";
	
	private RtBoxDevice device = null;

	public AudioControl(RtBoxDevice device) {
		super();
		this.device = device;
	}
	
	public List<String> getAudioFiles() {
		List<String> audioFiles = new LinkedList<>();
		System.out.println(device.getUrlRoot() + AUDIO_ENDPOINT);
		JsonObject json = this.device.callEndpoint(device.getUrlRoot() + AUDIO_ENDPOINT, "GET");
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
