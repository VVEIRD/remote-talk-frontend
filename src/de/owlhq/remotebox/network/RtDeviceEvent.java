package de.owlhq.remotebox.network;

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

	public final RtBoxDevice source;

	public final int eventType;

	public RtDeviceEvent(RtBoxDevice source, int eventType) {
		super();
		this.source = source;
		this.eventType = eventType;
	}

}
