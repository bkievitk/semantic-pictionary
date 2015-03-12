package action.noTree2D;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;

/**
 * Add a primitive to the model.
 * @author bkievitk
 */

public class Model2DNoTreeActionAdd extends Model2DNoTreeAction {

	// Added primitive.
	private PrimitiveInstance2DNoTree instance;
	
	public Model2DNoTreeActionAdd(PrimitiveInstance2DNoTree instance) {
		
		// Must have valid instance.
		if(instance == null) {
			error = true;
			return;
		}
		
		this.instance = instance;
	}
	
	public void performAction(Model2DNoTree model) {
		// Simply add to the object list.
		model.objects.add(instance);
	}

	public void undoAction(Model2DNoTree model) {
		// Remove from the object list.
		model.objects.remove(instance);
	}
	
}
