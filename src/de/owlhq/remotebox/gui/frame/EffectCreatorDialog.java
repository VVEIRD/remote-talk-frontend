package de.owlhq.remotebox.gui.frame;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.owlhq.remotebox.data.PlayEffect;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EffectCreatorDialog extends JDialog {

	private static final List<String> EFFECTS;
	private static final List<String> SOUNDS;
	
	static  {
		EFFECTS = new LinkedList<>();
		EFFECTS.add("Effect_1");
		EFFECTS.add("Effect_2");
		EFFECTS.add("Effect_3");
		EFFECTS.add("Effect_4");
		SOUNDS = new LinkedList<>();
		SOUNDS.add("Sound_1");
		SOUNDS.add("Sound_2");
		SOUNDS.add("Sound_3");
		SOUNDS.add("Sound_4");
	}
	
	private boolean completed = false;
	
	private boolean aborted = false;
	
	private PlayEffect effect = null;
	
	private List<String> availableEffects = null;
	
	private List<String> availableSounds = null;

	private JPanel contentPane;
	private JTextField tfName;
	private JComboBox cbEffect;
	private JCheckBox chckbxEndless;
	private JCheckBox chckbxPlayEffect;
	private JCheckBox chckbxPlaySound;
	private JComboBox cbSound;


	public EffectCreatorDialog() {
		this(null);
	}
	public EffectCreatorDialog(JFrame frame) {
		this(frame, new PlayEffect(), EFFECTS, SOUNDS);
	}
	/**
	 * Create the frame.
	 */
	public EffectCreatorDialog(JFrame frame, PlayEffect effect, List<String> availableEffects, List<String> availableSounds) {
		super(frame, true);
		setTitle(effect != null ? "Edit Effect" : "Create Effect");
		// Data
		this.effect = effect != null ? effect : new PlayEffect();
		this.availableEffects = availableEffects;
		this.availableSounds = availableSounds;
		// Frame Stuff
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 460, 210);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chckbxPlayEffect = new JCheckBox("Play Effect");
		chckbxPlayEffect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				cbEffect.setEnabled(chckbxPlayEffect.isSelected());
				chckbxEndless.setEnabled(chckbxPlayEffect.isSelected());
			}
		});
		chckbxPlayEffect.setBounds(6, 52, 97, 23);
		contentPane.add(chckbxPlayEffect);
		
		chckbxPlaySound = new JCheckBox("Play Sound");
		chckbxPlaySound.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				cbSound.setEnabled(chckbxPlaySound.isSelected());
			}
		});
		chckbxPlaySound.setBounds(235, 52, 97, 23);
		contentPane.add(chckbxPlaySound);
		
		cbEffect = new JComboBox(this.availableEffects.toArray());
		cbEffect.setEnabled(false);
		cbEffect.setBounds(9, 85, 195, 22);
		contentPane.add(cbEffect);
		
		chckbxEndless = new JCheckBox("Endless");
		chckbxEndless.setEnabled(false);
		chckbxEndless.setBounds(6, 115, 97, 23);
		contentPane.add(chckbxEndless);
		
		cbSound = new JComboBox(this.availableSounds.toArray());
		cbSound.setEnabled(false);
		cbSound.setBounds(239, 85, 195, 22);
		contentPane.add(cbSound);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EffectCreatorDialog.this.effect.setName(EffectCreatorDialog.this.tfName.getText());
				// Add LED Animation
				if (EffectCreatorDialog.this.chckbxPlayEffect.isSelected()) {
					EffectCreatorDialog.this.effect.setBlinkAnimationName((String)EffectCreatorDialog.this.cbEffect.getSelectedItem());
					EffectCreatorDialog.this.effect.setEndlessAnimation(EffectCreatorDialog.this.chckbxEndless.isSelected());
				}
				else {
					EffectCreatorDialog.this.effect.setBlinkAnimationName(null);
					EffectCreatorDialog.this.effect.setEndlessAnimation(false);
				}
				// Add sound effect
				if (EffectCreatorDialog.this.chckbxPlaySound.isSelected()) {
					EffectCreatorDialog.this.effect.setAudioFile((String)EffectCreatorDialog.this.cbSound.getSelectedItem());
				}
				else {
					EffectCreatorDialog.this.effect.setAudioFile(null);
				}
				EffectCreatorDialog.this.completed = true;
				EffectCreatorDialog.this.setVisible(false);
				EffectCreatorDialog.this.dispose();
			}
		});
		btnSave.setBounds(265, 142, 80, 23);
		contentPane.add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EffectCreatorDialog.this.aborted = true;
				EffectCreatorDialog.this.completed = true;
				EffectCreatorDialog.this.setVisible(false);
				EffectCreatorDialog.this.dispose();
			}
		});
		btnCancel.setBounds(355, 142, 80, 23);
		contentPane.add(btnCancel);
		
		tfName = new JTextField(this.effect.getName());
		tfName.setBounds(10, 25, 195, 20);
		contentPane.add(tfName);
		tfName.setColumns(10);
		tfName.setEditable(effect == null);
		
		JLabel lblNewLabel = new JLabel("Name");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setBounds(10, 11, 195, 14);
		contentPane.add(lblNewLabel);
		chckbxPlayEffect.setSelected(this.effect.getAnimationName() != null);
		chckbxPlaySound.setSelected(this.effect.getAudioFile() != null);
		chckbxEndless.setSelected(this.effect.isEndlessAnimation());
		cbEffect.setSelectedItem(this.effect.getAnimationName());
		cbSound.setSelectedItem(this.effect.getAudioFile());
	}
	
	public PlayEffect getEffect() {
		return effect;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	
	public boolean isAborted() {
		return aborted;
	}
}
