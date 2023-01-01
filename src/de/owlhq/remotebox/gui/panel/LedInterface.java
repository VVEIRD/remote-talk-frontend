package de.owlhq.remotebox.gui.panel;

import java.awt.Color;

public interface LedInterface {

	void setColor(int ledId, Color color);

	void setColor(Color color);

	Color getColor(int ledId);

	int getSelectedButton();

	Color getSelectedColor();

}