package de.owlhq.remotebox.network;

import de.owlhq.remotebox.device.BlinkDevice;

public class DeviceNetworkEvent {

	public final static int DEVICE_CONNECTED = 0;
	public final static int DEVICE_DISCONNECTED = 1;

	public final BlinkDevice source;

	public final int eventType;

	public DeviceNetworkEvent(BlinkDevice source, int eventType) {
		super();
		this.source = source;
		this.eventType = eventType;
	}

}
