package de.owlhq.remotebox.gui.panel;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

public class LedPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ActionListener> al = new ArrayList<>(2);
	
	private JButton led_0;
	private JButton led_1;
	private JButton led_2;
	private JButton led_3;
	private JButton led_4;
	private JButton led_5;
	private JButton led_6;
	private JButton led_7;

	private JButton selectedButton = led_0;

	/**
	 * Create the panel.
	 */
	public LedPanel() {
		setBorder(null);
		setLayout(null);
		setSize(254, 254);
		setMinimumSize(new Dimension(254, 254));
		setMaximumSize(new Dimension(254, 254));
		
		led_0 = new JButton("LED 0");
		led_0.setMargin(new Insets(1,1,1,1));
		led_0.setBounds(65, 4, 50, 50);
		led_0.setBackground(new Color(0, 0, 0));
		led_0.setForeground(new Color(120, 120, 120));
		led_0.setContentAreaFilled(false);
		led_0.setOpaque(true);
		selectedButton = led_0;		
		add(led_0);
		
		led_1 = new JButton("LED 1");
		led_1.setMargin(new Insets(1,1,1,1));
		led_1.setBounds(135, 4, 50, 50);
		led_1.setBackground(new Color(0, 0, 0));
		led_1.setBackground(new Color(0, 0, 0));
		led_1.setForeground(new Color(120, 120, 120));
		led_1.setContentAreaFilled(false);
		led_1.setOpaque(true);
		add(led_1);
		
		led_2 = new JButton("LED 2");
		led_2.setMargin(new Insets(1,1,1,1));
		led_2.setBounds(200, 65, 50, 50);
		led_2.setBackground(new Color(0, 0, 0));
		led_2.setBackground(new Color(0, 0, 0));
		led_2.setForeground(new Color(120, 120, 120));
		led_2.setContentAreaFilled(false);
		led_2.setOpaque(true);
		add(led_2);
		
		led_3 = new JButton("LED 3");
		led_3.setMargin(new Insets(1,1,1,1));
		led_3.setBounds(200, 135, 50, 50);
		led_3.setBackground(new Color(0, 0, 0));
		led_3.setBackground(new Color(0, 0, 0));
		led_3.setForeground(new Color(120, 120, 120));
		led_3.setContentAreaFilled(false);
		led_3.setOpaque(true);
		add(led_3);
		
		led_7 = new JButton("LED 7");
		led_7.setMargin(new Insets(1,1,1,1));
		led_7.setBounds(4, 65, 50, 50);
		led_7.setBackground(new Color(0, 0, 0));
		led_7.setBackground(new Color(0, 0, 0));
		led_7.setForeground(new Color(120, 120, 120));
		led_7.setContentAreaFilled(false);
		led_7.setOpaque(true);
		add(led_7);
		
		led_6 = new JButton("LED 6");
		led_6.setMargin(new Insets(1,1,1,1));
		led_6.setBounds(4, 135, 50, 50);
		led_6.setBackground(new Color(0, 0, 0));
		led_6.setBackground(new Color(0, 0, 0));
		led_6.setForeground(new Color(120, 120, 120));
		led_6.setContentAreaFilled(false);
		led_6.setOpaque(true);
		add(led_6);
		
		led_5 = new JButton("LED 5");
		led_5.setMargin(new Insets(1,1,1,1));
		led_5.setBounds(65, 200, 50, 50);
		led_5.setBackground(new Color(0, 0, 0));
		led_5.setBackground(new Color(0, 0, 0));
		led_5.setForeground(new Color(120, 120, 120));
		led_5.setContentAreaFilled(false);
		led_5.setOpaque(true);
		add(led_5);
		
		led_4 = new JButton("LED 4");
		led_4.setMargin(new Insets(1,1,1,1));
		led_4.setBounds(135, 200, 50, 50);
		led_4.setBackground(new Color(0, 0, 0));
		led_4.setBackground(new Color(0, 0, 0));
		led_4.setForeground(new Color(120, 120, 120));
		led_4.setContentAreaFilled(false);
		led_4.setOpaque(true);
		add(led_4);

		led_0.addActionListener(this);
		led_1.addActionListener(this);
		led_2.addActionListener(this);
		led_3.addActionListener(this);
		led_4.addActionListener(this);
		led_5.addActionListener(this);
		led_6.addActionListener(this);
		led_7.addActionListener(this);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 0, 0));
		panel.setForeground(new Color(0, 0, 0));
		panel.setBounds(0, 115, 10, 20);
		add(panel);
		
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		System.out.println("Printed");
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        Paint oldPaint = g2d.getPaint();
        Stroke stroke = new BasicStroke(1);
        Stroke oldStroke = g2d.getStroke();
        Object oldAA = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        try {
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(stroke);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawRoundRect(2, 2, 250, 250, 20, 20);
        }
        finally {
            g2d.setPaint(oldPaint);
            g2d.setStroke(oldStroke);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
        }
    }
	
	public void setColor(int ledId, Color color) {
		switch (ledId) {
		case 0:
			led_0.setBackground(color);
			break;
		case 1:
			led_1.setBackground(color);
			break;
		case 2:
			led_2.setBackground(color);
			break;
		case 3:
			led_3.setBackground(color);
			break;
		case 4:
			led_4.setBackground(color);
			break;
		case 5:
			led_5.setBackground(color);
			break;
		case 6:
			led_6.setBackground(color);
			break;
		case 7:
			led_7.setBackground(color);
			break;
		default:
			break;
		}
	}
	
	public void setColor(Color color) {
		selectedButton.setBackground(color);
	}
	
	public Color getColor(int ledId) {
		switch (ledId) {
		case 0:
			return led_0.getBackground();
		case 1:
			return led_1.getBackground();
		case 2:
			return led_2.getBackground();
		case 3:
			return led_3.getBackground();
		case 4:
			return led_4.getBackground();
		case 5:
			return led_5.getBackground();
		case 6:
			return led_6.getBackground();
		case 7:
			return led_7.getBackground();
		default:
			return Color.black;
		}
	}
	
	public int getSelectedButton() {
		if (selectedButton == led_0)
			return 0;
		else if (selectedButton == led_1)
			return 1;
		else if (selectedButton == led_2)
			return 2;
		else if (selectedButton == led_3)
			return 3;
		else if (selectedButton == led_4)
			return 4;
		else if (selectedButton == led_5)
			return 5;
		else if (selectedButton == led_6)
			return 6;
		else if (selectedButton == led_7)
			return 7;
		
		return 0;
	}
	
	public Color getSelectedColor() {
		return this.selectedButton.getBackground();
	}
	
	public void addActionListener(ActionListener  al) {
		this.al.add(al);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.selectedButton = (JButton) e.getSource();
		for (ActionListener actionListener : al) {
			actionListener.actionPerformed(e);
		}
	}
}
