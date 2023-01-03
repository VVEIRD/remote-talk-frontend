package de.owlhq.remotebox.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.vmichalak.protocol.ssdp.Device;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.data.info.RtBoxInfo;

public class RtBoxDevice {

	private String deviceName  = null;
	private String urlRoot     = null;
	private String ip          = null;
	private String serviceType = null;
	
	private transient RtBoxInfo lastStatus = null;
	
	public RtBoxDevice(Device ssdpDevice) {
		this.deviceName = ssdpDevice.getUSN();
		this.urlRoot = ssdpDevice.getDescriptionUrl();
		this.ip = ssdpDevice.getIPAddress();
		this.serviceType = ssdpDevice.getServiceType();
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getUrlRoot() {
		return urlRoot;
	}

	public void setUrlRoot(String urlRoot) {
		this.urlRoot = urlRoot;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	public boolean isSelectedDevice() {
		return BlinkApp.getSelectedDevice() == this;
	}
	
	public RtBoxInfo getLastStatus() {
		return lastStatus;
	}
	
	public JsonObject callEndpoint(String urlString, String method) {
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
		} catch (ProtocolException e) {
		} catch (IOException e) {
		}
		if (data != null) {
			JsonParser parser = new JsonParser();
			try {
				json = (JsonObject) parser.parse(data);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	private boolean callSubroutine(String url, String expectedStatus) {
		JsonObject json = this.callEndpoint(url, "GET");
		if (json.get("status") != null && json.get("status").isJsonPrimitive()) {
			try {
				String status = json.get("status").getAsString();
				return expectedStatus.equalsIgnoreCase(status);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public RtBoxInfo getStatus() {
		JsonObject json = this.callEndpoint(this.urlRoot, "GET");
		RtBoxInfo rt = null;
		if (json != null) {
			try {
				rt = new Gson().fromJson(json, RtBoxInfo.class);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
		this.lastStatus = rt;
		return rt;
	}

	public List<String> getBlinkAnimationList() {
		RtBoxInfo rt = getStatus();
		if (rt != null)
			return rt.getLed().getBlinks();
		return new LinkedList<>();
	}

	public List<String> getAudioFiles() {
		RtBoxInfo rt = getStatus();
		if (rt != null)
			return rt.getAudio().getAudio_files();
		return new LinkedList<>();
	}
	
	public BlinkAnimation getAnimation(String animationName) {
		RtBoxInfo rt = getStatus();
		if (isReachable()) {
			List<String> blinks = rt.getLed().getBlinks();
			if (blinks.contains(animationName)) {
				JsonObject json = this.callEndpoint(this.urlRoot + "/blink/" + animationName, "GET");
				if (json != null) {
					try {
						return new Gson().fromJson(json, BlinkAnimation.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public boolean playAnimation(String animationName, boolean endless) {
		if (isReachable()) {
			RtBoxInfo rt = getStatus();
			List<String> blinks = rt.getLed().getBlinkAsList();
			if (blinks.contains(animationName)) {
				return this.callSubroutine(this.urlRoot + "/led/play/" + animationName + "?endless=" + endless, "blink queued");
			}
		}
		return false;
	}
	
	public boolean stopAnimationPlayback() {
		if (isReachable()) {
			return this.callSubroutine(this.urlRoot + "/led/stop", "stopping animation queued");
		}
		return false;
	}
	
	public boolean playAudio(String audioName) {
		if (isReachable()) {
			RtBoxInfo rt = getStatus();
			List<String> audioFiles = rt.getAudio().getAudio_files();
			if (audioFiles.contains(audioName)) {
				return this.callSubroutine(this.urlRoot + "/audio/play/" + audioName, "Audio queued");
			}
		}
		return false;
	}
	
	public boolean stopAudioPlayback() {
		if (isReachable()) {
			return this.callSubroutine(this.urlRoot + "/audio/stop", "Playback stopped");
		}
		return false;
	}
	
	public boolean flushAudioQueue() {
		if (isReachable()) {
			return this.callSubroutine(this.urlRoot + "/audio/flush", "Queue flushed");
		}
		return false;
	}
	
	public boolean disableRandomAudio() {
		if (isReachable()) {
			return this.callSubroutine(this.urlRoot + "/audio/random/disable", "Random playback disabled");
		}
		return false;
	}
	
	public boolean enableRandomAudio() {
		if (isReachable()) {
			return this.callSubroutine(this.urlRoot + "/audio/random/enable", "Random playback enabled");
		}
		return false;
	}
	
	public boolean stopRandomAudio() {
		if (isReachable()) {
			return this.callSubroutine(this.urlRoot + "/audio/random/stop", "Random playback enabled");
		}
		return false;
	}

	public boolean isReachable() {
		RtBoxInfo rt = getStatus();
		return rt != null;
	}
	
}
