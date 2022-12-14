package de.owlhq.remotebox.gui.panel;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import java.awt.FlowLayout;
import java.awt.Insets;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import de.owlhq.remotebox.BlinkAnimation;
import de.owlhq.remotebox.BlinkAnimation.BlinkTypes;
import de.owlhq.remotebox.BlinkAnimator;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import javax.swing.JColorChooser;
import javax.swing.SwingConstants;
import javax.swing.JTable;

public class AnimationDialog extends JPanel implements ActionListener, ChangeListener {
	private JComboBox<BlinkTypes> valueType;
	private JSpinner valueDuration;
	private JSpinner valueBrightness;
	private JSpinner valueFps;
	private JSpinner valueDecay;
	private JSpinner valueLoops;
	private JPanel panGeneral;
	private JPanel panSourceColors;
	private JPanel panPreview;
	private JTabbedPane tabbedPane;
	private LedPanel valueStartColorLed;
	private JLabel lblStartColorLedSelected;
	private JColorChooser ccStartColor;

	private JPanel panEndColors;
	private LedPanel valueEndColorLed;
	private JLabel lblEndColorLedSelected;
	private JColorChooser ccEndColor;
	
	private LedPanel previewLedPanel;
	private BlinkAnimator blinkPreviewAnimator = null;

	private JPanel panFilterFrame;
	private LedPanel filterFrameLedPanel;
	private BlinkAnimator blinkFilterFrameAnimator = null;
	private JTable valueFilterFrames;
	DefaultTableModel filterFrameTableModel;

	/**
	 * Create the panel.
	 */
	public AnimationDialog() {
		setSize(new Dimension(750,650));
		setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panGeneral = generateGeneralTab();
		
		tabbedPane.addTab("General", null, panGeneral, null);

		panSourceColors = generateSourceColorPanel(Color.BLACK);
		panEndColors = generateTargetColorPanel(Color.RED);
		panFilterFrame = generateFilterFramePanel();
		panPreview = generatePreviewPanel();

		tabbedPane.addTab("Start Color", null, panSourceColors, null);
		tabbedPane.addTab("Target Color", null, panEndColors, null);
		tabbedPane.addTab("Filter Frames", null, panFilterFrame, null);
		tabbedPane.addTab("Preview", null, panPreview, null);
		
	}

