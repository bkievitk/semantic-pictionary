package creator2DTree;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.PrimitiveInstance;

import templates.CreatorPanel;
import templates.WindowAttachment;
import tools.MyArrays;

import action.tree2D.Model2DTreeActionClone;
import action.tree2D.Model2DTreeActionTreeConnection;



public class WindowAttachment2DTree extends WindowAttachment implements ChangeListener {

	private static final long serialVersionUID = 7781694025690997875L;
	
	public Model2DTree model;
	public JRadioButton[][] buttons = new JRadioButton[4][3];
	public boolean disableSelect = false;
	public boolean disableSetVals = false;
	public JButton cloneUnbound;
	public JButton cloneBound;
	
	public WindowAttachment2DTree(Model2DTree modelIn, boolean showClone) {
		super(modelIn);
		
		model = modelIn;

	    model.addUpdateListener(this);
	    model.addSelectListener(this);
	    
		this.setLayout(new GridLayout(3,2));
		
		JPanel buttonPanel;
		ButtonGroup group;
		
		buttonPanel = new JPanel(new GridLayout(1,3));
		group = new ButtonGroup();
		for(int position = -1;position <=1;position++) {
			JRadioButton button = new JRadioButton();
			buttons[0][position+1] = button;
			group.add(button);		
			buttonPanel.add(button);
			
			final short toPosition = (short)position;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(model.selected != null && model.getSelected().parent != null && !disableSelect) {
						short[] newAttach = MyArrays.copyOf(model.getSelected().parent.parentAttachmentPoint);
						newAttach[0] = toPosition;
						disableSetVals = true;
						model.performAction(new Model2DTreeActionTreeConnection(model.getSelected().parent.childAttachmentPoint,newAttach,model.getSelected().parent));
						disableSetVals = false;
					}
				}
			});
		}
		this.add(CreatorPanel.labeledPanel(buttonPanel,"parent X"));
		
		buttonPanel = new JPanel(new GridLayout(1,3));
		group = new ButtonGroup();
		for(int position = -1;position <=1;position++) {
			JRadioButton button = new JRadioButton();
			buttons[1][position+1] = button;
			group.add(button);		
			buttonPanel.add(button);
			
			final short toPosition = (short)position;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(model.selected != null && model.getSelected().parent != null && !disableSelect) {
						short[] newAttach = MyArrays.copyOf(model.getSelected().parent.parentAttachmentPoint);
						newAttach[1] = toPosition;
						disableSetVals = true;
						model.performAction(new Model2DTreeActionTreeConnection(model.getSelected().parent.childAttachmentPoint,newAttach,model.getSelected().parent));
						disableSetVals = false;
					}
				}
			});
		}
		this.add(CreatorPanel.labeledPanel(buttonPanel,"parent Y"));
		
		buttonPanel = new JPanel(new GridLayout(1,3));
		group = new ButtonGroup();
		for(int position = -1;position <=1;position++) {
			JRadioButton button = new JRadioButton();
			buttons[2][position+1] = button;
			group.add(button);		
			buttonPanel.add(button);
			
			final short toPosition = (short)position;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(model.selected != null && model.getSelected().parent != null && !disableSelect) {
						short[] newAttach = MyArrays.copyOf(model.getSelected().parent.childAttachmentPoint);
						newAttach[0] = toPosition;
						disableSetVals = true;
						model.performAction(new Model2DTreeActionTreeConnection(newAttach,model.getSelected().parent.parentAttachmentPoint,model.getSelected().parent));
						disableSetVals = false;
					}
				}
			});

		}
		this.add(CreatorPanel.labeledPanel(buttonPanel,"children X"));
		
		buttonPanel = new JPanel(new GridLayout(1,3));
		group = new ButtonGroup();
		for(int position = -1;position <=1;position++) {
			JRadioButton button = new JRadioButton();
			buttons[3][position+1] = button;
			group.add(button);		
			buttonPanel.add(button);
			
			final short toPosition = (short)position;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(model.selected != null && model.getSelected().parent != null && !disableSelect) {
						short[] newAttach = MyArrays.copyOf(model.getSelected().parent.childAttachmentPoint);
						newAttach[1] = toPosition;
						disableSetVals = true;
						model.performAction(new Model2DTreeActionTreeConnection(newAttach,model.getSelected().parent.parentAttachmentPoint,model.getSelected().parent));
						disableSetVals = false;
					}
				}
			});

		}
		this.add(CreatorPanel.labeledPanel(buttonPanel,"children Y"));
		
		if(showClone) {
			cloneUnbound = new JButton("clone unbound");
			cloneBound = new JButton("clone bound");
			
			cloneUnbound.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					model.performAction(new Model2DTreeActionClone(model.getSelected().parent,false));
				}
			});
			
			cloneBound.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					model.performAction(new Model2DTreeActionClone(model.getSelected().parent,true));
				}
			});
			
			this.add(cloneUnbound);
			this.add(cloneBound);
		}
		setEnabledState();
	}
	
	public void setVals() {
		if(!disableSetVals) {
			PrimitiveInstance2DTree object = model.getSelected();
			if(object != null && object != model.root) {
				disableSelect = true;
				buttons[0][object.parent.parentAttachmentPoint[0]+1].setSelected(true);
				buttons[1][object.parent.parentAttachmentPoint[1]+1].setSelected(true);
				buttons[2][object.parent.childAttachmentPoint[0]+1].setSelected(true);
				buttons[3][object.parent.childAttachmentPoint[1]+1].setSelected(true);
				disableSelect = false;
			}
		}
	}

	public void stateChanged(ChangeEvent arg0) {
		// When the selected object changes, change what is enabled.
		setEnabledState();
		setVals();
	}
	
	public void setEnabledState() {
		// Check if an object is selected then turn on or off all buttons.
		boolean setVal = model.getSelected() != null && model.getSelected() != model.root && model.getSelected().isEditable(PrimitiveInstance.EDIT_MOVE);
		for(JRadioButton[] buttonSet : buttons) {
			for(JRadioButton button : buttonSet) {
				button.setEnabled(setVal);
				button.setVisible(setVal);
			}
		}

		if(cloneBound != null) {
			cloneBound.setEnabled(setVal);
			cloneUnbound.setEnabled(setVal);
		}
	}
}
