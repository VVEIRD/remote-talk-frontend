package de.owlhq.remotebox.gui.frame;

import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JTabbedPane;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.device.RtBoxDevice;
import de.owlhq.remotebox.events.RtDeviceEvent;
import de.owlhq.remotebox.events.RtDeviceListener;
import de.owlhq.remotebox.gui.panel.AnimationDialog;
import java.awt.BorderLayout;
import de.owlhq.remotebox.gui.panel.AudioControlPanel;
import de.owlhq.remotebox.gui.panel.LedControlPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import de.owlhq.remotebox.gui.panel.DashboardPanel;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JComboBox cbDevice;
	private JButton btnAddCustomDevice;
	private AudioControlPanel audioControlPanel;
	private LedControlPanel ledControlPanel;
	private JLabel lblDeviceReachable;
	private DashboardPanel dashboardPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 820);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px:grow"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("20px"),}));
		
		lblDeviceReachable = new JLabel("Device unreachable");
		lblDeviceReachable.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDeviceReachable.setForeground(Color.RED);
		contentPane.add(lblDeviceReachable, "2, 2, 3, 1");
		
		JLabel lblNewLabel = new JLabel("Selected Device");
		contentPane.add(lblNewLabel, "14, 2, 7, 1");
		
		cbDevice = new JComboBox();
		cbDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BlinkApp.selectDevice((String)cbDevice.getSelectedItem());
			}
		});
		for(String name: BlinkApp.getDeviceNames()) {
			cbDevice.addItem(name);
		}
		if(BlinkApp.getSelectedDevice() != null)
			cbDevice.setSelectedItem(BlinkApp.getSelectedDevice().getDeviceName());
		BlinkApp.addRtDeviceListener(new RtDeviceListener() {
			
			@Override
			public void deviceChange(RtDeviceEvent e) {
				if (e.eventType == RtDeviceEvent.DEVICE_CONNECTED)
					cbDevice.addItem(e.source.getDeviceName());
				else {
					cbDevice.removeAllItems();
					for(String name: BlinkApp.getDeviceNames()) {
						cbDevice.addItem(name);
					}
				}
			}
		});
		contentPane.add(cbDevice, "22, 2, 13, 1, fill, default");
		
		btnAddCustomDevice = new JButton("+");
		btnAddCustomDevice.setMargin(new Insets(0, 0, 0, 0));
		contentPane.add(btnAddCustomDevice, "36, 2");
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, "2, 4, 35, 33, fill, fill");
		
		JPanel pnCommandCenter = new JPanel();
		tabbedPane.addTab("Command Center", null, pnCommandCenter, null);
		pnCommandCenter.setLayout(new BorderLayout(0, 0));
		
		audioControlPanel = new AudioControlPanel();
		pnCommandCenter.add(audioControlPanel, BorderLayout.NORTH);
		
		ledControlPanel = new LedControlPanel();
		pnCommandCenter.add(ledControlPanel, BorderLayout.EAST);
		
		dashboardPanel = new DashboardPanel();
		pnCommandCenter.add(dashboardPanel, BorderLayout.CENTER);
		
		AnimationDialog animationDialog = new AnimationDialog();
		tabbedPane.addTab("Create Animation", null, animationDialog, null);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Configuration", null, panel, null);
		BlinkApp.addRtDeviceListener(new RtDeviceListener() {
			
			@Override
			public void deviceChange(RtDeviceEvent e) {
				updateInfoDaemon(e);
			}
		});
		// Populate Data with current state
		if (BlinkApp.getSelectedDevice() != null) {
			BlinkApp.getSelectedDevice().getStatus();
			updateDeviceReachable(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.DEVICE_CONNECTED));
			if (BlinkApp.getSelectedDevice().getLastStatus() != null && BlinkApp.getSelectedDevice().getLastStatus().isPlayingAnimation())
				updateAnimation(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.ANIMATION_STARTED));
			else
				updateAnimation(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.ANIMATION_STOPPED));
			if (BlinkApp.getSelectedDevice().getLastStatus() != null && BlinkApp.getSelectedDevice().getLastStatus().isPlayingAudio())
				updateAudio(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.AUDIO_STARTED));
			else
				updateAudio(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.AUDIO_STOPPED));
		}
	}
	
	private void updateInfoDaemon(RtDeviceEvent e) {
		updateAudio(e);
		updateAnimation(e);
		updateDeviceReachable(e);
	}

	private void updateDeviceReachable(RtDeviceEvent e) {
		if(e.source != null) {
			String lastState = lblDeviceReachable.getText();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					lblDeviceReachable.setText(e.source.isReachable() ? "Device online" : "Device offline");
					lblDeviceReachable.setForeground(e.source.isReachable() ? Color.GREEN.darker() : Color.RED);
				}
			});
			// Populate Data with current state
			if (e.source.isReachable() && lastState.equals("Device offline")) {
				if (BlinkApp.getSelectedDevice().getLastStatus() != null && BlinkApp.getSelectedDevice().getLastStatus().isPlayingAnimation())
					updateAnimation(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.ANIMATION_STARTED));
				else
					updateAnimation(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.ANIMATION_STOPPED));
				if (BlinkApp.getSelectedDevice().getLastStatus() != null && BlinkApp.getSelectedDevice().getLastStatus().isPlayingAudio())
					updateAudio(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.AUDIO_STARTED));
				else
					updateAudio(new RtDeviceEvent(BlinkApp.getSelectedDevice(), RtDeviceEvent.AUDIO_STOPPED));
			}
		}
		else {
			System.out.println("Taking device offline, source is null");
			System.out.println("Event Type: " + e.eventType);
			lblDeviceReachable.setText("Device offline");
			lblDeviceReachable.setForeground(Color.RED);
		}
	}

	private void updateAnimation(RtDeviceEvent e) {
		if (e.source != null && (e.eventType == RtDeviceEvent.ANIMATION_CHANGED || (e.eventType == RtDeviceEvent.DEVICE_CONNECTED && e.source.isSelectedDevice())) ) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					RtBoxDevice dev = e.source;
					if (dev.isReachable() && e.source.getLastStatus() != null && e.source.getLastStatus().getLed() != null && e.source.getLastStatus().getLed().getCurrentlyPlaying() != null) {
						String animationName = e.source.getLastStatus().getLed().getCurrentlyPlaying().getBlink();
						boolean endless = e.source.getLastStatus().getLed().getCurrentlyPlaying().isEndless();
						BlinkAnimation anim = null;
						if (!"stop".equals(animationName))
							anim = BlinkApp.getAnimation(animationName);
						// Fallback to device data if animation was not stored locally
						if (anim == null && !"stop".equals(animationName)) {
							anim = dev.getAnimation(animationName);
						}
						if (anim != null) {
							ledControlPanel.playAnimation(anim, endless);
							ledControlPanel.setAnimationName(animationName);
							ledControlPanel.setAnimationEndless(endless);
						}
					}
				}
			});
		}
		else if (e.eventType == RtDeviceEvent.ANIMATION_STOPPED) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					ledControlPanel.stopAnimation();
					ledControlPanel.setAnimationName("NOTHING");
					ledControlPanel.setAnimationEndless(false);
				}
			});
		}
	}

	private void updateAudio(RtDeviceEvent e) {
		if (e.eventType == RtDeviceEvent.AUDIO_STARTED) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(e.source != null && e.source.getLastStatus() != null && e.source.getLastStatus().isPlayingAudio()) {
						audioControlPanel.updateCurrentlyPlaying(e.source.getLastStatus().getAudio().getStatus().getCurrently_playing());
						audioControlPanel.updateQueue(e.source.getLastStatus().getAudio().getStatus().getQueue());
						if(e.source.getLastStatus().getAudio().getRandom_playback() != null) {
							audioControlPanel.updateRandomPlaybackEnabled("enabled".equals(e.source.getLastStatus().getAudio().getRandom_playback().getStatus()));
							audioControlPanel.updateRandomPlaybackNextUp(e.source.getLastStatus().getAudio().getRandom_playback().getNext_up());
							audioControlPanel.updateRandomPlaybackNextUpTime(e.source.getLastStatus().getAudio().getRandom_playback().getPlayed_at());
						}
					}
					else {
						audioControlPanel.updateCurrentlyPlaying("NOTHING");
						audioControlPanel.updateQueue(new LinkedList<>());
						if(e.source.getLastStatus() != null && e.source.getLastStatus().getAudio() != null && e.source.getLastStatus().getAudio().getRandom_playback() != null) {
							audioControlPanel.updateRandomPlaybackEnabled("enabled".equals(e.source.getLastStatus().getAudio().getRandom_playback().getStatus()));
							audioControlPanel.updateRandomPlaybackNextUp(e.source.getLastStatus().getAudio().getRandom_playback().getNext_up());
							audioControlPanel.updateRandomPlaybackNextUpTime(e.source.getLastStatus().getAudio().getRandom_playback().getPlayed_at());
						}
					}
				}
			});
		}
		else if (e.eventType == RtDeviceEvent.AUDIO_STOPPED) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					audioControlPanel.updateCurrentlyPlaying("NOTHING");
					if(e.source.getLastStatus() != null && e.source.getLastStatus().getAudio() != null && e.source.getLastStatus().getAudio().getRandom_playback() != null) {
						audioControlPanel.updateRandomPlaybackEnabled("enabled".equals(e.source.getLastStatus().getAudio().getRandom_playback().getStatus()));
						audioControlPanel.updateRandomPlaybackNextUp(e.source.getLastStatus().getAudio().getRandom_playback().getNext_up());
						audioControlPanel.updateRandomPlaybackNextUpTime(e.source.getLastStatus().getAudio().getRandom_playback().getPlayed_at());
					}
				}
			});
		}
	}

}
