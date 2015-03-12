package creator3DTree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;
import my3D.JPanel3DChangeObject;

import templates.WindowAttachment;

import action.tree3D.Model3DActionClone;



/**
 * Manage the connections between objects.
 * @author brentkk
 */

public class WindowAttachment3DTree extends WindowAttachment implements ActionListener, ChangeListener  {
	
	private static final long serialVersionUID = 1356666802193904380L;
	
	// Action buttons.
	public JButton move;
	public JButton cloneBound;
	public JButton cloneUnbound;

	// Identify the relations for this attachment.
	public PrimitiveInstance3DTree parent;
	public PrimitiveInstance3DTree child;
	public ObjectAttachment attachment;
		
	public GeonModel model;
	
	public BoxSelect parentSelect;
	public BoxSelect childSelect;
	
	public JPanel3DChangeObject renderWindow;
	
	
	public void setConnections() {
		
		PrimitiveInstance3DTree object = (PrimitiveInstance3DTree)model.getSelected();
	    if(object != null) {
			// Set relations.
		    if(object.parent == null) {
		    	parent = null;
		    } else {
		    	parent = object.parent.parent;
		    }
			child = object;
			attachment = object.parent;
			parentSelect.setAttachment(attachment);
			childSelect.setAttachment(attachment);
			
	    } else {
	    	child = null;
	    	attachment = null;
	    	if(parentSelect != null && childSelect != null) {
	    		parentSelect.setAttachment(null);
	    		childSelect.setAttachment(null);
	    	}
	    }
	    

	    ChangeEvent ce = new ChangeEvent(renderWindow);
		parentSelect.stateChanged(ce);
		childSelect.stateChanged(ce);
		
	}
	
	
	/**
	 * Store attachments and build panel.
	 * @param attachment The attachment data set.
	 * @param mainWindow
	 */
	public WindowAttachment3DTree(GeonModel model, JPanel3DChangeObject renderWindow) {
		super(model);
		
		this.renderWindow = renderWindow;
		this.model = model;
		this.setLayout(new BorderLayout());
		
		renderWindow.addViewChangeListener(this);

		move = new JButton(ImageLoader.imgs.moveImg);
		cloneBound = new JButton(ImageLoader.imgs.cloneBindImg);
		cloneUnbound = new JButton(ImageLoader.imgs.cloneUnboundImg);

		model.addUpdateListener(this);
	    model.addSelectListener(this);
	    
		// These images allow the user to specify manually.
	    parentSelect = new BoxSelect(model, attachment,true);
	    childSelect = new BoxSelect(model, attachment,false);
	    
		add(childSelect,BorderLayout.WEST);
		add(parentSelect,BorderLayout.EAST);

	    //setConnections();
	    
		// Buttons to move, remove or select this child.
		JPanel moveRemove = new JPanel(new FlowLayout());
		
		this.add(moveRemove,BorderLayout.SOUTH);		
		move.setPreferredSize(new Dimension(25,25));
		cloneBound.setPreferredSize(new Dimension(25,25));
		cloneUnbound.setPreferredSize(new Dimension(25,25));
		moveRemove.add(move);
		moveRemove.add(cloneBound);
		moveRemove.add(cloneUnbound);
		move.setToolTipText("Move to new location.");
		cloneBound.setToolTipText("Create copy that will change with the original.");
		cloneUnbound.setToolTipText("Create copy that will be free from the original.");
		
		move.addActionListener(this);
		cloneBound.addActionListener(this);		
		cloneUnbound.addActionListener(this);		

	}

	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource() == move) {
			// Ask to move this node.
			//model.selectedForMove = child;
		} else if(arg0.getSource() == cloneBound) {
			model.performAction(new Model3DActionClone(attachment,true));
		} else if(arg0.getSource() == cloneUnbound) {
			model.performAction(new Model3DActionClone(attachment,false));
		}		
	}
	
	public void stateChanged(ChangeEvent arg0) {
		// When the selected object changes, change what is enabled.
		setEnabledState();
		setConnections();
	}
	
	public void setEnabledState() {
		/*
		// Check if an object is selected then turn on or off all buttons.
		boolean setVal = model.getSelected() != null && model.getSelected() != model.root;
		for(JRadioButton[] buttonSet : buttons) {
			for(JRadioButton button : buttonSet) {
				button.setEnabled(setVal);
				button.setVisible(setVal);
			}
		}
		cloneBound.setEnabled(setVal);
		cloneUnbound.setEnabled(setVal);
		*/
	}
}
