package action.tree2D;

import java.awt.Color;

import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;



public class Model2DTreeActionColor extends Model2DTreeAction {

	private Color oldColor;
	private Color newColor;
	private PrimitiveInstance2DTree object;
	
	public Model2DTreeActionColor(Color newColor, PrimitiveInstance2DTree object) {
		
		if(newColor == null || object == null) {
			error = true;
			return;
		}
		
		oldColor = object.color;
		this.newColor = newColor;
		this.object = object;
	}
	
	public void performAction(Model2DTree model) {
		object.color = newColor;
	}

	public void undoAction(Model2DTree model) {
		object.color = oldColor;
	}
}
