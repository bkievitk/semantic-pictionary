package action.tree3D;

import modelTools.GeonModel;
import tools.MyArrays;
import creator3DTree.PrimitiveInstance3DTree;

public class Model3DActionScale extends Model3DAction {

	private int[] oldScale;
	private int[] newScale;
	private PrimitiveInstance3DTree object;
	
	public Model3DActionScale(int[] newScale, PrimitiveInstance3DTree object) {
		
		if(object == null) {
			error = true;
			return;
		}
		
		oldScale = object.scale;
		this.newScale = newScale;
		this.object = object;
	}
	
	public Model3DActionScale(int newScale, int index, PrimitiveInstance3DTree object) {
		oldScale = object.scale;		
		int[] scale = MyArrays.copyOf(object.scale);
		scale[index] = newScale;		
		this.newScale = scale;
		this.object = object;
	}
	
	public void performAction(GeonModel model) {
		object.scale = newScale;
	}

	public void undoAction(GeonModel model) {
		object.scale = oldScale;
	}
	
	public Model3DAction combine(Model3DAction newAction) {
		// If this is to combine with a new scale action.
		if(newAction instanceof Model3DActionScale) {
			// Set the new scale old to this ones old, bypassing it.
			((Model3DActionScale)newAction).oldScale = oldScale;
			return newAction;
		}
		return null;
	}

}
