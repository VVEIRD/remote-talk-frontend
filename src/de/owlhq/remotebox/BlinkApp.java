package de.owlhq.remotebox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.vmichalak.protocol.ssdp.Device;
import com.vmichalak.protocol.ssdp.SSDPClient;

import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.audio.AudioControl;
import de.owlhq.remotebox.device.BlinkDevice;
import de.owlhq.remotebox.gui.frame.MainFrame;
import de.owlhq.remotebox.gui.frame.StartupFrame;
import de.owlhq.remotebox.network.DeviceNetworkEvent;
import de.owlhq.remotebox.network.DeviceNetworkListener;

public class BlinkApp {

	private static Thread deviceFinderDaemon = null;
	private static boolean daemonRunning = true;
	
	private static String ssdpDeviceName = "remote-box-client";

	private static Map<String, BlinkDevice> ALL_DEVICES = null;
	
	private static Map<String, BlinkDevice> NETWORK_DEVICES = null;
	
	private static Map<String, BlinkDevice> CUSTOM_DEVICES = null;

	private static Map<String, AudioControl> AUDIO_CONTROLLER = null;
	
	private static List<DeviceNetworkListener> deviceListener = new ArrayList<DeviceNetworkListener>(2);
	
	private static Properties CONFIGURATION = null;
	private static JFrame CURRENT_WINDOW;
	
	static {
		init();
	}

