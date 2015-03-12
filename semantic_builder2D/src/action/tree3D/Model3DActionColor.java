package action.tree3D;


import java.awt.Color;

import modelTools.GeonModel;

import creator3DTree.PrimitiveInstance3DTree;


public class Model3DActionColor extends Model3DAction {

	private Color oldColor;
	private Color newColor;
	private PrimitiveInstance3DTree object;
	
	public Model3DActionColor(Color newColor, PrimitiveInstance3DTree object) {
		
		if(newColor == null || object == null) {
			error = true;
			return;
		}
		
		oldColor = object.color;
		this.newColor = newColor;
		this.object = object;
	}
	
	public void performAction(GeonModel model) {
		object.color = newColor;
	}

	public void undoAction(GeonModel model) {
		object.color = oldColor;
	}

}
