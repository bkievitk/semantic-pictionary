package action.noTree2D;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;

/**
 * Apply scaling to an object in the model.
 * Changes one dimension of scaling.
 * @author bkievitk
 */

public class Model2DNoTreeActionScale extends Model2DNoTreeAction {

	// Track the old and new scale as well as the dimension.
	private PrimitiveInstance2DNoTree instance;
	private int oldScale;
	private int scale;
	private int index;
	
	public Model2DNoTreeActionScale(int scale, int index, PrimitiveInstance2DNoTree instance) {
		
		// Instance and scaling index must be valid.
		if(instance == null || index < 0 || index > 1) {
			error = true;
			return;
		}
		
		oldScale = instance.scale[index];
		this.scale = scale;
		this.index = index;
		this.instance = instance;		
	}
	

	public void performAction(Model2DNoTree model) {
		instance.scale[index] = scale;
	}

	public void undoAction(Model2DNoTree model) {
		instance.scale[index] = oldScale;
	}

	public Model2DNoTreeAction combine(Model2DNoTreeAction newAction) {
		if(newAction instanceof Model2DNoTreeActionScale) {
			Model2DNoTreeActionScale actionTyped = (Model2DNoTreeActionScale)newAction;
			if(actionTyped.instance == instance) {
				actionTyped.oldScale = oldScale;
				return newAction;
			}
		}
		return null;
	}
}
