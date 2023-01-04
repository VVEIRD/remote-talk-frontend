package de.owlhq.remotebox.animation;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.owlhq.remotebox.gui.panel.LedInterface;

public class BlinkAnimator implements Runnable {
	
	private BlinkAnimation ba = null;
	private LedInterface lp = null;
	private JLabel fpsCounter = null;
	private float[][][] frames = null;
	private int frameNo = 0;
	private Thread daemon = null;
	private int frameDrawn = 0;
	private int filterFrameNo = 0;
	private boolean endless = false;
	private boolean enabled = true;
	private boolean running = false;
	private int fps = 0;
	private Color[] ledState = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
	
	private int frameAdvance=1;
	
	public BlinkAnimator(BlinkAnimation ba, LedInterface lp, JLabel fpsCounter, boolean endless) {
		super();
		this.ba = ba;
		this.lp = lp;
		this.fpsCounter = fpsCounter;
		this.frames = this.ba.generate();
		this.endless = endless;
	}
	
	public BlinkAnimator(BlinkAnimation ba, LedInterface lp, boolean endless) {
		super();
		this.ba = ba;
		this.lp = lp;
		this.frames = this.ba.generate();
		this.endless = endless;
	}

	public void startAnimation() {
		this.enabled = true;
		this.frames = this.ba.generate();
		SwingUtilities.invokeLater(this);
		this.daemon = new Thread(new BlinkDaemon());
		this.daemon.setDaemon(true);
		this.daemon.start();
	}

	public void stopAnimation() {
		this.enabled = false;
		while(!this.enabled && this.running) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.daemon = null;
		this.enabled = true;
		pullColorsDown();
	}

	public boolean isDaemonRunning() {
		if (this.daemon == null)
			return false;
		return this.daemon.isAlive();
	}

	private void pullColorsDown() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int i=0;i<8;i++) {
					BlinkAnimator.this.lp.setColor(i, Color.black);
				}
			}
		});
	}
	
	private int[] getNextFilterFrame() {
		int[] ff = this.ba.getFilter_frames()[this.filterFrameNo];
		if (this.filterFrameNo + this.frameAdvance < 0)
			this.filterFrameNo = this.ba.getFilter_frames().length-1;
		else if (this.filterFrameNo + this.frameAdvance >= this.ba.getFilter_frames().length)
			this.filterFrameNo = 0;
		else 
			this.filterFrameNo += this.frameAdvance;
		return ff;
	}
	
	public void setBa(BlinkAnimation ba) {
		this.ba = ba;
		this.reset();
	}

	@Override
	public void run() {
		//System.out.println("FLength: " + this.frames.length);
		float[][] frame = frames[this.frameNo];
		int[] filterFrame = getNextFilterFrame();
		this.frameNo += this.frameAdvance;
		for (int i=0;i<frame.length;i++) {
			//System.out.print("[" + filterFrame[i] + "]");
			Color c = ledState[i];
			if (filterFrame[i] == 0) {
				c = new Color((int)(c.getRed()*(1.0f-ba.getDecay())), (int)(c.getGreen()*(1.0f-ba.getDecay())), (int)(c.getBlue()*(1.0f-ba.getDecay())));
			}
			else {
				c = new Color(frame[i][0]/255.0f, frame[i][1]/255.0f, frame[i][2]/255.0f);
			}
			ledState[i] = c;
			this.lp.setColor(i, ledState[i]);
		}
		if (fpsCounter != null) {
			fpsCounter.setText("FPS: " + this.fps);
			if (this.fps < this.ba.getFPS()-5)
				fpsCounter.setForeground(Color.RED);
			else if (this.fps < this.ba.getFPS())
				fpsCounter.setForeground(Color.ORANGE);
			else
				fpsCounter.setForeground(Color.green.darker());
		}
		//System.out.println();
		this.frameDrawn++;
	}

	public BlinkAnimation getBlinkAnimation() {
		return ba;
	}
	
	public void nextFrame() {
		if (this.frameNo >= BlinkAnimator.this.frames.length)
			this.frameNo = 0;
		this.frameAdvance = 1;
		SwingUtilities.invokeLater(BlinkAnimator.this);
	}
	
	public void previousFrame() {
		if (this.frameNo < 0)
			this.frameNo = BlinkAnimator.this.frames.length-1;
		this.frameAdvance = -1;
		SwingUtilities.invokeLater(BlinkAnimator.this);
	}
	
	private class BlinkDaemon implements Runnable {

		@Override
		public void run() {
			int frameTime = (1000/BlinkAnimator.this.ba.getFPS());
			long lastFrameDrawn = System.currentTimeMillis();
			int lastFrameCount = BlinkAnimator.this.frameDrawn;
			int locFramesDrawn = 0;
			do {
				BlinkAnimator.this.running = true;
				if (BlinkAnimator.this.enabled) {
					BlinkAnimator.this.frameNo = 0;
					while(BlinkAnimator.this.frameNo < BlinkAnimator.this.frames.length && BlinkAnimator.this.enabled) {
						long startTime = System.currentTimeMillis();
						// Invoke Animation
						SwingUtilities.invokeLater(BlinkAnimator.this);
						long endTime = System.currentTimeMillis();
						//System.out.println("RGB [" + frameNo + "]: " + frames[frameNo][0][0] + ":" + frames[frameNo][1][0] + ":" + frames[frameNo][2][0]); 
						try {	
							Thread.sleep(frameTime-(endTime-startTime) > 0 ? frameTime-(endTime-startTime) : 0);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (lastFrameCount != BlinkAnimator.this.frameDrawn) {
							locFramesDrawn += BlinkAnimator.this.frameDrawn-lastFrameCount;
							lastFrameCount = BlinkAnimator.this.frameDrawn;
						}
						if (locFramesDrawn>30) {
							long currentTime = System.currentTimeMillis();
							int fps = (int) (1000/((currentTime - lastFrameDrawn)/locFramesDrawn));
							BlinkAnimator.this.fps = fps;
							lastFrameDrawn = currentTime;
							locFramesDrawn = 0;
						}
					}
				}
			} while (BlinkAnimator.this.endless && BlinkAnimator.this.enabled);
			BlinkAnimator.this.running = false;
			BlinkAnimator.this.enabled = true;
		}
		
	}

	public void reset() {
		this.filterFrameNo = 0;
		this.frameDrawn = 0;
		this.frameNo = 0;
		this.frames = this.ba.generate();
		this.pullColorsDown();
	}
}
