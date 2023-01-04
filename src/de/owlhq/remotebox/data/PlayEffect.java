package de.owlhq.remotebox.data;

public class PlayEffect {
	
	private String name = null;

	private String blinkAnimationName = null;
	
	private boolean endlessAnimation = false;
	
	private String audioFile = null;
	
	public PlayEffect() {
	}

	public PlayEffect(String name, String blinkAnimationName, boolean endlessAnimation, String audioFile) {
		super();
		this.name = name;
		this.blinkAnimationName = blinkAnimationName;
		this.endlessAnimation = endlessAnimation;
		this.audioFile = audioFile;
	}

	public String getAnimationName() {
		return blinkAnimationName;
	}

	public void setBlinkAnimationName(String blinkAnimationName) {
		this.blinkAnimationName = blinkAnimationName;
	}

	public boolean isEndlessAnimation() {
		return endlessAnimation;
	}

	public void setEndlessAnimation(boolean endlessAnimation) {
		this.endlessAnimation = endlessAnimation;
	}

	public String getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
