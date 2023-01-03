package de.owlhq.remotebox.data.info;

public class RtBoxInfo {
	AudioInfo audio = null;
	
	LedInfo led = null;
	
	VoiceInfo voice = null;

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
	
	// ----------------------------------------------------------------------------------------------------------
	// -- VOICE Helper Functions
	// ----------------------------------------------------------------------------------------------------------
	
	public boolean hasVoiceConnected(RtBoxInfo oldInfo) {
		boolean oldVoiceState = false;
		boolean currentVoiceState = false;
		// Determine old state
		if (oldInfo != null && oldInfo.getVoice() != null) {
			oldVoiceState = oldInfo.getVoice().getStatus() == "connected";
		}
		// Determine current state
		if (this.getVoice() != null) {
			currentVoiceState = this.getVoice().getStatus() == "connected";
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

}
