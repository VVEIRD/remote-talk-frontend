package de.owlhq.remotebox.device;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private transient long lastStatusTime = System.currentTimeMillis();
	
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
			con.setRequestProperty("Cookie", "accessToken=" + BlinkApp.getConfig("de.owlhq.accessToken")); 
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
			if(BlinkApp.debug())
				e.printStackTrace();
		} catch (ProtocolException e) {
			if(BlinkApp.debug())
				e.printStackTrace();
		} catch (IOException e) {
			if(BlinkApp.debug())
				e.printStackTrace();
		}
		if (data != null) {
			JsonParser parser = new JsonParser();
			try {
				json = (JsonObject) parser.parse(data);
			} catch (JsonSyntaxException e) {
				if(BlinkApp.debug())
					e.printStackTrace();
				System.out.println(data);
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
				if(BlinkApp.debug())
					e.printStackTrace();
			}
		}
		return false;
	}

	private boolean callSubroutine(String url, String expectedStatus) {
		return callSubroutine(url, "GET", expectedStatus, null);
	}

	public RtBoxInfo getStatus() {
		return this.getStatus(false);
	}
	public RtBoxInfo getStatus(boolean force) {
		if (force || System.currentTimeMillis() - this.lastStatusTime > 1000 || this.lastStatus == null) {
		JsonObject json = this.callEndpoint(this.urlRoot, "GET", null);
		RtBoxInfo rt = null;
		if (json != null) {
			try {
				rt = new Gson().fromJson(json, RtBoxInfo.class);
			} catch (JsonSyntaxException e) {
				BlinkApp.setStatusText("Could get status update from device", Color.RED.darker());
				if(BlinkApp.debug())
					e.printStackTrace();
			}
		}
		else {
			BlinkApp.setStatusText("Could get status update from device", Color.RED.darker());
		}
		if (rt != null) {
			this.lastStatus = rt;
			this.lastStatusTime = System.currentTimeMillis();
		}
		else {
			if (this.lastStatus == null) {
				this.lastStatus = new RtBoxInfo();
				Map<String, String> processes = new HashMap<>();
				processes.put("led", "offline");
				processes.put("audio", "offline");
				processes.put("voice", "offline");
				this.lastStatus.setProcesses(processes);
			}
			this.lastStatus.setReachable(false);
		}
		return this.lastStatus;
		}
		else {
			return this.lastStatus;
		}
			
	}

	public List<String> getAnimationList() {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isLedEndpointOnline())
			return rt.getLed().getBlinks();
		BlinkApp.setStatusText("Could not get animations", Color.RED.darker());
		return new LinkedList<>();
	}

	public List<String> getAudioFiles() {
		RtBoxInfo rt = getStatus();
		if (rt.isAudioEndpointOnline())
			return rt.getAudio().getAudio_files();
		BlinkApp.setStatusText("Could not get sound clip names", Color.RED.darker());
		return new LinkedList<>();
	}
	
	public BlinkAnimation getAnimation(String animationName) {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isLedEndpointOnline()) {
			List<String> blinks = rt.getLed().getBlinks();
			if (blinks.contains(animationName)) {
				JsonObject json = this.callEndpoint(rt.getLedEndpoint() + "/blink/" + animationName, "GET", null);
				if (json != null) {
					try {
						return new Gson().fromJson(json, BlinkAnimation.class);
					} catch (JsonSyntaxException e) {
						if(BlinkApp.debug())
							e.printStackTrace();
					}
				}
			}
		}
		BlinkApp.setStatusText("Could not get animation" + animationName, Color.RED.darker());
		return null;
	}
	
	public boolean putAnimation(String animationName, BlinkAnimation animation) {
		boolean success = false;
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isLedEndpointOnline()) {
			success = callSubroutine(rt.getLedEndpoint() + "/blink/" + animationName, "PUT", "blink saved", new Gson().toJson(animation));
			if (success)
				BlinkApp.setStatusText("Uploaded animation " + animationName, Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not upload animation" + animationName, Color.RED.darker());
		}
		return success;
	}
	
	public boolean playAnimation(String animationName, boolean endless) {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isLedEndpointOnline()) {
			List<String> blinks = rt.getLed().getBlinkAsList();
			if (blinks.contains(animationName)) {
				boolean success = this.callSubroutine(rt.getLedEndpoint() + "/led/play/" + animationName + "?endless=" + endless, "blink queued");
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
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isLedEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getLedEndpoint() + "/led/stop", "stopping animation queued");
			if (success)
				BlinkApp.setStatusText("Animation stopped", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not stop animation", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean playAudio(String audioName) {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isAudioEndpointOnline()) {
			List<String> audioFiles = rt.getAudio().getAudio_files();
			if (audioFiles.contains(audioName)) {
				boolean success = this.callSubroutine(rt.getAudioEndpoint() + "/audio/play/" + audioName, "Audio queued");
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
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isAudioEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getAudioEndpoint() + "/audio/stop", "Playback stopped");
			if (success)
				BlinkApp.setStatusText("Audio playback stopped", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not stop audio playback", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean flushAudioQueue() {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isAudioEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getAudioEndpoint() + "/audio/flush", "Queue flushed");
			if (success)
				BlinkApp.setStatusText("Queue flushed", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not flush queue", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean disableRandomAudio() {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isAudioEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getAudioEndpoint() + "/audio/random/disable", "Random playback disabled");
			if (success)
				BlinkApp.setStatusText("Random audio disabled", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not disable random audio", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean enableRandomAudio() {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isAudioEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getAudioEndpoint() + "/audio/random/enable", "Random playback enabled");
			if (success)
				BlinkApp.setStatusText("Random audio enabled", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not enable random audio", Color.RED.darker());
			return success;
		}
		return false;
	}
	
	public boolean stopRandomAudio() {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isAudioEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getAudioEndpoint() + "/audio/random/stop", "Random playback enabled");
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
		return rt.isReachable();
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
			BlinkApp.setStatusText(effect.getName() + " was not played", Color.RED.darker());
		return successfullPlayed;
	}

	public boolean connectVoice(String ip, int port, String username, String password) {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isVoiceEndpointOnline()) {
			String url = rt.getVoiceEndpoint() + "/voice/connect?host=" + ip + "&port=" + port + "&username=" + username + "&password=" + password;
			boolean success = this.callSubroutine(url, "connected");
			if (success)
				BlinkApp.setStatusText("Voice connected", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not connect voice", Color.RED.darker());
			return success;
				
		}
		return false;
		
	}

	public boolean disconnectVoice() {
		RtBoxInfo rt = getStatus();
		if (rt.isReachable() && rt.isVoiceEndpointOnline()) {
			boolean success = this.callSubroutine(rt.getVoiceEndpoint() + "/voice/disconnect", "disconnected");
			if (success)
				BlinkApp.setStatusText("Voice disconnected", Color.GREEN.darker());
			else
				BlinkApp.setStatusText("Could not disconnect from voice", Color.RED.darker());
			return success;
				
		}
		return false;
	}
	
}