	public static void init() {
		ALL_DEVICES = new HashMap<>();
		AUDIO_CONTROLLER = new HashMap<>();
		NETWORK_DEVICES = new HashMap<>();
		CUSTOM_DEVICES = new HashMap<>();
		CONFIGURATION = new Properties();
		boolean loaded = false;
		try (FileInputStream fIn = new FileInputStream("data" + File.separator + "configuration.protperties")) {
			CONFIGURATION.load(fIn);
			loaded = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		// add coustom device count
		if (!CONFIGURATION.containsKey("de.owlhq.customDevices")) {
			CONFIGURATION.setProperty("de.owlhq.customDevices", "0");
		}
		for (int i=0;i<getConfigInt("de.owlhq.customDevices");i++) {
			String deviceJson = getConfig("de.owlhq.customDevice."+i);
			Gson gson = new GsonBuilder().create();
			try {
				BlinkDevice bd = gson.fromJson(deviceJson, BlinkDevice.class);
				CUSTOM_DEVICES.put(bd.getDeviceName(), bd);
			}
			catch(JsonSyntaxException  e) {
			}
		}
		ALL_DEVICES.putAll(CUSTOM_DEVICES);
		if (!loaded) {
			CONFIGURATION.setProperty("de.owlhq.customDevices", "0");
		}
		// add selected device and store configuration
		if (!loaded || !CONFIGURATION.contains("de.owlhq.selectedDevice") || CONFIGURATION.getProperty("de.owlhq.selectedDevice").equals("")) {
			CONFIGURATION.setProperty("de.owlhq.selectedDevice", ALL_DEVICES.keySet().isEmpty() ? "" : ALL_DEVICES.keySet().iterator().next());
			storeConfiguration();
		}
		findDevices();
		deviceFinderDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while(daemonRunning) {
					findDevices();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		deviceFinderDaemon.setDaemon(true);
		deviceFinderDaemon.start();
	}

	private static void storeConfiguration() {
		Thread d = new Thread(new Runnable() {
			@Override
			public void run() {
				try (FileOutputStream fOut = new FileOutputStream("data" + File.separator + "configuration.protperties")) {
					CONFIGURATION.store(fOut, "");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		d.start();
	}
	
	private static void findDevices() {
		try {
			List<Device> devices = SSDPClient.discover(1000, ssdpDeviceName);
			List<String> existingDevices = new LinkedList<>();
			List<String> newDevices = new LinkedList<>();
			List<String> removedDevices = new LinkedList<>();
			for (Device device : devices) {
				// New Device
				if (!NETWORK_DEVICES.containsKey(device.getUSN())) {
					newDevices.add(device.getUSN());
				}
				else {
					existingDevices.add(device.getUSN());
				}
				NETWORK_DEVICES.put(device.getUSN(), new BlinkDevice(device));
			}
			removedDevices.addAll(new LinkedList<>(NETWORK_DEVICES.keySet()));
			removedDevices.removeAll(newDevices);
			removedDevices.removeAll(existingDevices);
			for (String deviceName : newDevices) {
				DeviceNetworkEvent e = new DeviceNetworkEvent(NETWORK_DEVICES.get(deviceName), DeviceNetworkEvent.DEVICE_CONNECTED);
				for (DeviceNetworkListener listener : deviceListener) {
					listener.deviceNetworkChange(e);
				}
			}
			for (String deviceName : removedDevices) {
				DeviceNetworkEvent e = new DeviceNetworkEvent(NETWORK_DEVICES.get(deviceName), DeviceNetworkEvent.DEVICE_DISCONNECTED);
				for (DeviceNetworkListener listener : deviceListener) {
					listener.deviceNetworkChange(e);
				}
			}
			// Update ALL_DEVICES on change
			if (newDevices.size() != 0 || removedDevices.size() != 0) {
				Map<String, BlinkDevice> new_all_devices = new HashMap<>();
				new_all_devices.putAll(NETWORK_DEVICES);
				new_all_devices.putAll(CUSTOM_DEVICES);
				ALL_DEVICES = new_all_devices;
				for (String usn : ALL_DEVICES.keySet()) {
					AUDIO_CONTROLLER.clear();
					AUDIO_CONTROLLER.put(usn, new AudioControl(ALL_DEVICES.get(usn)));
				}
				if (!ALL_DEVICES.containsKey(CONFIGURATION.getProperty("de.owlhq.selectedDevice"))) {
					CONFIGURATION.setProperty("de.owlhq.selectedDevice", ALL_DEVICES.keySet().isEmpty() ? "" : ALL_DEVICES.keySet().iterator().next());
					storeConfiguration();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, BlinkDevice> getDevices() {
		return ALL_DEVICES;
	}
	
	public static BlinkDevice getDevices(String usn) {
		return ALL_DEVICES.get(usn);
	}
	
	public static boolean selectDevice(String usn) {
		if(ALL_DEVICES.containsKey(usn)) {
			CONFIGURATION.setProperty("de.owlhq.selectedDevice", usn);
			storeConfiguration();
			return true;
		}
		return false;
	}
	
	public static BlinkDevice getSelectedDevice() {
		return ALL_DEVICES.get(CONFIGURATION.getProperty("de.owlhq.selectedDevice"));
	}
	
	public static AudioControl getSelectedDeviceAudioController() {
		return AUDIO_CONTROLLER.get(CONFIGURATION.getProperty("de.owlhq.selectedDevice"));
	}
	
	public static List<String> getDeviceNames() {
		return new LinkedList<String>(ALL_DEVICES.keySet());
	}
	
	public static boolean addDeviceNetworkListener(DeviceNetworkListener d) {
		return deviceListener.add(d);
	}
	
	public static boolean removeDeviceNetworkListener(DeviceNetworkListener d) {
		return deviceListener.remove(d);
	}
	
	public static String getConfig(String key) {
		return CONFIGURATION.getProperty(key);
	}
	
	public static int getConfigInt(String key) {
		System.out.println(CONFIGURATION.getProperty(key));
		System.out.println(key);
		return Integer.valueOf(CONFIGURATION.getProperty(key));
	}
	
	public static boolean configContainsKey(String key) {
		return CONFIGURATION.containsKey(key);
	}
	
	public static <T> boolean configContainsKey(String key, Class<T> type) {
		return CONFIGURATION.containsKey(key) && CONFIGURATION.get(key).getClass() == type;
	}

	public static void setConfig(String key, String text) {
		CONFIGURATION.setProperty(key, text);
	}
	public static void saveConfig() {
		storeConfiguration();
	}

	public static void showMainDialog() {
		if (CURRENT_WINDOW != null && CURRENT_WINDOW.isVisible()) {
			CURRENT_WINDOW.setVisible(false);
			CURRENT_WINDOW.dispose();
		}
			
		CURRENT_WINDOW = new MainFrame();
		CURRENT_WINDOW.setVisible(true);
	}

	public static void showStartupDialog() {
		CURRENT_WINDOW = new StartupFrame();
		CURRENT_WINDOW.setVisible(true);
	}
}
