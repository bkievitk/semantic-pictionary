package animals;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import action.tree2D.Model2DTreeActionAdd;
import action.tree2D.Model2DTreeActionDelete;

import modelTools.*;
import creator2DTree.*;

public class PartsWindow extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 3239817923525073880L;
	
	public Vector<PrimitiveInstance2DTree> parts;
	public GeonModel model;
	public Vector<JButton> buttons = new Vector<JButton>();
	
	public JPanel scrolledPanel;
	public JButton returnButton;
	
	public PartsWindow(GeonModel myModel, final Vector<PrimitiveInstance2DTree> parts) {
		this.parts = parts;
		
		model = myModel;
	    model.addUpdateListener(this);
	    model.addSelectListener(this);
	    
	    returnButton = new JButton("ret");
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrimitiveInstance2DTree selected = (PrimitiveInstance2DTree)model.getSelected();
				if(selected != null && selected != ((Model2DTree)model).root) {
					model.performAction(new Model2DTreeActionDelete(selected));
					parts.add(selected);
					model.setSelected(null);
					rebuild();
				}
			}
		});

		this.setPreferredSize(new Dimension(305,100));
		scrolledPanel = new JPanel();
		scrolledPanel.setPreferredSize(new Dimension(305,100));
		
		int width = 6;
		scrolledPanel.setLayout(new GridLayout(0,width));
		
		JScrollPane scroll = new JScrollPane(scrolledPanel);
		scroll.setPreferredSize(new Dimension(305,100));
		
		add(scroll);
		rebuild();
		
		this.setEnabled(false);
		mySetEnabled();
	}
	
	public void rebuild() {		
		scrolledPanel.removeAll();		
		buttons.clear();
		

		int width = 6;
		scrolledPanel.setPreferredSize(new Dimension(50 * width,50 * (parts.size()-1) / width));
		
		for(PrimitiveInstance2DTree part : parts) {
			JButton button = new JButton(new ImageIcon(part.thumbnail(null, 50, 50)));
			
			if(part.functional != null) {
				button.setToolTipText(part.functional + "");
			}
			
			button.setDisabledIcon(null);
			buttons.add(button);
			scrolledPanel.add(button);
			final PrimitiveInstance2DTree partFinal = part;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(model.getSelected() != null) {
						model.performAction(new Model2DTreeActionAdd(partFinal,(PrimitiveInstance2DTree)model.getSelected()));
						parts.remove(partFinal);
						model.setSelected(null);
						rebuild();
					}
				}
			});
		}
				
		buttons.add(returnButton);
		scrolledPanel.add(returnButton);
		
		this.validate();
		repaint();
	}

	public void mySetEnabled() {
		PrimitiveInstance instance = model.getSelected();
		boolean setVal = instance != null && instance.isEditable(PrimitiveInstance.EDIT_ADD);
		for(JButton button : buttons) {
			button.setEnabled(setVal);
		}
		if(instance != null) {
			returnButton.setEnabled(true);
		}
	}
	
	public void stateChanged(ChangeEvent arg0) {
		mySetEnabled();
	}
}
