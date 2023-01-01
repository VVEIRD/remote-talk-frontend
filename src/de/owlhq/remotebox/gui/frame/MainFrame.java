package de.owlhq.remotebox.gui.frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JTabbedPane;
import de.owlhq.remotebox.gui.panel.AnimationDialog;
import java.awt.BorderLayout;
import de.owlhq.remotebox.gui.panel.AudioControlPanel;
import de.owlhq.remotebox.gui.panel.LedControlPanel;

public class MainFrame extends JFrame {

	private JPanel contentPane;

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
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, "2, 4, 35, 33, fill, fill");
		
		JPanel pnCommandCenter = new JPanel();
		tabbedPane.addTab("Command Center", null, pnCommandCenter, null);
		pnCommandCenter.setLayout(new BorderLayout(0, 0));
		
		AudioControlPanel audioControlPanel = new AudioControlPanel();
		pnCommandCenter.add(audioControlPanel, BorderLayout.NORTH);
		
		LedControlPanel ledControlPanel = new LedControlPanel();
		pnCommandCenter.add(ledControlPanel, BorderLayout.EAST);
		
		AnimationDialog animationDialog = new AnimationDialog();
		tabbedPane.addTab("Create Animation", null, animationDialog, null);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Configuration", null, panel, null);
	}

}
