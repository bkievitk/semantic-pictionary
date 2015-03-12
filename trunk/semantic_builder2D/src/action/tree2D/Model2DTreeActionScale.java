package action.tree2D;

import tools.MyArrays;
import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;

public class Model2DTreeActionScale extends Model2DTreeAction {

	private int[] oldScale;
	private int[] newScale;
	private PrimitiveInstance2DTree object;
	
	public Model2DTreeActionScale(int[] newScale, PrimitiveInstance2DTree object) {
		
		if(object == null) {
			error = true;
			return;
		}
		
		oldScale = object.scale;
		this.newScale = newScale;
		this.object = object;
	}
	
	public Model2DTreeActionScale(int newScale, int index, PrimitiveInstance2DTree object) {
		oldScale = object.scale;	
		int[] scale = MyArrays.copyOf(object.scale);
		scale[index] = newScale;		
		this.newScale = scale;
		this.object = object;
	}
	
	public void performAction(Model2DTree model) {
		object.scale = newScale;
	}

	public void undoAction(Model2DTree model) {
		object.scale = oldScale;
	}
	
	public Model2DTreeAction combine(Model2DTreeAction newAction) {
		// If this is to combine with a new scale action.
		if(newAction instanceof Model2DTreeActionScale) {
			// Set the new scale old to this ones old, bypassing it.
			((Model2DTreeActionScale)newAction).oldScale = oldScale;
			return newAction;
		}
		return null;
	}

}
