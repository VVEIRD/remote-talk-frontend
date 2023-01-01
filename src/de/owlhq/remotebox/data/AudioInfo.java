package de.owlhq.remotebox.data;

import java.util.List;

public class AudioInfo {
	List<String> audio_files = null;
	RandomPlaybackInfo random_playback = null;
	AudioStatus status = null;
	
	
	
	public List<String> getAudio_files() {
		return audio_files;
	}

	public void setAudio_files(List<String> audio_files) {
		this.audio_files = audio_files;
	}

	public RandomPlaybackInfo getRandom_playback() {
		return random_playback;
	}

	public void setRandom_playback(RandomPlaybackInfo random_playback) {
		this.random_playback = random_playback;
	}

	public AudioStatus getStatus() {
		return status;
	}

	public void setStatus(AudioStatus status) {
		this.status = status;
	}

	public class AudioStatus {
		String currently_playing = null;
		List<String> queue = null;
		int queue_count = -1;
		public String getCurrently_playing() {
			return currently_playing;
		}
		public void setCurrently_playing(String currently_playing) {
			this.currently_playing = currently_playing;
		}
		public List<String> getQueue() {
			return queue;
		}
		public void setQueue(List<String> queue) {
			this.queue = queue;
		}
		public int getQueue_count() {
			return queue_count;
		}
		public void setQueue_count(int queue_count) {
			this.queue_count = queue_count;
		}
	}
	
	public class RandomPlaybackInfo {
		List<String> list = null;
		int max_interval = 3600;
		int min_interval = 900;
		String next_up = null;
		String played_at = null;
		public List<String> getList() {
			return list;
		}
		public void setList(List<String> list) {
			this.list = list;
		}
		public int getMax_interval() {
			return max_interval;
		}
		public void setMax_interval(int max_interval) {
			this.max_interval = max_interval;
		}
		public int getMin_interval() {
			return min_interval;
		}
		public void setMin_interval(int min_interval) {
			this.min_interval = min_interval;
		}
		public String getNext_up() {
			return next_up;
		}
		public void setNext_up(String next_up) {
			this.next_up = next_up;
		}
		public String getPlayed_at() {
			return played_at;
		}
		public void setPlayed_at(String played_at) {
			this.played_at = played_at;
		}
	}
}
