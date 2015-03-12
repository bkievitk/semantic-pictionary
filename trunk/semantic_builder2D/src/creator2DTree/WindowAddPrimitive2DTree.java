package creator2DTree;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import modelTools.Primitive2D;

import templates.WindowAddPrimitive;

import action.tree2D.Model2DTreeActionAdd;


/**
 * Add a primitive to the scene.
 * @author bkievitk
 */

public class WindowAddPrimitive2DTree extends WindowAddPrimitive {
	
	private static final long serialVersionUID = 3909819993193071502L;
	
	Model2DTree model;
	
	public WindowAddPrimitive2DTree(Model2DTree modelIn) {
		super(modelIn);
		model = modelIn;
		
		setLayout(new GridLayout(2,4));
		model.addSelectListener(this);
		
		for(Primitive2D shape : Primitive2D.shapes.values()) {
			
			JButton button = new JButton(shape.img);
			button.setDisabledIcon(shape.disabled);
			
			buttons.add(button);
			add(button);
			
			final Primitive2D finalShape = shape;
			
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					model.performAction(new Model2DTreeActionAdd(finalShape,model.getSelected()));
					
					// Set the child as the selected node.
					PrimitiveInstance2DTree oldSelected = model.getSelected();
					
					// If there is an objects selected.
					if(oldSelected != null) {
						
						// Get the newest child and set it as selected.
						PrimitiveInstance2DConnection newSelected = oldSelected.children.get(oldSelected.children.size()-1);
						if(newSelected != null) {
							model.setSelected(newSelected.child);
						}
					}
				}
			});			
		}
		
		setEnabledState();
	}
}
