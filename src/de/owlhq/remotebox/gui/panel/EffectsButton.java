package de.owlhq.remotebox.gui.panel;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.data.PlayEffect;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EffectsButton extends JPanel {
	private JLabel lblEditButton;

	/**
	 * Create the panel.
	 */
	public EffectsButton() {
		setBackground(Color.RED);
		setSize(new Dimension(120, 80));
		setMaximumSize(new Dimension(80, 80));
		setMinimumSize(new Dimension(80, 80));
		setLayout(null);
		
		lblEditButton = new JLabel("E");
		lblEditButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BlinkApp.editEffect(effect);
			}
		});
		lblEditButton.setHorizontalAlignment(SwingConstants.CENTER);
		lblEditButton.setVerticalAlignment(SwingConstants.BOTTOM);
		lblEditButton.setBounds(0, 65, 15, 15);
		add(lblEditButton);
		
		btnNewButton = new JButton("PLAY");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (EffectsButton.this.effect != null && BlinkApp.getSelectedDevice() != null && BlinkApp.getSelectedDevice().isReachable()) {
					if (EffectsButton.this.effect.getBlinkAnimationName() != null) {
						BlinkApp.getSelectedDevice().playAnimation(EffectsButton.this.effect.getBlinkAnimationName(), EffectsButton.this.effect.isEndlessAnimation());
					}
					if (EffectsButton.this.effect.getAudioFile() != null) {
						BlinkApp.getSelectedDevice().playAudio(EffectsButton.this.effect.getAudioFile());
					}
					
				}
			}
		});
		btnNewButton.setBounds(65, 26, 50, 50);
		btnNewButton.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton.setOpaque(false);
		add(btnNewButton);
		
		lblName = new JLabel("NAME");
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(0, 0, 120, 20);
		add(lblName);
	}
	private PlayEffect effect = null;
	
	private JButton btnNewButton;
	private JLabel lblName;
}
