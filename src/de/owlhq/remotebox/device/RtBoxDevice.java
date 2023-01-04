package de.owlhq.remotebox.device;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import de.owlhq.remotebox.data.PlayEffect;
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
		
	public JsonObject callEndpoint(String urlString, String method, String content) {
		String data = null;
		JsonObject json = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if("PUT".equals(method.toUpperCase())) {
				con.setDoOutput(true);
				con.setRequestMethod(method);
				try(OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream())) {
					out.write(content);
				}
			}
			else
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
			JsonParser parser = new JsonParser();
			try {
				json = (JsonObject) parser.parse(data);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	private boolean callSubroutine(String url, String method, String expectedStatus, String content) {
		JsonObject json = this.callEndpoint(url, method, content);
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

	private boolean callSubroutine(String url, String expectedStatus) {
		return callSubroutine(url, "GET", expectedStatus, null);
	}
	
	public RtBoxInfo getStatus() {
		JsonObject json = this.callEndpoint(this.urlRoot, "GET", null);
		RtBoxInfo rt = null;
		if (json != null) {
			try {
				rt = new Gson().fromJson(json, RtBoxInfo.class);
			} catch (JsonSyntaxException e) {
				BlinkApp.setStatusText("Could get status update from device", Color.RED.darker());
				e.printStackTrace();
			}
		}
		else {
			BlinkApp.setStatusText("Could get status update from device", Color.RED.darker());
		}
		this.lastStatus = rt;
		return rt;
	}

	public List<String> getAnimationList() {
		RtBoxInfo rt = getStatus();
		if (rt != null)
			return rt.getLed().getBlinks();
		BlinkApp.setStatusText("Could not get animations", Color.RED.darker());
		return new LinkedList<>();
	}

	public List<String> getAudioFiles() {
		RtBoxInfo rt = getStatus();
		if (rt != null)
			return rt.getAudio().getAudio_files();
		BlinkApp.setStatusText("Could not get sound clip names", Color.RED.darker());
		return new LinkedList<>();
	}
	
	public BlinkAnimation getAnimation(String animationName) {
		RtBoxInfo rt = getStatus();
		if (isReachable()) {
			List<String> blinks = rt.getLed().getBlinks();
			if (blinks.contains(animationName)) {
				JsonObject json = this.callEndpoint(this.urlRoot + "/blink/" + animationName, "GET", null);
				if (json != null) {
					try {
						return new Gson().fromJson(json, BlinkAnimation.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}
		BlinkApp.setStatusText("Could not get animation" + animationName, Color.RED.darker());
		return null;
	}
	
	public boolean putAnimation(String animationName, BlinkAnimation animation) {
		boolean success = callSubroutine(this.urlRoot + "/blink/" + animationName, "PUT", "blink saved", new Gson().toJson(animation));
		if (success)
			BlinkApp.setStatusText("Uploaded animation " + animationName, Color.GREEN.darker());
		else
			BlinkApp.setStatusText("Could not upload animation" + animationName, Color.RED.darker());
		return success;
	}
	
	public boolean playAnimation(String animationName, boolean endless) {
		if (isReachable()) {
			RtBoxInfo rt = getStatus();
			List<String> blinks = rt.getLed().getBlinkAsList();
			if (blinks.contains(animationName)) {
				boolean success = this.callSubroutine(this.urlRoot + "/led/play/" + animationName + "?endless=" + endless, "blink queued");
				if (success)
					BlinkApp.setStatusText("Playing animation " + animationName + (endless ? " endlessly" : ""), Color.GREEN.darker());
				else
					BlinkApp.setStatusText("Could not play animation" + animationName, Color.RED.darker());
				return success;
			}
		}
		return false;
	}
	
	public boolean stopAnimationPlayback() {
		if (isReachable()) {
			boolean success = this.callSubroutine(this.urlRoot + "/led/stop", "stopping animation queued");
			if (success)
				BlinkApp.setStatusText("Animation stopped", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not stop animation", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean playAudio(String audioName) {
		if (isReachable()) {
			RtBoxInfo rt = getStatus();
			List<String> audioFiles = rt.getAudio().getAudio_files();
			if (audioFiles.contains(audioName)) {
				boolean success = this.callSubroutine(this.urlRoot + "/audio/play/" + audioName, "Audio queued");
				if (success)
					BlinkApp.setStatusText("sound " + audioName + " queued", Color.GREEN.darker());
				else
					BlinkApp.setStatusText("Could not queue sound " + audioName, Color.RED.darker());
				return success;
			}
		}
		return false;
	}
	
	public boolean stopAudioPlayback() {
		if (isReachable()) {
			boolean success = this.callSubroutine(this.urlRoot + "/audio/stop", "Playback stopped");
			if (success)
				BlinkApp.setStatusText("Audio playback stopped", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not stop audio playback", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean flushAudioQueue() {
		if (isReachable()) {
			boolean success = this.callSubroutine(this.urlRoot + "/audio/flush", "Queue flushed");
			if (success)
				BlinkApp.setStatusText("Queue flushed", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not flush queue", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean disableRandomAudio() {
		if (isReachable()) {
			boolean success = this.callSubroutine(this.urlRoot + "/audio/random/disable", "Random playback disabled");
			if (success)
				BlinkApp.setStatusText("Random audio disabled", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not disable random audio", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean enableRandomAudio() {
		if (isReachable()) {
			boolean success = this.callSubroutine(this.urlRoot + "/audio/random/enable", "Random playback enabled");
			if (success)
				BlinkApp.setStatusText("Random audio enabled", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not enable random audio", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean stopRandomAudio() {
		if (isReachable()) {
			boolean success = this.callSubroutine(this.urlRoot + "/audio/random/stop", "Random playback enabled");
			if (success)
				BlinkApp.setStatusText("Random audio stopped", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not stop random audio", Color.RED.darker());
			return success;
				
		}
		return false;
	}

	public boolean isReachable() {
		RtBoxInfo rt = getStatus();
		return rt != null;
	}

	public boolean playEffect(PlayEffect effect) {
		boolean successfullPlayed = true;
		if (effect != null) {
			if (effect.getAudioFile() != null)
				successfullPlayed = successfullPlayed && this.playAudio(effect.getAudioFile());
			if (effect.getAnimationName() != null)
				successfullPlayed = successfullPlayed && this.playAnimation(effect.getAnimationName(), effect.isEndlessAnimation());
		}
		else {
			successfullPlayed = false;
		}
		if (successfullPlayed) 
			BlinkApp.setStatusText(effect.getName() + " played", Color.GREEN.darker());
		else
			BlinkApp.setStatusText(effect.getName() + " not played", Color.RED.darker());
		return successfullPlayed;
	}
	
}
