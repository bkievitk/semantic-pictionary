package action.noTree2D;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;

/**
 * Change the rotation of the object in the model.
 * @author bkievitk
 */

public class Model2DNoTreeActionRotation extends Model2DNoTreeAction {

	// Store old and new rotations.
	private int oldRotation;
	private int rotation;
	private PrimitiveInstance2DNoTree instance;
	
	public Model2DNoTreeActionRotation(int rotation, PrimitiveInstance2DNoTree instance) {
		
		// Instance must be valid.
		if(instance == null) {
			error = true;
			return;
		}
		
		oldRotation = instance.rotation[0];
		this.rotation = rotation;
		this.instance = instance;		
	}
	

	public void performAction(Model2DNoTree model) {
		instance.rotation[0] = rotation;
	}

	public void undoAction(Model2DNoTree model) {
		instance.rotation[0] = oldRotation;
	}
	
	public Model2DNoTreeAction combine(Model2DNoTreeAction newAction) {
		if(newAction instanceof Model2DNoTreeActionRotation) {
			Model2DNoTreeActionRotation actionTyped = (Model2DNoTreeActionRotation)newAction;
			if(actionTyped.instance == instance) {
				actionTyped.oldRotation = oldRotation;
				return newAction;
			}
		}
		return null;
	}
	
}
