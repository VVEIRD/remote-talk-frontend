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
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.vmichalak.protocol.ssdp.Device;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.data.RtBoxInfo;

public class RtBoxDevice {

	private String deviceName  = null;
	private String urlRoot     = null;
	private String ip          = null;
	private String serviceType = null;
	
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
		return rt;
	}

	public String[] getBlinkAnimationList() {
		RtBoxInfo rt = getStatus();
		if (rt != null)
			return rt.getLed().getBlinks();
		return null;
	}
	
	public BlinkAnimation getAnimation(String animationName) {
		RtBoxInfo rt = getStatus();
		if (isReachable()) {
			List<String> blinks = Arrays.asList(rt.getLed().getBlinks());
			if (blinks.contains(animationName)) {
				JsonObject json = this.callEndpoint(this.urlRoot + "/blink/" + animationName, "GET");
				return new Gson().fromJson(json, BlinkAnimation.class);
			}
		}
		return null;
	}
	
	public boolean playAnimation(String animationName, boolean endless) {
		if (isReachable()) {
			RtBoxInfo rt = getStatus();
			List<String> blinks = rt.getLed().getBlinkAsList();
			if (blinks.contains(animationName)) {
				JsonObject json = this.callEndpoint(this.urlRoot + "/led/play/" + animationName + "?endless=" + endless, "GET");
				if (json.get("status") != null && json.get("status").isJsonPrimitive()) {
					try {
						String status = json.get("status").getAsString();
						return status == "blink queued";
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	public boolean isReachable() {
		RtBoxInfo rt = getStatus();
		return rt != null;
	}
	
}
