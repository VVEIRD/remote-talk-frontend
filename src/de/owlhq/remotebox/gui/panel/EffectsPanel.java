package de.owlhq.remotebox.gui.panel;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.data.PlayEffect;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class EffectsPanel extends JPanel {
	
	private JLabel lblEditButton;
	
	private PlayEffect effect = null;
	
	private JButton btnPlay = null;
	
	private JLabel lblName = null;
	private JLabel lblAudio;
	private JLabel lblEffect;

	public EffectsPanel() {
		this(null);
	}
	/**
	 * Create the panel.
	 */
	public EffectsPanel(PlayEffect effect) {
		this.effect = effect != null ? effect : new PlayEffect("Nameless", null, false, null);
		BlinkAnimation ba = null;
		if (this.effect.getAnimationName() != null) {
			ba = BlinkApp.getAnimation(this.effect.getAnimationName());
			// Fallback get animation from device
			if (ba == null && BlinkApp.getSelectedDevice() != null && BlinkApp.getSelectedDevice().isReachable()) {
				ba = BlinkApp.getSelectedDevice().getAnimation(this.effect.getAnimationName());
			}
		}
		if (ba != null) {
			Color c = ba.getPrimaryTargetColor();
			if (c == null)
				c = getBackground().darker();
			setBackground(c);
		}
		setSize(new Dimension(120, 120));
		setMaximumSize(new Dimension(120, 120));
		setMinimumSize(new Dimension(120, 120));
		setPreferredSize(new Dimension(120, 120));
		setLayout(null);
		
		
		lblEditButton = new JLabel("E");
		lblEditButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				EffectsPanel.this.effect = BlinkApp.editEffect(EffectsPanel.this.effect);
				lblName.setText(EffectsPanel.this.effect.getName());
				lblEffect.setText(EffectsPanel.this.effect.getAnimationName());
				lblAudio.setText(EffectsPanel.this.effect.getAudioFile());
			}
		});
		lblEditButton.setHorizontalAlignment(SwingConstants.CENTER);
		lblEditButton.setVerticalAlignment(SwingConstants.BOTTOM);
		lblEditButton.setBounds(0, 105, 15, 15);
		add(lblEditButton);
		
		btnPlay = new JButton("PLAY");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (EffectsPanel.this.effect != null && BlinkApp.getSelectedDevice() != null && BlinkApp.getSelectedDevice().isReachable()) {
					BlinkApp.getSelectedDevice().playEffect(EffectsPanel.this.effect);
				}
			}
		});
		btnPlay.setBounds(65, 66, 50, 50);
		btnPlay.setMargin(new Insets(0, 0, 0, 0));
		btnPlay.setOpaque(false);
		add(btnPlay);
		
		lblName = new JLabel(this.effect.getName());
		lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(0, 0, 120, 20);
		add(lblName);
		
		lblAudio = new JLabel(this.effect.getAudioFile() == null ? "" : this.effect.getAudioFile());
		lblAudio.setBounds(2, 50, 115, 15);
		add(lblAudio);
		
		lblEffect = new JLabel(this.effect.getAnimationName());
		lblEffect.setBounds(2, 30, 115, 15);
		add(lblEffect);
	}
}
