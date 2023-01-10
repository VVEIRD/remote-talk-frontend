package de.owlhq.remotebox.data.info;

import java.util.Map;

public class RtBoxInfo {
	
	boolean reachable = true;
	
	AudioInfo audio = null;
	
	LedInfo led = null;
	
	VoiceInfo voice = null;
	
	Map<String, String> endpoints = null;
	
	Map<String, String> processes = null;

	public AudioInfo getAudio() {
		return audio;
	}

	public void setAudio(AudioInfo audio) {
		this.audio = audio;
	}

	public LedInfo getLed() {
		return led;
	}

	public void setLed(LedInfo led) {
		this.led = led;
	}

	public VoiceInfo getVoice() {
		return voice;
	}

	public void setVoice(VoiceInfo voice) {
		this.voice = voice;
	}
	
	public void setEndpoints(Map<String, String> endpoints) {
		this.endpoints = endpoints;
	}
	
	public void setProcesses(Map<String, String> processes) {
		this.processes = processes;
	}
	
	public Map<String, String> getEndpoints() {
		return endpoints;
	}
	
	public Map<String, String> getProcesses() {
		return processes;
	}
	
	public boolean isReachable() {
		return reachable;
	}
	
	public void setReachable(boolean reachable) {
		this.reachable = reachable;
	}
	
	// ----------------------------------------------------------------------------------------------------------
	// -- AUDIO Helper Functions
	// ----------------------------------------------------------------------------------------------------------
	
