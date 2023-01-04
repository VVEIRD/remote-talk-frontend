package de.owlhq.remotebox.gui.panel;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.owlhq.remotebox.BlinkApp;
import de.owlhq.remotebox.data.PlayEffect;
import de.owlhq.remotebox.gui.layout.WrapLayout;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

public class DashboardPanel extends JPanel {
	private JLabel lblStatus;
	private JButton btnAddEffect;
	private JPanel pnEffects;

	/**
	 * Create the panel.
	 */
	public DashboardPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
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
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
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
				RowSpec.decode("5px"),}));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, "1, 2, 32, 29, fill, fill");
		
		pnEffects = new JPanel();
		scrollPane.setViewportView(pnEffects);
		FlowLayout fl_pnEffects = new FlowLayout(FlowLayout.LEFT, 5, 5);
		fl_pnEffects.setAlignOnBaseline(true);
		WrapLayout wLayout = new WrapLayout(FlowLayout.LEFT, 5, 5);
		wLayout.setAlignOnBaseline(true);
		pnEffects.setLayout(wLayout);
		
		lblStatus = new JLabel("");
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblStatus, "1, 32, 30, 1");
		
		btnAddEffect = new JButton("+");
		btnAddEffect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlayEffect effect = BlinkApp.editEffect(null);
				if (effect != null) {
					BlinkApp.saveEffect(effect);
					addEffect(effect);
				}
			}
		});
		btnAddEffect.setMargin(new Insets(0, 0, 0, 0));
		add(btnAddEffect, "32, 32");

		loadData();
	}
	
	private void loadData() {
		for(PlayEffect pe: BlinkApp.getEffects()) {
			EffectsPanel eP = new EffectsPanel(pe);
			pnEffects.add(eP);
		}
	}

	public void addEffect(PlayEffect eff) {
		EffectsPanel eP = new EffectsPanel(eff);
		pnEffects.add(eP);
		pnEffects.revalidate();
	}
	
	public void setStatusText(String text, Color color) {
		this.lblStatus.setText(text);
		this.lblStatus.setForeground(color);
	}

}
