package creator3DTree;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;
import modelTools.Primitive3D;

import templates.WindowAddPrimitive;

import action.tree3D.Model3DActionAdd;


/**
 * This panel allows the user to add a new primitive child to the active object.
 * @author brentkk
 */

public class WindowAddPrimitive3DTree extends WindowAddPrimitive implements ChangeListener {
	
	private static final long serialVersionUID = 6702540994701105633L;
			
	/**
	 * Simply build and link the buttons.
	 * @param mainWindow
	 */
	public WindowAddPrimitive3DTree(GeonModel newModel) {
		super(newModel);
		
		// Ask to be informed of changes.
	    model.addSelectListener(this);

		// Add buttons for each primitive and link to this as a listener.
		setLayout(new GridLayout(2,4));
		
		// For each Geon, create a button.
		for(final Primitive3D p : Primitive3D.shapes.values()) {
			JButton button = new JButton(p.img);			
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					Model3DActionAdd addAction = new Model3DActionAdd(p.id,(PrimitiveInstance3DTree)model.getSelected());
					model.performAction(addAction);
					
					// Set the child as the selected node.
					PrimitiveInstance3DTree oldSelected = (PrimitiveInstance3DTree)model.getSelected();
					
					// If there is an objects selected.
					if(oldSelected != null) {
						
						// Get the newest child and set it as selected.
						ObjectAttachment newSelected = oldSelected.children.get(oldSelected.children.size()-1);
						if(newSelected != null) {
							model.setSelected(newSelected.child);
						}
					}
				}
			});

			// Set properties.
			button.setPreferredSize(new Dimension(50,50));
			button.setToolTipText("Add " + p.name + " child.");
			button.setDisabledIcon(p.disabled);
			add(button);
			
			// Add to list.
			buttons.add(button);
		}
		
		// Turn on or off buttons.
		setEnabledState();
	}

	public void stateChanged(ChangeEvent arg0) {
		// When the selected object changes, change what is enabled.
		setEnabledState();
	}
	
	public void setEnabledState() {
		// Check if an object is selected then turn on or off all buttons.
		boolean setVal = model.getSelected() != null;
		for(JButton b : buttons) {
			b.setEnabled(setVal);
		}
	}
	
}
