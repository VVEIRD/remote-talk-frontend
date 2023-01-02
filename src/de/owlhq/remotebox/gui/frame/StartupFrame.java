package de.owlhq.remotebox.gui.frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.network.RtDeviceEvent;
import de.owlhq.remotebox.network.RtDeviceListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StartupFrame extends JFrame {

	private JPanel contentPane;
	private JTextField tfVoiceHost;
	private JTextField tfPassword;
	private JComboBox cbRemoteBox;
	private JSpinner spVoiceHostPort;
	private JTextField tfVoiceUsername;
	private JButton btnStart;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartupFrame frame = new StartupFrame();
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
	public StartupFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Arkane Intelligenz");
		lblNewLabel.setFont(new Font("Tw Cen MT", Font.ITALIC, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 284, 60);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Würfel auswählen");
		lblNewLabel_1.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_1.setBounds(10, 70, 265, 14);
		contentPane.add(lblNewLabel_1);
		
		cbRemoteBox = new JComboBox(new Object[]{});
		cbRemoteBox.setBounds(10, 90, 265, 22);
		contentPane.add(cbRemoteBox);
		
		JLabel lblNewLabel_1_1 = new JLabel("Mumble Voice Host");
		lblNewLabel_1_1.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_1_1.setBounds(10, 150, 265, 14);
		contentPane.add(lblNewLabel_1_1);
		
		tfVoiceHost = new JTextField();
		tfVoiceHost.setBounds(10, 170, 265, 20);
		contentPane.add(tfVoiceHost);
		tfVoiceHost.setColumns(10);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Port");
		lblNewLabel_1_1_1.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_1_1_1.setBounds(10, 200, 265, 14);
		contentPane.add(lblNewLabel_1_1_1);
		
		spVoiceHostPort = new JSpinner();
		spVoiceHostPort.setModel(new SpinnerNumberModel(12000, 1000, 60000, 1));
		spVoiceHostPort.setBounds(10, 220, 265, 20);
		contentPane.add(spVoiceHostPort);
		
		JLabel lable = new JLabel("Benutzername");
		lable.setVerticalAlignment(SwingConstants.BOTTOM);
		lable.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lable.setBounds(10, 250, 265, 14);
		contentPane.add(lable);
		
		tfVoiceUsername = new JTextField();
		tfVoiceUsername.setText("ai-cube");
		tfVoiceUsername.setColumns(10);
		tfVoiceUsername.setBounds(10, 270, 265, 20);
		contentPane.add(tfVoiceUsername);
		
		JLabel lblPasswort = new JLabel("Passwort");
		lblPasswort.setVerticalAlignment(SwingConstants.BOTTOM);
		lblPasswort.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblPasswort.setBounds(10, 300, 265, 14);
		contentPane.add(lblPasswort);
		
		tfPassword = new JTextField();
		tfPassword.setText("ai-cube-12345");
		tfPassword.setColumns(10);
		tfPassword.setBounds(10, 320, 265, 20);
		contentPane.add(tfPassword);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BlinkApp.setConfig("de.owlhq.voice.ip", tfVoiceHost.getText());
				BlinkApp.setConfig("de.owlhq.voice.port", String.valueOf((Integer)spVoiceHostPort.getValue()));
				BlinkApp.setConfig("de.owlhq.voice.username", tfVoiceUsername.getText());
				BlinkApp.setConfig("de.owlhq.voice.password", tfPassword.getText());
				BlinkApp.selectDevice((String)cbRemoteBox.getSelectedItem());
				BlinkApp.saveConfig();
				StartupFrame.this.setVisible(false);
				StartupFrame.this.dispose();
				BlinkApp.showMainDialog();
			}
		});
		btnStart.setBounds(10, 400, 265, 23);
		contentPane.add(btnStart);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initData();
	}
	
	private void initData() {
		String ipAdress = BlinkApp.getConfig("de.owlhq.voice.ip");
		int port = BlinkApp.configContainsKey("de.owlhq.voice.port", Integer.class) ? BlinkApp.getConfigInt("de.owlhq.voice.port") : 12000;
		String username = BlinkApp.getConfig("de.owlhq.voice.username");
		String password = BlinkApp.getConfig("de.owlhq.voice.password");
		if (ipAdress != null)
			tfVoiceHost.setText(ipAdress);
		if (username != null)
			tfVoiceUsername.setText(username);
		if (password != null)
			tfPassword.setText(password);
		spVoiceHostPort.setValue(port);
		// Add all blink devices on network
		for(String name: BlinkApp.getDeviceNames()) {
			cbRemoteBox.addItem(name);
		}
		BlinkApp.addRtDeviceListener(new RtDeviceListener() {
			@Override
			public void deviceChange(RtDeviceEvent e) {
				if (e.eventType == RtDeviceEvent.DEVICE_CONNECTED)
					cbRemoteBox.addItem(e.source.getDeviceName());
				else {
					cbRemoteBox.removeAllItems();
					for(String name: BlinkApp.getDeviceNames()) {
						cbRemoteBox.addItem(name);
					}
				}
			}
		});
	}
}
