package de.owlhq.remotebox.data.info;

import java.util.Arrays;
import java.util.List;

public class LedInfo {

	List<String> blinks = null;
	
	BlinkDevice[] devices = null;
	
	String last_update = "";
	
	
	public List<String> getBlinks() {
		return blinks;
	}

	public List<String> getBlinkAsList() {
		return this.blinks;
	}

	public void setBlinks(List<String> blinks) {
		this.blinks = blinks;
	}

	public BlinkDevice[] getDevices() {
		return devices;
	}

	public void setDevices(BlinkDevice[] devices) {
		this.devices = devices;
	}
	
	public PlayingInfo getCurrentlyPlaying() {
		PlayingInfo pInfo = null;
		if (devices != null && devices.length > 0)
			pInfo = devices[0].getCurrently_playing();
		return pInfo;
	}
	
	public String getLast_update() {
		return last_update;
	}
	public void setLast_update(String last_update) {
		this.last_update = last_update;
	}

	public class BlinkDevice {
		PlayingInfo currently_playing = null;
		String description = null;
		String manufacturer = null;
		String name = null;
		public PlayingInfo getCurrently_playing() {
			return currently_playing;
		}
		public void setCurrently_playing(PlayingInfo currently_playing) {
			this.currently_playing = currently_playing;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getManufacturer() {
			return manufacturer;
		}
		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public class PlayingInfo {
		String blink = null;
		boolean endless = false;
		public String getBlink() {
			return blink;
		}
		public void setBlink(String blink) {
			this.blink = blink;
		}
		public boolean isEndless() {
			return endless;
		}
		public void setEndless(boolean endless) {
			this.endless = endless;
		}
		
	}
}
