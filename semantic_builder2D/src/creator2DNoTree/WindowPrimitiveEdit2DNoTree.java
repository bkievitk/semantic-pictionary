package creator2DNoTree;

import java.awt.event.*;
import javax.swing.event.*;

import modelTools.Primitive2D;

import templates.WindowPrimitiveEdit2D;

import action.noTree2D.Model2DNoTreeActionColor;
import action.noTree2D.Model2DNoTreeActionDelete;
import action.noTree2D.Model2DNoTreeActionRotation;
import action.noTree2D.Model2DNoTreeActionScale;
import action.noTree2D.Model2DNoTreeActionType;


public class WindowPrimitiveEdit2DNoTree extends WindowPrimitiveEdit2D {

	private static final long serialVersionUID = 2748959238429559068L;

	private Model2DNoTree model;
	
	public WindowPrimitiveEdit2DNoTree(Model2DNoTree modelIn) {
		super(modelIn);
		
		model = modelIn;
		
		xSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model2DNoTreeActionScale(xSize.getValue(),0,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		ySize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model2DNoTreeActionScale(ySize.getValue(),1,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		rotation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model2DNoTreeActionRotation(rotation.getValue(),model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		colorSelect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				model.performAction(new Model2DNoTreeActionColor(colorSelect.getColor(),model.getSelected()));
			}
		});
		
		for(int buttonID = 0; buttonID < Primitive2D.shapes.size();buttonID++) {
			final Primitive2D finalShape = Primitive2D.shapes.get(buttons.get(buttonID).getName());			
			buttons.get(buttonID).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					model.performAction(new Model2DNoTreeActionType(finalShape,model.getSelected()));
				}
			});	
		}
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.performAction(new Model2DNoTreeActionDelete(model.getSelected()));
				model.setSelected(null);
			}
		});
	}

}
