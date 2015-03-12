package action.noTree2D;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;

/**
 * Remove an object from the model.
 * @author bkievitk
 */

public class Model2DNoTreeActionDelete extends Model2DNoTreeAction {

	// Object to be removed.
	private PrimitiveInstance2DNoTree instance;
	
	public Model2DNoTreeActionDelete(PrimitiveInstance2DNoTree instance) {

		// Instance must be valid.
		if(instance == null) {
			error = true;
			return;
		}
		
		this.instance = instance;
	}
	
	public void performAction(Model2DNoTree model) {
		model.objects.remove(instance);
	}

	public void undoAction(Model2DNoTree model) {
		model.objects.add(instance);
	}

}
