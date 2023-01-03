package de.owlhq.remotebox.gui.panel;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.animation.BlinkAnimator;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LedControlPanel extends JPanel {
	private LedPreviewPanel ledPreviewPanel;
	
	private BlinkAnimator bA = null;
	
	private boolean enabled = true;
	private JLabel lblAnimationName;
	private JLabel lblEndless;

	/**
	 * Create the panel.
	 */
	public LedControlPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("80px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("120px"),
				RowSpec.decode("5px"),
				RowSpec.decode("fill:25px"),
				RowSpec.decode("5px"),
				RowSpec.decode("fill:80px"),
				RowSpec.decode("5px"),
				RowSpec.decode("fill:80px"),
				RowSpec.decode("5px"),
				RowSpec.decode("80px"),
				RowSpec.decode("5px:grow"),}));
		
		ledPreviewPanel = new LedPreviewPanel();
		add(ledPreviewPanel, "2, 2, 3, 1, fill, fill");
		
		JButton btnNewButton = new JButton("Disable Preview");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LedControlPanel.this.enabled = !LedControlPanel.this.enabled;
				if (!LedControlPanel.this.enabled) {
					btnNewButton.setText("Enable Preview");
				}
				else {
					btnNewButton.setText("Disable Preview");
				}
				if (LedControlPanel.this.bA != null) {
					if (LedControlPanel.this.enabled) {
						LedControlPanel.this.bA.startAnimation();
					}
					else {
						LedControlPanel.this.bA.stopAnimation();
					}
				}
			}
		});
		add(btnNewButton, "2, 4, 3, 1");
		
		JButton btnNewButton_1 = new JButton("STOP");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BlinkApp.getSelectedDevice().stopAnimationPlayback();
				BlinkApp.forceSelectedDeviceStatusUpdate();
			}
		});
		add(btnNewButton_1, "3, 6");
		
		JPanel panel = new JPanel();
		add(panel, "2, 8, 3, 3, fill, fill");
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Currently Playing");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel.setBounds(0, 0, 120, 14);
		panel.add(lblNewLabel);
		
		lblAnimationName = new JLabel("NOTHING");
		lblAnimationName.setBounds(0, 18, 120, 14);
		panel.add(lblAnimationName);
		
		JLabel lblNewLabel_2 = new JLabel("Endless");
		lblNewLabel_2.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_2.setBounds(0, 43, 46, 14);
		panel.add(lblNewLabel_2);
		
		lblEndless = new JLabel("No");
		lblEndless.setBounds(0, 60, 120, 14);
		panel.add(lblEndless);
	}
	
	public void playAnimation(BlinkAnimation animation, boolean endless) {
		if (this.bA != null) {
			this.bA.stopAnimation();
			this.bA = null;
		}
		this.bA = new BlinkAnimator(animation, ledPreviewPanel, endless);
		if (enabled)
			this.bA.startAnimation();
	}
	
	public void stopAnimation() {
		if (this.bA != null) {
			this.bA.stopAnimation();
			this.bA = null;
		}
	}

	public void setAnimationName(String animationName) {
		this.lblAnimationName.setText(animationName);
	}

	public void setAnimationEndless(boolean endless) {
		this.lblEndless.setText(endless ? "Yes" : "No");
	}
}
