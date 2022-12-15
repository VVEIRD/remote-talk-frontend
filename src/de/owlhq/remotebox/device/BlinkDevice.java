package de.owlhq.remotebox.device;

import com.vmichalak.protocol.ssdp.Device;

public class BlinkDevice {

	private String deviceName  = null;
	private String urlRoot     = null;
	private String ip          = null;
	private String serviceType = null;
	
	public BlinkDevice(Device ssdpDevice) {
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
	
	

}
