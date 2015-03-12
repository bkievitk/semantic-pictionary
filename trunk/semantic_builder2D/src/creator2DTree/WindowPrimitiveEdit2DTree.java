package creator2DTree;

import java.awt.event.*;
import javax.swing.event.*;

import modelTools.Primitive2D;

import templates.WindowPrimitiveEdit2D;

import action.tree2D.*;

public class WindowPrimitiveEdit2DTree extends WindowPrimitiveEdit2D {

	private static final long serialVersionUID = 2748959238429559068L;

	private Model2DTree model;
	
	public WindowPrimitiveEdit2DTree(Model2DTree modelIn) {
		super(modelIn);
		
		model = modelIn;
		
		xSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model2DTreeActionScale(xSize.getValue(),0,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		ySize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model2DTreeActionScale(ySize.getValue(),1,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		rotation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model2DTreeActionRotation(rotation.getValue(),model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		 
		colorSelect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				model.performAction(new Model2DTreeActionColor(colorSelect.getColor(),model.getSelected()));
			}
		});
				
		for(int buttonID = 0; buttonID < buttons.size();buttonID++) {
			final Primitive2D finalShape = Primitive2D.shapes.get(buttons.get(buttonID).getName());			
			buttons.get(buttonID).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					model.performAction(new Model2DTreeActionType(finalShape,model.getSelected()));
				}
			});	
		}
		
		if(delete != null) {
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(model.selected != null && model.selected != model.root) {
						model.performAction(new Model2DTreeActionDelete(model.getSelected()));
						model.setSelected(null);
					}
				}
			});
		}
	}

}
