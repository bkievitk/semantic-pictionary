package action.tree2D;

import modelTools.Primitive2D;
import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;


public class Model2DTreeActionType extends Model2DTreeAction {
	
	private Primitive2D oldType;
	private Primitive2D newType;
	private PrimitiveInstance2DTree object;
	
	public Model2DTreeActionType(Primitive2D newType, PrimitiveInstance2DTree object) {
		
		if(object == null) {
			error = true;
			return;
		}
		
		oldType = object.shape;
		this.newType = newType;
		this.object = object;
	}
	
	public void performAction(Model2DTree model) {
		object.shape = newType;
	}

	public void undoAction(Model2DTree model) {
		object.shape = oldType;
	}
}
