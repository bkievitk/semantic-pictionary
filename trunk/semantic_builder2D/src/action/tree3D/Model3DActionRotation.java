package action.tree3D;

import modelTools.GeonModel;
import tools.MyArrays;
import creator3DTree.PrimitiveInstance3DTree;

public class Model3DActionRotation extends Model3DAction {
	
	private int[] oldRotation;
	private int[] newRotation;
	private PrimitiveInstance3DTree object;
	
	public Model3DActionRotation(int[] newRotation, PrimitiveInstance3DTree object) {
		
		if(object == null) {
			error = true;
			return;
		}
		
		oldRotation = object.rotation;
		this.newRotation = newRotation;
		this.object = object;
	}
	
	public Model3DActionRotation(int newRotation, int index, PrimitiveInstance3DTree object) {
		oldRotation = object.rotation;		
		int[] rotation = MyArrays.copyOf(object.rotation);
		rotation[index] = newRotation;		
		this.newRotation = rotation;
		this.object = object;
	}
	
	public void performAction(GeonModel model) {
		object.rotation = newRotation;
	}

	public void undoAction(GeonModel model) {
		object.rotation = oldRotation;
	}
	
	public Model3DAction combine(Model3DAction newAction) {
		// If this is to combine with a new rotation action.
		if(newAction instanceof Model3DActionRotation) {
			// Set the new rotations old to this ones old, bypassing it.
			((Model3DActionRotation)newAction).oldRotation = oldRotation;
			return newAction;
		}
		return null;
	}

}
