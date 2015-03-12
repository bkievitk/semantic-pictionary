package action.noTree2D;

import modelTools.Primitive2D;
import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;

/**
 * Change the primitive type of an object in the model.
 * @author bkievitk
 */

public class Model2DNoTreeActionType extends Model2DNoTreeAction {

	// Store the old and new type.
	private Primitive2D oldType;
	private Primitive2D newType;
	private PrimitiveInstance2DNoTree object;
	
	public Model2DNoTreeActionType(Primitive2D newType, PrimitiveInstance2DNoTree object) {
		
		if(object == null || newType == null) {
			error = true;
			return;
		}
		
		oldType = object.shape;
		this.newType = newType;
		this.object = object;
	}
	
	public void performAction(Model2DNoTree model) {
		object.shape = newType;
	}

	public void undoAction(Model2DNoTree model) {
		object.shape = oldType;
	}
}
