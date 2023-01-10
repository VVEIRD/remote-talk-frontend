package de.owlhq.remotebox;

import java.awt.Color;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vmichalak.protocol.ssdp.Device;
import com.vmichalak.protocol.ssdp.SSDPClient;

import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.data.PlayEffect;
import de.owlhq.remotebox.data.info.RtBoxInfo;
import de.owlhq.remotebox.device.RtBoxDevice;
import de.owlhq.remotebox.events.RtDeviceEvent;
import de.owlhq.remotebox.events.RtDeviceListener;
import de.owlhq.remotebox.gui.frame.EffectCreatorDialog;
import de.owlhq.remotebox.gui.frame.MainFrame;
import de.owlhq.remotebox.gui.frame.StartupFrame;
import de.owlhq.remotebox.gui.panel.LedInterface;
import de.owlhq.remotebox.gui.panel.LedPanel;

public class BlinkApp {

	private static Thread deviceFinderDaemon = null;
	
	private static Thread selectedDeviceChangeDaemon = null;
	
	private static boolean DAEMON_RUNNING = true;
	
	private static String SSDP_DEVICE_NAME = "remote-box-client";

	private static RtBoxDevice SELECTED_DEVICE = null;

	private static Map<String, RtBoxDevice> ALL_DEVICES = null;
	
	private static Map<String, RtBoxDevice> NETWORK_DEVICES = null;
	
	private static Map<String, RtBoxDevice> CUSTOM_DEVICES = null;
	
	private static List<RtDeviceListener> DEVICE_LISTENER = new ArrayList<RtDeviceListener>(2);
	
	private static List<PlayEffect> EFFECTS = new LinkedList<>();
	
	private static Properties CONFIGURATION = null;
	private static JFrame CURRENT_WINDOW = null;
	
	static { 
		init();
	}

