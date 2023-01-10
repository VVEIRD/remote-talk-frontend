package de.owlhq.remotebox.events;

import de.owlhq.remotebox.device.RtBoxDevice;

public class RtDeviceEvent {

	public final static int DEVICE_CONNECTED = 0;
	public final static int DEVICE_DISCONNECTED = 1;
	public final static int ANIMATION_STARTED = 2;
	public final static int ANIMATION_CHANGED = 2;
	public final static int ANIMATION_STOPPED = 3;
	public final static int AUDIO_STARTED = 4;
	public final static int AUDIO_STOPPED = 5;
	public final static int VOICE_CONNECTED = 6;
	public final static int VOICE_DISCONNECTED = 7;
	
	
	// Process events

	public final static int LED_PROCESS_STARTED = 8;
	public final static int LED_PROCESS_STOPPED = 9;
	
	public final static int AUDIO_PROCESS_STARTED = 10;
	public final static int AUDIO_PROCESS_STOPPED = 11;
	
	public final static int VOICE_PROCESS_STARTED = 12;
	public final static int VOICE_PROCESS_STOPPED = 13;

	public final RtBoxDevice source;

	public final int eventType;

	public RtDeviceEvent(RtBoxDevice source, int eventType) {
		super();
		this.source = source;
		this.eventType = eventType;
	}

}
