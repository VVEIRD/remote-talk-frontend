package de.owlhq.remotebox.gui.panel;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.animation.BlinkAnimation;
import de.owlhq.remotebox.animation.BlinkAnimator;
import de.owlhq.remotebox.data.info.RtBoxInfo;
import de.owlhq.remotebox.events.RtDeviceEvent;
import de.owlhq.remotebox.events.RtDeviceListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import com.jgoodies.forms.layout.FormSpecs;
import java.awt.Color;
import java.awt.EventQueue;

public class VoiceControlPanel extends JPanel {
	
	
	private boolean enabled = false;
	private JLabel lblVoiceStatus;
	private JLabel lblHost;
	private JLabel lblUsername;
	private JLabel lblPort;
	private JButton btnConnect;

	/**
	 * Create the panel.
	 */
	public VoiceControlPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("80px"),
				ColumnSpec.decode("20px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("25px"),
				RowSpec.decode("5px"),
				RowSpec.decode("fill:25px"),
				RowSpec.decode("5px"),
				RowSpec.decode("fill:80px"),
				RowSpec.decode("5px"),
				RowSpec.decode("fill:80px"),
				RowSpec.decode("5px"),
				RowSpec.decode("80px"),
				RowSpec.decode("20px"),
				RowSpec.decode("5px"),
				RowSpec.decode("5px:grow"),}));
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VoiceControlPanel.this.enabled = !VoiceControlPanel.this.enabled;
				if (!VoiceControlPanel.this.enabled) {
					boolean successful = BlinkApp.getSelectedDevice().disconnectVoice();
					if (successful)
						btnConnect.setText("Connect");
					VoiceControlPanel.this.enabled = !successful;
				}
				else {
					if (BlinkApp.getSelectedDevice() != null && BlinkApp.getSelectedDevice().isReachable()) {
						boolean successful = BlinkApp.getSelectedDevice().connectVoice(
							BlinkApp.getConfig("de.owlhq.voice.ip"),
							BlinkApp.getConfigInt("de.owlhq.voice.port"),
							BlinkApp.getConfig("de.owlhq.voice.username"),
							BlinkApp.getConfig("de.owlhq.voice.password")
						);
						if (successful) {
							btnConnect.setText("Disconnect");
							BlinkApp.forceSelectedDeviceStatusUpdate();
						}
						VoiceControlPanel.this.enabled = successful;
					}
				}
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("VOICE");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblNewLabel_1, "2, 2, 3, 1");
		add(btnConnect, "2, 4, 3, 1");
		
		JPanel panel = new JPanel();
		add(panel, "2, 6, 3, 8, fill, fill");
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Status");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel.setBounds(0, 0, 120, 14);
		panel.add(lblNewLabel);
		
		lblVoiceStatus = new JLabel("DISCONNECTED");
		lblVoiceStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblVoiceStatus.setForeground(Color.RED.darker());
		lblVoiceStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblVoiceStatus.setBounds(0, 15, 120, 14);
		panel.add(lblVoiceStatus);
		
		JLabel lblNewLabel_2 = new JLabel("Server");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_2.setBounds(0, 45, 120, 15);
		panel.add(lblNewLabel_2);
		
		lblHost = new JLabel("0.0.0.0");
		lblHost.setHorizontalAlignment(SwingConstants.CENTER);
		lblHost.setBounds(0, 60, 120, 20);
		panel.add(lblHost);
		
		JLabel lblNewLabel_2_1 = new JLabel("Port");
		lblNewLabel_2_1.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_2_1.setBounds(0, 80, 120, 15);
		panel.add(lblNewLabel_2_1);
		
		lblPort = new JLabel("12000");
		lblPort.setHorizontalAlignment(SwingConstants.CENTER);
		lblPort.setBounds(0, 95, 120, 20);
		panel.add(lblPort);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("Username");
		lblNewLabel_2_1_1.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_2_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_2_1_1.setBounds(0, 115, 120, 15);
		panel.add(lblNewLabel_2_1_1);
		
		lblUsername = new JLabel("ai-cube-x");
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setBounds(0, 130, 120, 20);
		panel.add(lblUsername);
		
		BlinkApp.addRtDeviceListener(new RtDeviceListener() {
			@Override
			public void deviceChange(RtDeviceEvent e) {
				if (e.eventType == RtDeviceEvent.VOICE_CONNECTED && e.source != null && e.source.isSelectedDevice()) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							VoiceControlPanel.this.enabled = true;
							VoiceControlPanel.this.btnConnect.setText("Disconnect");
							VoiceControlPanel.this.lblVoiceStatus.setText("CONNECTED");
							VoiceControlPanel.this.lblVoiceStatus.setForeground(Color.GREEN.darker());
							RtBoxInfo inf = e.source.getLastStatus();
							if (inf != null) {
								VoiceControlPanel.this.lblHost.setText(inf.getVoice().getHost());
								VoiceControlPanel.this.lblUsername.setText(inf.getVoice().getName());
								VoiceControlPanel.this.lblPort.setText(""+inf.getVoice().getPort());
							}
						}
					});
				}
				else if (e.eventType == RtDeviceEvent.VOICE_DISCONNECTED && e.source != null && e.source.isSelectedDevice()) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							VoiceControlPanel.this.enabled = false;
							VoiceControlPanel.this.btnConnect.setText("Connect");
							VoiceControlPanel.this.lblVoiceStatus.setText("DISCONNECTED");
							VoiceControlPanel.this.lblVoiceStatus.setForeground(Color.RED.darker());
							VoiceControlPanel.this.lblHost.setText(BlinkApp.getConfig("de.owlhq.voice.ip"));
							VoiceControlPanel.this.lblUsername.setText(BlinkApp.getConfig("de.owlhq.voice.username"));
							VoiceControlPanel.this.lblPort.setText(""+BlinkApp.getConfigInt("de.owlhq.voice.port"));
						}
					});
				}
				else if (e.eventType == RtDeviceEvent.DEVICE_CONNECTED && e.source != null && e.source.isSelectedDevice() 
						|| e.eventType == RtDeviceEvent.DEVICE_DISCONNECTED && e.source != null && e.source.isSelectedDevice() ) {
					RtBoxInfo inf = e.source.getLastStatus();
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							VoiceControlPanel.this.enabled = inf != null ? inf.isVoiceConnected() : false;
							VoiceControlPanel.this.btnConnect.setText(VoiceControlPanel.this.enabled ? "Disconnect" : "Connect");
							VoiceControlPanel.this.lblVoiceStatus.setText(VoiceControlPanel.this.enabled ? "CONNECTED" : "DISCONNECTED");
							VoiceControlPanel.this.lblVoiceStatus.setForeground(VoiceControlPanel.this.enabled ? Color.GREEN.darker() : Color.RED.darker());
							if (inf != null && VoiceControlPanel.this.enabled) {
								VoiceControlPanel.this.lblHost.setText(inf.getVoice().getHost());
								VoiceControlPanel.this.lblUsername.setText(inf.getVoice().getName());
								VoiceControlPanel.this.lblPort.setText(""+inf.getVoice().getPort());
							}
						}
					});
				}
			}
		});
		if (BlinkApp.getSelectedDevice() != null) {
			RtBoxInfo inf = BlinkApp.getSelectedDeviceStatus() != null ? BlinkApp.getSelectedDeviceStatus() : BlinkApp.getSelectedDevice().getStatus();
			if (inf.isReachable() && inf.isVoiceEndpointOnline() && inf.isVoiceConnected()) {
				VoiceControlPanel.this.enabled = true;
				VoiceControlPanel.this.btnConnect.setText("Disconnect");
				VoiceControlPanel.this.lblVoiceStatus.setText("CONNECTED");
				VoiceControlPanel.this.lblVoiceStatus.setForeground(Color.GREEN.darker());
				VoiceControlPanel.this.enabled = true;
				VoiceControlPanel.this.btnConnect.setText("Disconnect");
				VoiceControlPanel.this.lblVoiceStatus.setText("CONNECTED");
				VoiceControlPanel.this.lblHost.setText(inf.getVoice().getHost());
				VoiceControlPanel.this.lblUsername.setText(inf.getVoice().getName());
				VoiceControlPanel.this.lblPort.setText(""+inf.getVoice().getPort());
			}
			else {
				this.lblHost.setText(BlinkApp.getConfig("de.owlhq.voice.ip"));
				this.lblUsername.setText(BlinkApp.getConfig("de.owlhq.voice.username"));
				this.lblPort.setText(""+BlinkApp.getConfigInt("de.owlhq.voice.port"));
			}
		}
	}
	
	

	public void setAnimationName(String animationName) {
		this.lblVoiceStatus.setText(animationName);
	}

	public void setAnimationEndless(boolean endless) {
		this.lblHost.setText(endless ? "Yes" : "No");
	}
}