	private static void init() {
		ALL_DEVICES = new HashMap<>();
		NETWORK_DEVICES = new HashMap<>();
		CUSTOM_DEVICES = new HashMap<>();
		CONFIGURATION = new Properties();
		boolean loaded = false;
		try (FileInputStream fIn = new FileInputStream("data" + File.separator + "configuration.properties")) {
			CONFIGURATION.load(fIn);
			loaded = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		// Load adapted config
		try (FileInputStream fIn = new FileInputStream("data" + File.separator + "configuration.auto.properties")) {
			CONFIGURATION.load(fIn);
			loaded = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		System.out.println("Keys");
		for (Object key : CONFIGURATION.keySet()) {
			System.out.println(key + ": " + CONFIGURATION.get(key));
		}
		EFFECTS = loadEffects();
		// add coustom device count
		boolean saveConfig = false;
		if (!CONFIGURATION.containsKey("de.owlhq.customDevices")) {
			CONFIGURATION.setProperty("de.owlhq.customDevices", "0");
			CONFIGURATION.setProperty("de.owlhq.daemon.network.sleep", "5000");
			CONFIGURATION.setProperty("de.owlhq.daemon.change.sleep", "2500");
			saveConfig = true;
		}
		if(!CONFIGURATION.contains("de.owlhq.dataDir")) {
			CONFIGURATION.setProperty("de.owlhq.dataDir", "data");
			saveConfig = true;
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
			saveConfig = true;
		}
		if(saveConfig)
			storeConfiguration();
		// Set SELECTED_DEVICE
		if (ALL_DEVICES.containsKey(getConfig("de.owlhq.selectedDeviceUsn"))) {
			SELECTED_DEVICE  = ALL_DEVICES.get(getConfig("de.owlhq.selectedDeviceUsn"));
		}
		else {
			SELECTED_DEVICE = new Gson().fromJson(getConfig("de.owlhq.selectedDevice"), RtBoxDevice.class);
		}
		// Download all Blink Animations if device is available
		if (SELECTED_DEVICE.isReachable()) {
			List<String> animations = SELECTED_DEVICE.getAnimationList();
			for (String animation : animations) {
				BlinkAnimation bA = SELECTED_DEVICE.getAnimation(animation);
				saveAnimation(animation, bA);
			}
			Map<String, BlinkAnimation> localAnimations = BlinkApp.getAnimations();
			for (String animation : localAnimations.keySet()) {
				if (!animations.contains(animation)) {
					SELECTED_DEVICE.putAnimation(animation, localAnimations.get(animation));
				}
			}
		}
		findDevices();
		deviceFinderDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while(DAEMON_RUNNING) {
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
				updateSelectedDeviceStatus(false);
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
	
	private static List<PlayEffect> loadEffects() {
		    return Stream.of(new File(getConfig("de.owlhq.dataDir") + File.separator + "effects").listFiles())
		      .filter(file -> !file.isDirectory())
		      .map(file -> loadEffect(file.getAbsolutePath()))
		      .collect(Collectors.toList());
	}
	
	private static Map<String, BlinkAnimation> getAnimations() {
			File[] files = new File(getConfig("de.owlhq.dataDir") + File.separator + "blinks").listFiles();
			Map<String, BlinkAnimation> animations = new HashMap<>();
			for (File file : files) {
				BlinkAnimation ba = getAnimation(file.getName().replace(".json", ""));
				System.out.println(file.getName().replace(".json", ""));
				animations.put(file.getName().replace(".json", ""), ba);
			}
		    return animations;
	}
	
	private static PlayEffect loadEffect(String path) {
		Gson g = new Gson();
		try(FileReader fIn = new FileReader(path, StandardCharsets.UTF_8)) {
			return g.fromJson(fIn, PlayEffect.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean uploadAnimation(String animation, BlinkAnimation bA) {
		if(SELECTED_DEVICE != null && SELECTED_DEVICE.isReachable()) {
			return SELECTED_DEVICE.putAnimation(animation, bA);
		}
		return false;
	}

	public static boolean saveAnimation(String animation, BlinkAnimation bA) {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		String data = g.toJson(bA);
		try (FileOutputStream fOut = new FileOutputStream(getConfig("de.owlhq.dataDir") + File.separator + "blinks" + File.separator + animation + ".json")) {
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

	public static boolean saveEffect(PlayEffect effect) {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		String data = g.toJson(effect);
		try (FileOutputStream fOut = new FileOutputStream(getConfig("de.owlhq.dataDir") + File.separator + "effects" + File.separator + effect.getName() + ".json")) {
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
	
	public static List<PlayEffect> getEffects() {
		return EFFECTS;
	}

	public static BlinkAnimation getAnimation(String animation) {
		BlinkAnimation anim = null;
		try (FileReader fIn = new FileReader(getConfig("de.owlhq.dataDir") + File.separator + "blinks" + File.separator + animation + ".json", StandardCharsets.UTF_8)) {
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
		if (CONFIGURATION.containsKey("de.owlhq.dataDir"))  {
			try (FileOutputStream fOut = new FileOutputStream(CONFIGURATION.get("de.owlhq.dataDir") + File.separator + "configuration.auto.properties")) {
				CONFIGURATION.store(fOut, "");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
		}
		}	
	}
	
	private static void findDevices() {
		try {
			List<Device> devices = SSDPClient.discover(1000, SSDP_DEVICE_NAME);
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
			// Updated Device Listener
			for (String deviceName : newDevices) {
				RtDeviceEvent e = new RtDeviceEvent(NETWORK_DEVICES.get(deviceName), RtDeviceEvent.DEVICE_CONNECTED);
				informDeviceListener(e);
			}
			for (String deviceName : removedDevices) {
				RtDeviceEvent e = new RtDeviceEvent(NETWORK_DEVICES.get(deviceName), RtDeviceEvent.DEVICE_DISCONNECTED);
				informDeviceListener(e);
			}
			for (String device : removedDevices) {
				NETWORK_DEVICES.remove(device);
			}
			// Update ALL_DEVICES on change
			if (newDevices.size() != 0 || removedDevices.size() != 0) {
				Map<String, RtBoxDevice> new_all_devices = new HashMap<>();
				new_all_devices.putAll(NETWORK_DEVICES);
				new_all_devices.putAll(CUSTOM_DEVICES);
				ALL_DEVICES = new_all_devices;
				if (!ALL_DEVICES.containsKey(getConfig("de.owlhq.selectedDeviceUsn"))) {
					System.out.println(getConfig("de.owlhq.selectedDeviceUsn"));
					System.out.println("Selected Device not reachable");
					SELECTED_DEVICE = new Gson().fromJson(getConfig("de.owlhq.selectedDevice"), RtBoxDevice.class);
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
	
	public static void forceSelectedDeviceStatusUpdate() {
		Thread d = new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateSelectedDeviceStatus(true);
			}
		});
		d.start();
	}
	
	private static RtBoxInfo lastKnownState = null;
	
	private static void updateSelectedDeviceStatus(boolean runOnce) {
		if (SELECTED_DEVICE != null && lastKnownState == null) {
			lastKnownState = SELECTED_DEVICE.getStatus(true);
		}
		do {
			if (SELECTED_DEVICE != null) {
				RtBoxInfo currentKnownState  = SELECTED_DEVICE.getStatus(true);
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
				if (currentKnownState != null && (currentKnownState.hasAudioStarted(lastKnownState) 
						|| currentKnownState.hasAudioChanged(lastKnownState) && !currentKnownState.hasAudioStopped(lastKnownState) 
						|| currentKnownState.hasRandomAudioChanged(lastKnownState))) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.AUDIO_STARTED);
					informDeviceListener(rtEvent);
				}
				// Disconnect Voice Event
				if (currentKnownState != null && currentKnownState.hasVoiceDisconnected(lastKnownState)) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.VOICE_DISCONNECTED);
					informDeviceListener(rtEvent);
				}
				// Connect Voice Event
				if (currentKnownState != null && (currentKnownState.hasVoiceConnected(lastKnownState) 
						|| currentKnownState.hasVoiceChanged(lastKnownState) && !currentKnownState.hasVoiceDisconnected(lastKnownState))) {
					RtDeviceEvent rtEvent = new RtDeviceEvent(SELECTED_DEVICE, RtDeviceEvent.VOICE_CONNECTED);
					informDeviceListener(rtEvent);
				}
				lastKnownState = currentKnownState;
			}
			else  {
				lastKnownState = null;
			}
			try {
				if (!runOnce)
					Thread.sleep(getConfigInt("de.owlhq.daemon.change.sleep"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (DAEMON_RUNNING && !runOnce);
		
	}

	private static void informDeviceListener(RtDeviceEvent rtEvent) {
		for (RtDeviceListener deviceListener : DEVICE_LISTENER) {
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
	
	public static RtBoxDevice getDevice(String usn) {
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
				List<String> animations = SELECTED_DEVICE.getAnimationList();
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
	
	public static RtBoxInfo getSelectedDeviceStatus() {
		if (lastKnownState != null)
			return lastKnownState;
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
		return DEVICE_LISTENER.add(d);
	}
	
	public static boolean removeRtDeviceListener(RtDeviceListener d) {
		return DEVICE_LISTENER.remove(d);
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


	public static void setConfig(String key, String text) {
		if (BlinkApp.debug()) {
			System.out.println("KEY: " + key + "; VALUE: " + text);
		}
		if(key != null && !key.isBlank() && text != null && !text.isBlank()) {
			if (BlinkApp.debug())
				System.out.println("SETTING PROPERTY");
			CONFIGURATION.setProperty(key, text);
		}
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
		EffectCreatorDialog cDiag = new EffectCreatorDialog(CURRENT_WINDOW, effect, getSelectedDevice().getAnimationList(), getSelectedDevice().getAudioFiles());
		cDiag.setVisible(true);
		if (!cDiag.isAborted())
			saveEffect(cDiag.getEffect());
		return !cDiag.isAborted() ? cDiag.getEffect() : effect;
	}
	
	public static void setStatusText(String text, Color c) {
		System.out.println("Status: " + text);
		if (CURRENT_WINDOW !=null && CURRENT_WINDOW instanceof MainFrame) {
			((MainFrame)CURRENT_WINDOW).setStatusText(text, c);
		}
	}

	public static boolean debug() {
		return "true".equalsIgnoreCase(getConfig("de.owlhq.debug"));
	}
	
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Device> devices = SSDPClient.discover(1000, "remote-box-client");
	    System.out.println(devices.size() + " devices found");
	    for (Device device : devices) {
	    	System.out.println("== DEVICE : ==");
			System.out.println(" USN:         " + device.getUSN());
			System.out.println(" IP:          " + device.getIPAddress());
			System.out.println(" Server:      " + device.getServer());
			System.out.println(" ServiceType: " + device.getServiceType());
			System.out.println(" URL:         " + device.getDescriptionUrl());
		}
	    BlinkApp.showStartupDialog();
	    RtBoxInfo rtInfo = BlinkApp.getSelectedDeviceStatus();
	    System.out.println("-------------------------------------------------------");
	    System.out.println("Status:");
	    System.out.println("-------------------------------------------------------");
	    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(rtInfo));
	    System.out.println("-------------------------------------------------------");
	    if (rtInfo != null && rtInfo.getLed().getCurrentlyPlaying() != null) {
		    System.out.println("Currently Playing:");
		    System.out.println("-------------------------------------------------------");
		    System.out.println(rtInfo.getLed().getCurrentlyPlaying().getBlink());
		    if (rtInfo.getLed().getCurrentlyPlaying().isEndless()) 
			    System.out.println("Endlessly");
		    System.out.println("-------------------------------------------------------");
		    System.out.println(BlinkApp.getSelectedDevice().getAnimation(rtInfo.getLed().getCurrentlyPlaying().getBlink()));
	    }
	}

}
