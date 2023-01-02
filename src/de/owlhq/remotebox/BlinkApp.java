package de.owlhq.remotebox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vmichalak.protocol.ssdp.Device;
import com.vmichalak.protocol.ssdp.SSDPClient;

import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.audio.AudioControl;
import de.owlhq.remotebox.data.PlayEffect;
import de.owlhq.remotebox.data.info.AudioInfo;
import de.owlhq.remotebox.data.info.RtBoxInfo;
import de.owlhq.remotebox.device.RtBoxDevice;
import de.owlhq.remotebox.events.RtDeviceEvent;
import de.owlhq.remotebox.events.RtDeviceListener;
import de.owlhq.remotebox.gui.frame.EffectCreatorDialog;
import de.owlhq.remotebox.gui.frame.MainFrame;
import de.owlhq.remotebox.gui.frame.StartupFrame;

public class BlinkApp {

	private static Thread deviceFinderDaemon = null;
	
	private static Thread selectedDeviceChangeDaemon = null;
	
	private static boolean daemonRunning = true;
	
	private static String ssdpDeviceName = "remote-box-client";

	private static RtBoxDevice SELECTED_DEVICE = null;

	private static Map<String, RtBoxDevice> ALL_DEVICES = null;
	
	private static Map<String, RtBoxDevice> NETWORK_DEVICES = null;
	
	private static Map<String, RtBoxDevice> CUSTOM_DEVICES = null;

	private static Map<String, AudioControl> AUDIO_CONTROLLER = null;
	
