package action.tree2D;

import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;

public class Model2DTreeActionRotation extends Model2DTreeAction {
	
	private int oldRotation;
	private int newRotation;
	private PrimitiveInstance2DTree object;
	
	public Model2DTreeActionRotation(int newRotation, PrimitiveInstance2DTree object) {
		
		if(object == null) {
			error = true;
			return;
		}
		
		oldRotation = object.rotation[0];
		this.newRotation = newRotation;
		this.object = object;
	}
	
	public void performAction(Model2DTree model) {
		object.rotation[0] = newRotation;
	}

	public void undoAction(Model2DTree model) {
		object.rotation[0] = oldRotation;
	}
	
	public Model2DTreeAction combine(Model2DTreeAction newAction) {
		// If this is to combine with a new rotation action.
		if(newAction instanceof Model2DTreeActionRotation) {
			// Set the new rotations old to this ones old, bypassing it.
			((Model2DTreeActionRotation)newAction).oldRotation = oldRotation;
			return newAction;
		}
		return null;
	}

}