	public boolean hasAudioStarted(RtBoxInfo oldInfo) {
		boolean oldPlaybackState = false;
		boolean currentPlaybackState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getAudio() != null && oldInfo.getAudio().getStatus() != null) {
			oldPlaybackState = oldInfo.getAudio().getStatus().getCurrently_playing() != null;
		}
		// Determine current state
		if (this.getAudio() != null && this.getAudio().getStatus() != null) {
			currentPlaybackState = this.getAudio().getStatus().getCurrently_playing() != null;
		}
		return !oldPlaybackState && currentPlaybackState;
	}
	
	public boolean hasAudioChanged(RtBoxInfo oldInfo) {
		String oldCurrentlyPlaying = "";
		String newCurrentlyPlaying = "";
		String oldRandomPlayback = "";
		String newRandomPlayback = "";
		int oldQueueLength = 0;
		int newQueueLength = 0;
		// Determine old state
		if (oldInfo != null && oldInfo.getAudio() != null && oldInfo.getAudio().getStatus() != null) {
			oldCurrentlyPlaying = oldInfo.getAudio().getStatus().getCurrently_playing();
			oldRandomPlayback = oldInfo.getAudio().getRandom_playback().getStatus();
			oldQueueLength = oldInfo.getAudio().getStatus().getQueue_count();
		}
		// Determine current state
		if (this.getAudio() != null && this.getAudio().getStatus() != null) {
			newCurrentlyPlaying = this.getAudio().getStatus().getCurrently_playing();
			newRandomPlayback = this.getAudio().getRandom_playback().getStatus();
			newQueueLength = this.getAudio().getStatus().getQueue_count();
		}
		return oldCurrentlyPlaying == null && newCurrentlyPlaying != null 
				|| oldCurrentlyPlaying != null && !oldCurrentlyPlaying.equals(newCurrentlyPlaying) 
				|| !oldRandomPlayback.equals(newRandomPlayback) 
				|| oldQueueLength != newQueueLength;
	}
	
	public boolean hasRandomAudioChanged(RtBoxInfo oldInfo) {

		String oldRandomPlayback = "";
		String newRandomPlayback = "";
		// Determine old state
		if (oldInfo != null && oldInfo.getAudio() != null && oldInfo.getAudio().getStatus() != null) {
			oldRandomPlayback = oldInfo.getAudio().getRandom_playback().getStatus();
		}
		// Determine current state
		if (this.getAudio() != null && this.getAudio().getStatus() != null) {
			newRandomPlayback = this.getAudio().getRandom_playback().getStatus();
		}
		return !oldRandomPlayback.equals(newRandomPlayback) ;
	}
	
	public boolean hasAudioStopped(RtBoxInfo oldInfo) {
		boolean oldPlaybackState = false;
		boolean currentPlaybackState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getAudio() != null && oldInfo.getAudio().getStatus() != null) {
			oldPlaybackState = oldInfo.getAudio().getStatus().getCurrently_playing() != null;
		}
		// Determine current state
		if (this.getAudio() != null && this.getAudio().getStatus() != null) {
			currentPlaybackState = this.getAudio().getStatus().getCurrently_playing() != null;
		}
		return oldPlaybackState && !currentPlaybackState;
	}
	
	public boolean isPlayingAudio() {
		return this.getAudio().getStatus() != null && this.getAudio().getStatus().getCurrently_playing() != null;
	}
	
	public boolean isPlayingAnimation() {
		return this.getLed() != null && this.getLed().getCurrentlyPlaying() != null && this.getLed().getCurrentlyPlaying().getBlink() != null;
	}

	public boolean isVoiceConnected() {
		return this.getVoice() != null && "connected".equalsIgnoreCase(this.getVoice().getStatus());
	}
	
	// ----------------------------------------------------------------------------------------------------------
	// -- VOICE Helper Functions
	// ----------------------------------------------------------------------------------------------------------
	
	public boolean hasVoiceConnected(RtBoxInfo oldInfo) {
		boolean oldVoiceState = false;
		boolean currentVoiceState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getVoice() != null) {
			oldVoiceState = "connected".equals(oldInfo.getVoice().getStatus());
		}
		// Determine current state
		if (this.getVoice() != null) {
			currentVoiceState = "connected".equals(this.getVoice().getStatus());
		}
		return !oldVoiceState && currentVoiceState;
	}
	
	public boolean hasVoiceChanged(RtBoxInfo oldInfo) {
		String oldVoiceServer = "";
		String oldVoiceUser = "";
		int oldVoicePort = -1;
		String newVoiceServer = "";
		String newVoiceUser = "";
		int newVoicePort = -1;
		// Determine old state
		if (oldInfo != null && oldInfo.getVoice() != null) {
			oldVoiceServer = oldInfo.getVoice().getHost();
			oldVoiceUser = oldInfo.getVoice().getName();
			oldVoicePort = oldInfo.getVoice().getPort();
		}
		// Determine current state
		if (this.getVoice() != null) {
			newVoiceServer = this.getVoice().getHost();
			newVoiceUser = this.getVoice().getName();
			newVoicePort = this.getVoice().getPort();
		}
		return !oldVoiceServer.equals(newVoiceServer) || oldVoicePort != newVoicePort || !oldVoiceUser.equals(newVoiceUser);
	}
	
	public boolean hasVoiceDisconnected(RtBoxInfo oldInfo) {
		boolean oldVoiceState = false;
		boolean currentVoiceState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getVoice() != null) {
			oldVoiceState = oldInfo.getVoice().getStatus().equals("connected");
		}
		// Determine current state
		if (this.getVoice() != null) {
			currentVoiceState = this.getVoice().getStatus().equals("connected");
		}
		return oldVoiceState && !currentVoiceState;
	}
	
	// ----------------------------------------------------------------------------------------------------------
	// -- LED Animation Helper Functions
	// ----------------------------------------------------------------------------------------------------------
	
	public boolean hasAnimationStarted(RtBoxInfo oldInfo) {
		boolean oldAnimationState = false;
		boolean currentAnimationState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getLed() != null && oldInfo.getLed().getCurrentlyPlaying() != null) {
			oldAnimationState = oldInfo.getLed().getCurrentlyPlaying().getBlink() != null;
		}
		// Determine current state
		if (this.getLed() != null && this.getLed().getCurrentlyPlaying() != null) {
			currentAnimationState = this.getLed().getCurrentlyPlaying().getBlink() != null;
		}
		return !oldAnimationState && currentAnimationState;
	}
	
	public boolean hasAnimationChanged(RtBoxInfo oldInfo) {
		String oldCurrentlyPlaying = null;
		String newCurrentlyPlaying = null;
		boolean oldEndles = false;
		boolean newEndles = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getLed() != null && oldInfo.getLed().getCurrentlyPlaying() != null) {
			oldCurrentlyPlaying = oldInfo.getLed().getCurrentlyPlaying().getBlink();
			oldEndles = oldInfo.getLed().getCurrentlyPlaying().isEndless();
		}
		// Determine current state
		if (this.getLed() != null && this.getLed().getCurrentlyPlaying() != null) {
			newCurrentlyPlaying = this.getLed().getCurrentlyPlaying().getBlink();
			newEndles = this.getLed().getCurrentlyPlaying().isEndless();
		}
		return oldCurrentlyPlaying == null && oldCurrentlyPlaying != null 
				|| oldCurrentlyPlaying != null && oldCurrentlyPlaying == null 
				|| oldCurrentlyPlaying != null && !oldCurrentlyPlaying.equals(newCurrentlyPlaying) 
				|| oldEndles != newEndles;
	}
	
	public boolean hasAnimationStopped(RtBoxInfo oldInfo) {
		boolean oldAnimationState = false;
		boolean currentAnimationState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getLed() != null && oldInfo.getLed().getCurrentlyPlaying() != null) {
			oldAnimationState = oldInfo.getLed().getCurrentlyPlaying().getBlink() != null;
		}
		// Determine current state
		if (this.getLed() != null && this.getLed().getCurrentlyPlaying() != null) {
			currentAnimationState = this.getLed().getCurrentlyPlaying().getBlink() != null;
		}
		return oldAnimationState && !currentAnimationState;
	}

	public boolean isAudioEndpointOnline() {
		return "online".equals(this.processes.get("audio"));
	}

	public boolean isLedEndpointOnline() {
		return "online".equals(this.processes.get("led"));
	}

	public boolean isVoiceEndpointOnline() {
		return "online".equals(this.processes.get("voice"));
	}
	
	public String getAudioEndpoint() {
		return this.endpoints.get("audio");
	}
	
	public String getLedEndpoint() {
		return this.endpoints.get("led");
	}
	
	public String getVoiceEndpoint() {
		return this.endpoints.get("voice");
	}

}
