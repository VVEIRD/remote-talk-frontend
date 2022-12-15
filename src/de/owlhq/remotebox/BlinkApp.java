package de.owlhq.remotebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vmichalak.protocol.ssdp.Device;
import com.vmichalak.protocol.ssdp.SSDPClient;

import de.owlhq.remotebox.device.BlinkDevice;
import de.owlhq.remotebox.network.DeviceNetworkEvent;
import de.owlhq.remotebox.network.DeviceNetworkListener;

public class BlinkApp {

	private static Thread deviceFinderDaemon = null;
	private static boolean daemonRunning = true;
	
	private static String ssdpDeviceName = "remote-box-client";
	
	private static Map<String, BlinkDevice> DEVICES = null;
	
	private static List<DeviceNetworkListener> deviceListener = new ArrayList<DeviceNetworkListener>(2);
	
	static {
		init();
	}

	public static void init() {
		DEVICES = new HashMap<>();
		findDevices();
		deviceFinderDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while(daemonRunning) {
					findDevices();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		deviceFinderDaemon.setDaemon(true);
		deviceFinderDaemon.start();
	}
	
	private static void findDevices() {
		try {
			List<Device> devices = SSDPClient.discover(1000, ssdpDeviceName);
			List<String> existingDevices = new LinkedList<>();
			List<String> newDevices = new LinkedList<>();
			List<String> removedDevices = new LinkedList<>();
			for (Device device : devices) {
				// New Device
				if (!DEVICES.containsKey(device.getUSN())) {
					newDevices.add(device.getUSN());
				}
				else {
					existingDevices.add(device.getUSN());
				}
				DEVICES.put(device.getUSN(), new BlinkDevice(device));
			}
			removedDevices.addAll(new LinkedList<>(DEVICES.keySet()));
			removedDevices.removeAll(newDevices);
			removedDevices.removeAll(existingDevices);
			for (String deviceName : newDevices) {
				DeviceNetworkEvent e = new DeviceNetworkEvent(DEVICES.get(deviceName), DeviceNetworkEvent.DEVICE_CONNECTED);
				for (DeviceNetworkListener listener : deviceListener) {
					listener.deviceNetworkChange(e);
				}
			}
			for (String deviceName : removedDevices) {
				DeviceNetworkEvent e = new DeviceNetworkEvent(DEVICES.get(deviceName), DeviceNetworkEvent.DEVICE_DISCONNECTED);
				for (DeviceNetworkListener listener : deviceListener) {
					listener.deviceNetworkChange(e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, BlinkDevice> getDevices() {
		return DEVICES;
	}
	
	public static BlinkDevice getDevices(String usn) {
		return DEVICES.get(usn);
	}
	
	public static List<String> getDeviceNames() {
		return new LinkedList<String>(DEVICES.keySet());
	}
	
	public static boolean addDeviceNetworkListener(DeviceNetworkListener d) {
		return deviceListener.add(d);
	}
	
	public static boolean removeDeviceNetworkListener(DeviceNetworkListener d) {
		return deviceListener.remove(d);
	}
}
