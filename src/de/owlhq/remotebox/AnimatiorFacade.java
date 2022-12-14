package de.owlhq.remotebox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class AnimatiorFacade {
	
	private static final int MODE_EDIT_COLOR = 0;
	
	private static final int MODE_EDIT_FILTER_FRAMES = 0;
	
	private static BlinkAnimation BUILDER = null;
	
	private static int OPERATION_MODE = 0;
	
	private static JButton[] REGISTERED_BUTTONS = null;
	
	public static void createNewAnimator(BlinkAnimation.BlinkTypes type) {
		BUILDER = new BlinkAnimation();
	}
	
//	public static register
	
	private static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
		
	}

}
