package de.owlhq.remotebox;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public class BlinkAnimation {

	public static enum BlinkTypes {
		
		@SerializedName("1")
		PULSE(1), 
		@SerializedName("2")
		MORPH(2),
		@SerializedName("3")
		DECAY(3);

		private final int value;

		BlinkTypes(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	
	}

	private BlinkTypes type = BlinkTypes.PULSE;
	
	private int duration_ms = 1000;
	
	private int loop = 1;
	
	private int FPS = 30;
	
	private float brightnes = 0.9f;
	
	private int led_count = 8;
	
	private float decay = 0.25f;
	
	private int[][] filter_frames = {{1, 1, 1, 1, 1, 1, 1, 1}};

	private String[] color_source = {"#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
	
	private String[] color_target = {"#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
	
	public BlinkAnimation() {
	}

	public BlinkAnimation(BlinkTypes type, int duration_ms, int loop, int fPS, float brightnes, float decay, int led_count, String[] color_source, String[] color_target) {
		super();
		this.type = type;
		this.duration_ms = duration_ms;
		this.loop = loop;
		FPS = fPS;
		this.brightnes = brightnes;
		this.led_count = led_count;
		this.color_source = color_source;
		this.color_target = color_target;
		this.decay = decay;
	}

	public BlinkAnimation(BlinkTypes type, int duration_ms, int loop, int fPS, float brightnes, float decay, int led_count,
			int[][] filter_frames, String[] color_source, String[] color_target) {
		super();
		this.type = type;
		this.duration_ms = duration_ms;
		this.loop = loop;
		FPS = fPS;
		this.brightnes = brightnes;
		this.led_count = led_count;
		this.filter_frames = filter_frames;
		this.color_source = color_source;
		this.color_target = color_target;
		this.decay = decay;
	}

	public BlinkTypes getType() {
		return type;
	}

	public void setType(BlinkTypes type) {
		this.type = type;
	}

	public int getDuration_ms() {
		return duration_ms;
	}

	public void setDuration_ms(int duration_ms) {
		this.duration_ms = duration_ms;
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {
		this.loop = loop;
	}

	public int getFPS() {
		return FPS;
	}

	public void setFPS(int fPS) {
		FPS = fPS;
	}

	public float getBrightnes() {
		return brightnes;
	}
	
	public float getDecay() {
		return decay;
	}
	
	public void setDecay(float decay) {
		this.decay = decay;
	}

	public void setBrightnes(float brightnes) {
		this.brightnes = brightnes;
	}

	public int getLed_count() {
		return led_count;
	}

	public void setLed_count(int led_count) {
		if (led_count <= 0)
			throw new IllegalArgumentException("led_count must be a positive integer");
		this.led_count = led_count;
	}

	public int[][] getFilter_frames() {
		return filter_frames;
	}

	public void setFilter_frames(int[][] filter_frames) {
		this.filter_frames = Objects.requireNonNull(filter_frames, "filter_frames cannot be null");
	}

	public String[] getColor_source() {
		return color_source;
	}

	public void setColor_source(String[] color_source) {
		this.color_source = color_source;
	}

	public String[] getColor_target() {
		return color_target;
	}

	public void setColor_target(String[] color_target) {
		this.color_target = color_target;
	}
	
	
	public float[][][] generate() {
		switch (this.type) {
		case PULSE:
			return generatePulse();
		case MORPH:
			return generateMorph(false, true);
		default:
			return null;
		}
	}
	
	private float[][][] generatePulse() {
		System.out.println("Generating pulse");
    	float[][][] frames = new float[0][0][0];
        int duration = this.duration_ms;
		this.duration_ms = this.duration_ms/2;
    	for (int i=0;i<this.loop;i++) {
    		float[][][] g1 = this.generateMorph(false, false);
    		System.out.println("Generated morph 1, Length: " + g1.length);
    		float[][][] g2 = this.generateMorph(true, false);
    		System.out.println("Generated morph 2, Length: " + g2.length);
    		float[][][] nFrames = new float[g1.length+g2.length+frames.length][][];
    		for (int n=0;n<frames.length;n++) {
    			nFrames[n] = frames[n];
    		}
    		for (int n=0;n<g1.length;n++) {
    			nFrames[n+frames.length] = g1[n];
    		}
    		for (int n=0;n<g2.length;n++) {
    			nFrames[n+frames.length+g1.length] = g2[n];
    		}
    		frames = nFrames;
    	}
    	this.duration_ms = duration;
		return frames;
	}
	
	private float[][][] generateMorph(boolean reverse, boolean loop) {
		int loop_count = loop ? this.loop : 1;
		String[] target = !reverse ? this.color_target : this.color_source;
		String[] source = !reverse ? this.color_source : this.color_target;
		Color[] target_color = Arrays.stream(target).map(x -> Color.decode(x)).toArray(size -> new Color[size]);
		Color[] source_color = Arrays.stream(source).map(x -> Color.decode(x)).toArray(size -> new Color[size]);
		int steps = (this.getDuration_ms() / (1000/this.getFPS()));
		float[][][] gradient = new float[steps*loop_count][source_color.length][3];
		for (int n=0;n<loop_count;n++) {
			for (int i=0;i<steps;i++) {
				float[][] leds = new float[led_count][3];
				float d = 1.0f * i / steps;
				System.out.println("D: " + d);
				for (int l=0;l<led_count;l++) {
					float r_start = source_color[l].getRed();
					float g_start = source_color[l].getGreen();
					float b_start = source_color[l].getBlue();
					float r_end =   target_color[l].getRed();
					float g_end =   target_color[l].getGreen();
					float b_end =   target_color[l].getBlue();
					float r, g, b = 0;
					if (i == steps-1) {
                        r = r_end * this.brightnes;
                        g = g_end * this.brightnes;
                        b = b_end * this.brightnes;
					}
					else {
                        r = ((r_start * (1.0f - d)) + (r_end * d)) * this.brightnes;
                        g = ((g_start * (1.0f - d)) + (g_end * d)) * this.brightnes;
                        b = ((b_start * (1.0f - d)) + (b_end * d)) * this.brightnes;
					}
					leds[l][0] = r;
					leds[l][1] = g;
					leds[l][2] = b;
				}
				gradient[i+(steps*n)] = Arrays.copyOf(leds, leds.length);
			}
		}
		return gradient;
	}		
    
    static <T> T concatWithCopy(T array1, T array2) {
        if (!array1.getClass().isArray() || !array2.getClass().isArray()) {
            throw new IllegalArgumentException("Only arrays are accepted.");
        }

        Class<?> compType1 = array1.getClass().getComponentType();
        Class<?> compType2 = array2.getClass().getComponentType();

        if (!compType1.equals(compType2)) {
            throw new IllegalArgumentException("Two arrays have different types.");
        }

        int len1 = Array.getLength(array1);
        int len2 = Array.getLength(array2);

        @SuppressWarnings("unchecked")
        //the cast is safe due to the previous checks
        T result = (T) Array.newInstance(compType1, len1 + len2);

        System.arraycopy(array1, 0, result, 0, len1);
        System.arraycopy(array2, 0, result, len1, len2);

        return result;
    }
	
}
