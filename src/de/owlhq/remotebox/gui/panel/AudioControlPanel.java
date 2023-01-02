package de.owlhq.remotebox.gui.panel;

import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.SpringLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class AudioControlPanel extends JPanel {
	private JLabel lblNextUp;
	private JLabel lblCurrentyPlaying;
	private JLabel lblStartingAt;
	private JList listQueue;
	private JLabel lblRndPlaybackEnabled;

	/**
	 * Create the panel.
	 */
	public AudioControlPanel() {

		setSize(new Dimension(730, 220));
		setPreferredSize(new Dimension(730, 175));
		setMinimumSize(new Dimension(730, 220));
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("243px"),
				ColumnSpec.decode("195px:grow"),
				ColumnSpec.decode("5px"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("279px"),},
			new RowSpec[] {
				RowSpec.decode("175px"),}));
		
		JPanel pnRandom = new JPanel();
		pnRandom.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		add(pnRandom, "5, 1, fill, fill");
		pnRandom.setLayout(null);
		
		JButton btnRandomStop = new JButton("STOP");
		btnRandomStop.setBounds(10, 38, 80, 80);
		pnRandom.add(btnRandomStop);
		
		JButton btnRandomEnable = new JButton("ENABLE");
		btnRandomEnable.setBounds(100, 38, 80, 80);
		pnRandom.add(btnRandomEnable);
		
		JButton btnRandomDisable = new JButton("DISABLE");
		btnRandomDisable.setBounds(190, 38, 80, 80);
		pnRandom.add(btnRandomDisable);
		
		JLabel lblNewLabel = new JLabel("Random Playback");
		lblNewLabel.setBounds(10, 11, 170, 14);
		pnRandom.add(lblNewLabel);
		
		lblRndPlaybackEnabled = new JLabel("Enabled");
		lblRndPlaybackEnabled.setForeground(Color.GREEN);
		lblRndPlaybackEnabled.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRndPlaybackEnabled.setBounds(190, 13, 79, 14);
		pnRandom.add(lblRndPlaybackEnabled);
		
		JLabel lblNewLabel_2 = new JLabel("Next Up:");
		lblNewLabel_2.setBounds(10, 129, 60, 14);
		pnRandom.add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("Starting at:");
		lblNewLabel_2_1.setBounds(10, 154, 60, 14);
		pnRandom.add(lblNewLabel_2_1);
		
		lblNextUp = new JLabel("NOTHING");
		lblNextUp.setBounds(100, 129, 170, 14);
		pnRandom.add(lblNextUp);
		
		lblStartingAt = new JLabel("2023-01-01T12:10:00");
		lblStartingAt.setBounds(100, 154, 170, 14);
		pnRandom.add(lblStartingAt);
		
		JPanel panelAudio = new JPanel();
		panelAudio.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		add(panelAudio, "1, 1, 3, 1, fill, fill");
		panelAudio.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("80px"),
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("80px"),
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("243px:grow"),
				FormSpecs.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("24px"),
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				RowSpec.decode("80px"),
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("14px"),
				FormSpecs.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("12px"),}));
		
		JLabel lblAudioPlayback = new JLabel("Audio Playback");
		panelAudio.add(lblAudioPlayback, "2, 2, 3, 1, fill, top");
		
		JButton btnStopCurrent_1 = new JButton("STOP");
		panelAudio.add(btnStopCurrent_1, "2, 4, fill, fill");
		
		JButton btnFlushQueue = new JButton("FLUSH Q");
		panelAudio.add(btnFlushQueue, "4, 4, fill, fill");
		
		JLabel lblNewLabel_2_2 = new JLabel("Currently Playing:");
		panelAudio.add(lblNewLabel_2_2, "2, 6, 3, 1, fill, top");
		
		listQueue = new JList(new DefaultListModel<>());
		panelAudio.add(listQueue, "6, 4, 1, 5, fill, fill");
		
		JLabel lblNewLabel_2_2_1 = new JLabel("Queue");
		panelAudio.add(lblNewLabel_2_2_1, "6, 2, fill, bottom");
		
		lblCurrentyPlaying = new JLabel("NOTHING");
		panelAudio.add(lblCurrentyPlaying, "2, 8, 3, 1, fill, top");
	}
	
	public void updateQueue(List<String> newQueue) {
		DefaultListModel listmodel = (DefaultListModel)listQueue.getModel();
		listmodel.removeAllElements();
		for (String queue : newQueue) {
			listmodel.addElement(queue);
		}
	}

	public void updateCurrentlyPlaying(String currently_playing) {
		lblCurrentyPlaying.setText(currently_playing);
	}

	public void updateRandomPlaybackEnabled(boolean enabled) {
		lblRndPlaybackEnabled.setText(enabled ? "Enabled" : "Disabled");
		lblRndPlaybackEnabled.setForeground(enabled ? Color.GREEN.darker() : Color.BLACK);
	}

	public void updateRandomPlaybackNextUp(String next_up) {
		lblNextUp.setText(next_up);
	}

	public void updateRandomPlaybackNextUpTime(String played_at) {
		lblStartingAt.setText(played_at);
	}
}