	private JPanel generateFilterFramePanel() {
		JPanel panFilterFrame = new JPanel();
		panFilterFrame.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px:grow"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),}));
		
		filterFrameLedPanel = new LedPanel();
		
		JLabel lblNewLabel = new JLabel("CREATE FILTER FRAMES");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panFilterFrame.add(lblNewLabel, "16, 2, 19, 1");
		panFilterFrame.add(filterFrameLedPanel, "16, 3, 25, 20, fill, default");
		
		// ------------------------------------------------------------------------------------------------------------------
		// NEXT and PREVIOUS BUTTONS
		// ------------------------------------------------------------------------------------------------------------------
		JButton btnColorSourceNext = new JButton("Next");
		btnColorSourceNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Btn Triggered");
				if(AnimationDialog.this.blinkPreviewAnimator != null) {
					AnimationDialog.this.blinkPreviewAnimator = null;
				}
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()+1);
			}
		});

		JButton btnColorSourceBack = new JButton("Previous");
		btnColorSourceBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(AnimationDialog.this.blinkPreviewAnimator != null) {
					AnimationDialog.this.blinkPreviewAnimator = null;
				}
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()-1);
			}
		});
		
		Object[] columnNames = {"Frame No", "LED 0", "LED 1", "LED 2", "LED 3", "LED 4", "LED 5", "LED 6", "LED 7"};
        Object[][] data = {
            {0, true, true, true, true, true, true, true, true}
        };
        filterFrameTableModel = new DefaultTableModel(data, columnNames);
        valueFilterFrames = new JTable(filterFrameTableModel) {

            private static final long serialVersionUID = 1L;

            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        filterFrameTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				System.out.println("Changed Table data");
				int[][] filterFrames = getValueFilterFrames();
				if (blinkFilterFrameAnimator == null) {
					BlinkAnimation ba = AnimationDialog.this.getAnimation();
					AnimationDialog.this.blinkFilterFrameAnimator = new BlinkAnimator(ba, AnimationDialog.this.filterFrameLedPanel, true);
				}
				AnimationDialog.this.blinkFilterFrameAnimator.getBlinkAnimation().setFilter_frames(filterFrames);
				AnimationDialog.this.blinkFilterFrameAnimator.reset();
			}
		});
        
        valueFilterFrames.putClientProperty("terminateEditOnFocusLost", true);
        valueFilterFrames.setPreferredScrollableViewportSize(valueFilterFrames.getPreferredSize());
        
        JScrollPane scrollPane = new JScrollPane(valueFilterFrames);
		panFilterFrame.add(scrollPane, "2, 26, 47, 19, fill, fill");
		
		JButton btnAddFrame = new JButton("+");
		btnAddFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] row = {filterFrameTableModel.getRowCount(), true, true, true, true, true, true, true, true};
				filterFrameTableModel.addRow(row);
			}
		});
		btnAddFrame.setMargin(new Insets(1,1,1,1));
		panFilterFrame.add(btnAddFrame, "2, 46");
		
		JButton btnRemoveFrame = new JButton("-");
		btnRemoveFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(valueFilterFrames.getRowCount() > 1) {
					int selRow = valueFilterFrames.getSelectedRow();
					if (selRow >= 0) {
						if (selRow > 0)
							valueFilterFrames.setRowSelectionInterval(selRow-1, selRow-1);
						filterFrameTableModel.removeRow(selRow);
						for (int i=0;i<filterFrameTableModel.getRowCount();i++) {
							filterFrameTableModel.setValueAt(i, i, 0);
						}
					}
				}
			}
		});
		btnRemoveFrame.setMargin(new Insets(1, 1, 1, 1));
		panFilterFrame.add(btnRemoveFrame, "4, 46");
		
		JButton btnInsertFrame = new JButton("Du");
		btnInsertFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (valueFilterFrames.getSelectedRow() >= 0) {
					Object[] row = {999, true, true, true, true, true, true, true, true};
					for (int c=0;c<8;c++) {
						row[1+c] = (Boolean)filterFrameTableModel.getValueAt(valueFilterFrames.getSelectedRow(), 1+c);
					}
					filterFrameTableModel.insertRow(valueFilterFrames.getSelectedRow()+1, row);
					for (int i=0;i<filterFrameTableModel.getRowCount();i++) {
						filterFrameTableModel.setValueAt(i, i, 0);
					}
				}
			}
		});
		btnInsertFrame.setMargin(new Insets(1, 1, 1, 1));
		panFilterFrame.add(btnInsertFrame, "6, 46");
		panFilterFrame.add(btnColorSourceBack, "38, 46, 5, 1");
		
		// ------------------------------------------------------------------------------------------------------------------
		// Next Frame and Previous Frame BUTTONS
		// ------------------------------------------------------------------------------------------------------------------
		JButton btnNextFrame = new JButton(">");
		btnNextFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (blinkFilterFrameAnimator == null) {
					BlinkAnimation ba = AnimationDialog.this.getAnimation();
					AnimationDialog.this.blinkFilterFrameAnimator = new BlinkAnimator(ba, AnimationDialog.this.filterFrameLedPanel, true);
				}
				AnimationDialog.this.blinkFilterFrameAnimator.nextFrame();
			}
		});
		
		JButton btnPrevFrame = new JButton("<");
		btnPrevFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (blinkFilterFrameAnimator == null) {
					BlinkAnimation ba = AnimationDialog.this.getAnimation();
					AnimationDialog.this.blinkFilterFrameAnimator = new BlinkAnimator(ba, AnimationDialog.this.filterFrameLedPanel, true);
				}
				AnimationDialog.this.blinkFilterFrameAnimator.previousFrame();
			}
		});
		panFilterFrame.add(btnNextFrame, "26, 24, 6, 1");
		panFilterFrame.add(btnPrevFrame, "18, 24, 5, 1");
		panFilterFrame.add(btnColorSourceNext, "44, 46, 5, 1");
		
		return panFilterFrame;
	}

	private JPanel generatePreviewPanel() {
		JPanel panPreview = new JPanel();
		panPreview.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px:grow"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),}));
		
		previewLedPanel = new LedPanel();
		
		JLabel lblNewLabel = new JLabel("PREVIEW ANIMATION");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panPreview.add(lblNewLabel, "16, 2, 19, 1");
		panPreview.add(previewLedPanel, "16, 3, 25, 20, fill, default");
		
		// ------------------------------------------------------------------------------------------------------------------
		// NEXT and PREVIOUS BUTTONS
		// ------------------------------------------------------------------------------------------------------------------
		JButton btnColorSourceNext = new JButton("Next");
		btnColorSourceNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Btn Triggered");
				if(AnimationDialog.this.blinkPreviewAnimator != null) {
					AnimationDialog.this.blinkPreviewAnimator.stopAnimation();
					AnimationDialog.this.blinkPreviewAnimator = null;
				}
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()+1);
			}
		});

		JButton btnColorSourceBack = new JButton("Previous");
		btnColorSourceBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(AnimationDialog.this.blinkPreviewAnimator != null) {
					AnimationDialog.this.blinkPreviewAnimator.stopAnimation();
					AnimationDialog.this.blinkPreviewAnimator = null;
				}
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()-1);
			}
		});
		panPreview.add(btnColorSourceBack, "38, 46, 5, 1");
		
		// ------------------------------------------------------------------------------------------------------------------
		// NEXT and PREVIOUS BUTTONS
		// ------------------------------------------------------------------------------------------------------------------
		JButton btnStartAnimation = new JButton("Start");
		btnStartAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (blinkPreviewAnimator == null) {
					BlinkAnimation ba = AnimationDialog.this.getAnimation();
					AnimationDialog.this.blinkPreviewAnimator = new BlinkAnimator(ba, AnimationDialog.this.previewLedPanel, true);
					AnimationDialog.this.blinkPreviewAnimator.startAnimation();
				}
			}
		});
		panPreview.add(btnStartAnimation, "18, 24, 5, 1");
		
		JButton btnStopAnimation = new JButton("Stop");
		btnStopAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(AnimationDialog.this.blinkPreviewAnimator != null) {
					AnimationDialog.this.blinkPreviewAnimator.stopAnimation();
					AnimationDialog.this.blinkPreviewAnimator = null;
				}
			}
		});
		panPreview.add(btnStopAnimation, "26, 24, 6, 1");
		panPreview.add(btnColorSourceNext, "44, 46, 5, 1");
		
		return panPreview;
	}

	private JPanel generateSourceColorPanel(Color initColor) {
		JPanel panColor = new JPanel();
		panColor.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px:grow"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),}));
		
		valueStartColorLed = new LedPanel();
		for (int i=0;i<8;i++) {
			valueStartColorLed.setColor(i, initColor);
		}
		valueStartColorLed.addActionListener(this);
		
		JLabel lblNewLabel = new JLabel("START COLOR");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panColor.add(lblNewLabel, "16, 2, 19, 1");
		panColor.add(valueStartColorLed, "16, 3, 25, 20, fill, default");
		

		JButton btnColorSourceNext = new JButton("Next");
		btnColorSourceNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Btn Triggered");
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()+1);
			}
		});
		
		ccStartColor = new JColorChooser();
		ccStartColor.setPreviewPanel(new JPanel());
		ColorSelectionModel model = ccStartColor.getSelectionModel();
		model.addChangeListener(this);
		
		lblStartColorLedSelected = new JLabel("LED 0 Selected");
		lblStartColorLedSelected.setHorizontalAlignment(SwingConstants.CENTER);
		panColor.add(lblStartColorLedSelected, "16, 24, 17, 1");
		panColor.add(ccStartColor, "2, 26, 47, 17");
		
		JButton btnStartColorPushToLeds = new JButton("Push Color to all LEDs");
		btnStartColorPushToLeds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (AnimationDialog.this.tabbedPane.getSelectedIndex()) {
				case 1:
					Color c = AnimationDialog.this.ccStartColor.getColor();
					for(int i = 0 ; i < 8; i++) {
						AnimationDialog.this.valueStartColorLed.setColor(i, c);
					}
					break;

				default:
					break;
				}
			}
		});
		panColor.add(btnStartColorPushToLeds, "18, 44, 11, 1");
		panColor.add(btnColorSourceNext, "44, 46, 5, 1");

		JButton btnColorSourceBack = new JButton("Previous");
		btnColorSourceBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()-1);
			}
		});
		panColor.add(btnColorSourceBack, "38, 46, 5, 1");
		
		return panColor;
	}

	private JPanel generateTargetColorPanel(Color initColor) {
		JPanel panColor = new JPanel();
		panColor.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px:grow"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),}));
		
		valueEndColorLed = new LedPanel();
		for (int i=0;i<8;i++) {
			valueEndColorLed.setColor(i, initColor);
		}
		valueEndColorLed.addActionListener(this);
		
		JLabel lblNewLabel_1 = new JLabel("TARGET COLOR");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panColor.add(lblNewLabel_1, "16, 2, 19, 1");
		panColor.add(valueEndColorLed, "16, 3, 25, 20, fill, default");
		

		
		ccEndColor = new JColorChooser();
		ccEndColor.setPreviewPanel(new JPanel());
		ColorSelectionModel model = ccEndColor.getSelectionModel();
		model.addChangeListener(this);
		
		lblEndColorLedSelected = new JLabel("LED 0 Selected");
		lblEndColorLedSelected.setHorizontalAlignment(SwingConstants.CENTER);
		panColor.add(lblEndColorLedSelected, "16, 24, 17, 1");
		panColor.add(ccEndColor, "2, 26, 47, 17");
		
		JButton btnEndColorPushToLeds = new JButton("Push Color to all LEDs");
		btnEndColorPushToLeds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (AnimationDialog.this.tabbedPane.getSelectedIndex()) {
				case 2:
					Color c = AnimationDialog.this.ccEndColor.getColor();
					for(int i = 0 ; i < 8; i++) {
						AnimationDialog.this.valueEndColorLed.setColor(i, c);
					}
					break;

				default:
					break;
				}
			}
		});
		panColor.add(btnEndColorPushToLeds, "18, 44, 11, 1");
		
		// ------------------------------------------------------------------------------------------------------------------
		// NEXT and PREVIOUS BUTTONS
		// ------------------------------------------------------------------------------------------------------------------
		JButton btnColorSourceNext = new JButton("Next");
		btnColorSourceNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Btn Triggered");
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()+1);
			}
		});
		panColor.add(btnColorSourceNext, "44, 46, 5, 1");

		JButton btnColorSourceBack = new JButton("Previous");
		btnColorSourceBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AnimationDialog.this.tabbedPane.setSelectedIndex(AnimationDialog.this.tabbedPane.getSelectedIndex()-1);
			}
		});
		panColor.add(btnColorSourceBack, "38, 46, 5, 1");
		
		return panColor;
	}
	
	private JPanel generateGeneralTab() {
		panGeneral = new JPanel();
		panGeneral.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px:grow"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),
				ColumnSpec.decode("22px"),
				ColumnSpec.decode("5px"),},
			new RowSpec[] {
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px:grow"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),
				RowSpec.decode("22px"),
				RowSpec.decode("5px"),}));

		JLabel lblGeneralType = new JLabel("Type");
		panGeneral.add(lblGeneralType, "2, 2, 7, 1");
		
		JLabel lblGeneralDuration = new JLabel("Duration (MS)");
		panGeneral.add(lblGeneralDuration, "2, 4, 7, 1");
		
		JLabel lblGeneralBrightnes = new JLabel("Brightness (%)");
		panGeneral.add(lblGeneralBrightnes, "2, 6, 7, 1");
		
		JLabel lblGeneralFPS = new JLabel("FPS");
		panGeneral.add(lblGeneralFPS, "2, 8, 7, 1");
		
		JLabel lblGeneralDecay = new JLabel("Decay (%/Frame)");
		panGeneral.add(lblGeneralDecay, "2, 10, 7, 1");
		
		JLabel lblGeneralLoops = new JLabel("Loops");
		panGeneral.add(lblGeneralLoops, "2, 12, 7, 1");
		
		BlinkTypes[] types = BlinkTypes.values();
		valueType = new JComboBox(types);
		panGeneral.add(valueType, "10, 2, 11, 1, fill, default");
		
		valueDuration = new JSpinner();
		valueDuration.setModel(new SpinnerNumberModel(1500, 250, 60000, 1));
		panGeneral.add(valueDuration, "10, 4, 11, 1, fill, default");
		
		valueBrightness = new JSpinner();
		valueBrightness.setModel(new SpinnerNumberModel(90, 5, 100, 1));
		panGeneral.add(valueBrightness, "10, 6, 11, 1, fill, default");
		
		valueFps = new JSpinner();
		valueFps.setModel(new SpinnerNumberModel(30, 15, 30, 1));
		panGeneral.add(valueFps, "10, 8, 11, 1, fill, default");
		
		valueDecay = new JSpinner();
		valueDecay.setModel(new SpinnerNumberModel(25, 5, 100, 1));
		panGeneral.add(valueDecay, "10, 10, 11, 1, fill, default");
		
		valueLoops = new JSpinner();
		valueLoops.setModel(new SpinnerNumberModel(1, 1, 100, 1));
		panGeneral.add(valueLoops, "10, 12, 11, 1, fill, default");
		
		JButton btnGeneralNext = new JButton("Next");
		btnGeneralNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Btn Triggered");
				AnimationDialog.this.tabbedPane.setSelectedIndex(1);
			}
		});
		panGeneral.add(btnGeneralNext, "44, 46, 5, 1");
		return panGeneral;
	}

	private int[][] getValueFilterFrames() {
		int[][] filterFrames = new int[filterFrameTableModel.getRowCount()][8];
		for (int r=0; r<filterFrameTableModel.getRowCount();r++) {
			for (int c=0;c<8;c++) {
				filterFrames[r][c] = (Boolean)filterFrameTableModel.getValueAt(r, 1+c) ? 1 : 0;
			}
		}
		return filterFrames;
	}

	public int getValueLoops() {
		return (Integer) valueLoops.getValue();
	}
	public float getValueDecay() {
		return ((Integer)valueDecay.getValue()/100.0f);
	}
	public int getValueFps() {
		return (Integer) valueFps.getValue();
	}
	public float getValueBrightness() {
		return ((Integer)valueBrightness.getValue()/100.0f);
	}
	public int getValueDuration() {
		return (Integer) valueDuration.getValue();
	}
	public BlinkTypes getValueType() {
		return (BlinkTypes) valueType.getSelectedItem();
	}
	
	public BlinkAnimation getAnimation() {
		// TODO: Implement
		BlinkAnimation ba = new BlinkAnimation(this.getValueType(), this.getValueDuration(), this.getValueLoops(), this.getValueFps(),
				this.getValueBrightness(), this.getValueDecay(), 8, this.getValueColorSource(), this.getValueColorTarget());
		ba.setFilter_frames(getValueFilterFrames());
		return ba;
	}

	private String[] getValueColorTarget() {
		String[] rgb = new String[8];
		for (int i = 0; i < rgb.length; i++) {
			Color c = this.valueEndColorLed.getColor(i);
			rgb[i] = "#"+Integer.toHexString(c.getRGB()).substring(2);
		}
		return rgb;
	}

	private String[] getValueColorSource() {
		String[] rgb = new String[8];
		for (int i = 0; i < rgb.length; i++) {
			Color c = this.valueStartColorLed.getColor(i);
			rgb[i] = "#"+Integer.toHexString(c.getRGB()).substring(2);
		}
		return rgb;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (this.tabbedPane.getSelectedIndex()) {
		case 1:
			this.lblStartColorLedSelected.setText("LED " + this.valueStartColorLed.getSelectedButton() + " selected");
			this.ccStartColor.setColor(this.valueStartColorLed.getSelectedColor());
			break;
		case 2:
			this.lblEndColorLedSelected.setText("LED " + this.valueEndColorLed.getSelectedButton() + " selected");
			this.ccEndColor.setColor(this.valueEndColorLed.getSelectedColor());
			break;

		default:
			break;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		switch (this.tabbedPane.getSelectedIndex()) {
		case 1:
			this.valueStartColorLed.setColor(this.ccStartColor.getColor());
			break;
		case 2:
			this.valueEndColorLed.setColor(this.ccEndColor.getColor());
			break;

		default:
			break;
		}
	}
}