	private static List<RtDeviceListener> deviceListener = new ArrayList<RtDeviceListener>(2);
	
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
			CONFIGURATION.setProperty("de.owlhq.daemon.network.sleep", "5000");
			CONFIGURATION.setProperty("de.owlhq.daemon.change.sleep", "2500");
		}
		for (int i=0;i<getConfigInt("de.owlhq.customDevices");i++) {
			String deviceJson = getConfig("de.owlhq.customDevice."+i);
			Gson gson = new GsonBuilder().create();
			try {
				RtBoxDevice bd = gson.fromJson(deviceJson, RtBoxDevice.class);
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
		if (!loaded && !CONFIGURATION.contains("de.owlhq.selectedDeviceUsn") || CONFIGURATION.getProperty("de.owlhq.selectedDeviceUsn").equals("")) {
			CONFIGURATION.setProperty("de.owlhq.selectedDeviceUsn", ALL_DEVICES.keySet().isEmpty() ? "" : ALL_DEVICES.keySet().iterator().next());
			storeConfiguration();
		}
		// Set SELECTED_DEVICE
		if (ALL_DEVICES.containsKey(getConfig("de.owlhq.selectedDeviceUsn"))) {
			SELECTED_DEVICE  = ALL_DEVICES.get(getConfig("de.owlhq.selectedDeviceUsn"));
		}
		else {
			SELECTED_DEVICE = new Gson().fromJson(getConfig("de.owlhq.selectedDevice"), RtBoxDevice.class);
		}
		// Download all Blink Animations if device is available
		if (SELECTED_DEVICE.isReachable()) {
			List<String> animations = SELECTED_DEVICE.getBlinkAnimationList();
			for (String animation : animations) {
				BlinkAnimation bA = SELECTED_DEVICE.getAnimation(animation);
				saveAnimation(animation, bA);
			}
		}
		AUDIO_CONTROLLER.put(SELECTED_DEVICE.getDeviceName(), new AudioControl(SELECTED_DEVICE));
		findDevices();
		deviceFinderDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while(daemonRunning) {
					findDevices();
					try {
						Thread.sleep(getConfigInt("de.owlhq.daemon.network.sleep"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		deviceFinderDaemon.setDaemon(true);
		deviceFinderDaemon.start();
		selectedDeviceChangeDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				updateSelectedDeviceStatus();
			}
		});
		selectedDeviceChangeDaemon.setDaemon(true);
		selectedDeviceChangeDaemon.start();
		addRtDeviceListener(new RtDeviceListener() {
			
			@Override
			public void deviceChange(RtDeviceEvent e) {
				System.out.println("-------------------------------------------");
				System.out.println("Device Changed: ");
				System.out.println("-------------------------------------------");
				if (e.source != null)
				System.out.println("Name:   " + e.source.getDeviceName());
				System.out.println("Change: " + e.eventType);
				System.out.println("-------------------------------------------");
			}
		});
	}

	private static boolean saveAnimation(String animation, BlinkAnimation bA) {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		String data = g.toJson(bA);
		try (FileOutputStream fOut = new FileOutputStream("data" + File.separator + "blinks" + File.separator + animation + ".json")) {
			fOut.write(data.getBytes(StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static BlinkAnimation getAnimation(String animation) {
		BlinkAnimation anim = null;
		try (FileReader fIn = new FileReader("data" + File.separator + "blinks" + File.separator + animation + ".json", StandardCharsets.UTF_8)) {
			anim = new Gson().fromJson(fIn, BlinkAnimation.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		}
		return anim;
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
				NETWORK_DEVICES.put(device.getUSN(), new RtBoxDevice(device));
			}
			removedDevices.addAll(new LinkedList<>(NETWORK_DEVICES.keySet()));
			removedDevices.removeAll(newDevices);
			removedDevices.removeAll(existingDevices);
			for (String device : removedDevices) {
				NETWORK_DEVICES.remove(device);
			}
			// Updated Device Listener
			for (String deviceName : newDevices) {
				RtDeviceEvent e = new RtDeviceEvent(NETWORK_DEVICES.get(deviceName), RtDeviceEvent.DEVICE_CONNECTED);
				informDeviceListener(e);
			}
			for (String deviceName : removedDevices) {
				RtDeviceEvent e = new RtDeviceEvent(NETWORK_DEVICES.get(deviceName), RtDeviceEvent.DEVICE_DISCONNECTED);
				informDeviceListener(e);
			}
			// Update ALL_DEVICES on change
			if (newDevices.size() != 0 || removedDevices.size() != 0) {
				Map<String, RtBoxDevice> new_all_devices = new HashMap<>();
				new_all_devices.putAll(NETWORK_DEVICES);
				new_all_devices.putAll(CUSTOM_DEVICES);
				ALL_DEVICES = new_all_devices;
				for (String usn : ALL_DEVICES.keySet()) {
					AUDIO_CONTROLLER.clear();
					AUDIO_CONTROLLER.put(usn, new AudioControl(ALL_DEVICES.get(usn)));
				}
				if (!ALL_DEVICES.containsKey(getConfig("de.owlhq.selectedDeviceUsn"))) {
					System.out.println(getConfig("de.owlhq.selectedDeviceUsn"));
					System.out.println("Selected Device not reachable");
					SELECTED_DEVICE = new Gson().fromJson(getConfig("de.owlhq.selectedDevice"), RtBoxDevice.class);
					AUDIO_CONTROLLER.put(SELECTED_DEVICE.getDeviceName(), new AudioControl(SELECTED_DEVICE));
					//CONFIGURATION.setProperty("de.owlhq.selectedDeviceUsn", ALL_DEVICES.keySet().isEmpty() ? "" : ALL_DEVICES.keySet().iterator().next());
					storeConfiguration();
				}
				else {
					SELECTED_DEVICE = ALL_DEVICES.get(getConfig("de.owlhq.selectedDeviceUsn"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void updateSelectedDeviceStatus() {
		RtBoxInfo lastKnownState = null;
		if (SELECTED_DEVICE != null) {
			lastKnownState = SELECTED_DEVICE.getStatus();
		}
		while (daemonRunning) {
			if (SELECTED_DEVICE != null) {
				RtBoxInfo currentKnownState  = SELECTED_DEVICE.getStatus();
				// Connect & Disconnect Events are handled by findDevices()
				// Animation Events
				// Stop Animation Event
				if (currentKnownState != null && currentKnownState.hasAnimationStopped(lastKnownState)) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.ANIMATION_STOPPED);
					informDeviceListener(rtEvent);
				}
				// Start Animation Event
				if (currentKnownState != null && (currentKnownState.hasAnimationStarted(lastKnownState) || currentKnownState.hasAnimationChanged(lastKnownState) && !currentKnownState.hasAnimationStopped(lastKnownState))) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.ANIMATION_CHANGED);
					informDeviceListener(rtEvent);
				}
				// Stop Audio Event
				if (currentKnownState != null && currentKnownState.hasAudioStopped(lastKnownState)) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.AUDIO_STOPPED);
					informDeviceListener(rtEvent);
				}
				// Start Audio Event
				if (currentKnownState != null && (currentKnownState.hasAudioStarted(lastKnownState) || currentKnownState.hasAudioChanged(lastKnownState) && !currentKnownState.hasAudioStopped(lastKnownState))) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.AUDIO_STARTED);
					informDeviceListener(rtEvent);
				}
				// Disconnect Voice Event
				if (currentKnownState != null && currentKnownState.hasVoiceDisconnected(lastKnownState)) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.VOICE_DISCONNECTED);
					informDeviceListener(rtEvent);
				}
				// Connect Voice Event
				if (currentKnownState != null && (currentKnownState.hasVoiceConnected(lastKnownState) || currentKnownState.hasVoiceChanged(lastKnownState) && !currentKnownState.hasVoiceDisconnected(lastKnownState))) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.VOICE_CONNECTED);
					informDeviceListener(rtEvent);
				}
				lastKnownState = currentKnownState;
			}
			else  {
				lastKnownState = null;
			}
			try {
				Thread.sleep(getConfigInt("de.owlhq.daemon.change.sleep"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	private static void informDeviceListener(RtDeviceEvent rtEvent) {
		for (RtDeviceListener deviceListener : deviceListener) {
			try {
				deviceListener.deviceChange(rtEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Map<String, RtBoxDevice> getDevices() {
		return ALL_DEVICES;
	}
	
	public static RtBoxDevice getDevices(String usn) {
		return ALL_DEVICES.get(usn);
	}
	
	public static boolean selectDevice(String usn) {
		if(ALL_DEVICES.containsKey(usn)) {
			CONFIGURATION.setProperty("de.owlhq.selectedDeviceUsn", usn);
			CONFIGURATION.setProperty("de.owlhq.selectedDevice", new Gson().toJson(ALL_DEVICES.get(usn)));
			SELECTED_DEVICE = ALL_DEVICES.get(usn);
			storeConfiguration();
			// Download all Blink Animations if device is available
			if (SELECTED_DEVICE.isReachable()) {
				List<String> animations = SELECTED_DEVICE.getBlinkAnimationList();
				for (String animation : animations) {
					BlinkAnimation bA = SELECTED_DEVICE.getAnimation(animation);
					saveAnimation(animation, bA);
				}
			}
			return true;
		}
		return false;
	}
	
	public static RtBoxDevice getSelectedDevice() {
		return SELECTED_DEVICE;
	}
	
	public static AudioControl getSelectedDeviceAudioController() {
		return AUDIO_CONTROLLER.get(CONFIGURATION.getProperty("de.owlhq.selectedDeviceUsn"));
	}
	
	public static RtBoxInfo getSelectedDeviceStatus() {
		RtBoxInfo rt = null;
		if (SELECTED_DEVICE.isReachable()) {
			rt = SELECTED_DEVICE.getStatus();
		}
		return rt;
	}
	
	public static List<String> getDeviceNames() {
		List<String> ll = new LinkedList<String>();
		if(SELECTED_DEVICE != null)
			ll.add(SELECTED_DEVICE.getDeviceName());
		ll.addAll(ALL_DEVICES.keySet());
		return ll;
	}
	
	public static boolean addRtDeviceListener(RtDeviceListener d) {
		return deviceListener.add(d);
	}
	
	public static boolean removeRtDeviceListener(RtDeviceListener d) {
		return deviceListener.remove(d);
	}
	
	public static String getConfig(String key) {
		return CONFIGURATION.getProperty(key);
	}
	
	public static int getConfigInt(String key) {
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

	public static PlayEffect editEffect(PlayEffect effect) {
		
		EffectCreatorDialog cDiag = new EffectCreatorDialog(CURRENT_WINDOW, effect, getSelectedDevice().getBlinkAnimationList(), getSelectedDevice().getAudioFiles());
		cDiag.setVisible(true);
		return !cDiag.isAborted() ? cDiag.getEffect() : effect;
	}
}
