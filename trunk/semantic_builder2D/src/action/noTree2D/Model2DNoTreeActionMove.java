package action.noTree2D;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;

/**
 * Move an object to a new location.
 * @author bkievitk
 */

public class Model2DNoTreeActionMove extends Model2DNoTreeAction {

	// Store old and new location.
	private int[] oldTranslate;
	private int[] translate;
	private PrimitiveInstance2DNoTree instance;
	
	public Model2DNoTreeActionMove(int[] translate, PrimitiveInstance2DNoTree instance) {
		

		// Instance and translate must be valid.
		if(instance == null || translate == null || translate.length != 2) {
			error = true;
			return;
		}
		
		oldTranslate = instance.translate;
		this.translate = translate;
		this.instance = instance;
	}
	
	public void performAction(Model2DNoTree model) {
		instance.translate = translate;
	}

	public void undoAction(Model2DNoTree model) {
		instance.translate = oldTranslate;
	}
	
	public Model2DNoTreeAction combine(Model2DNoTreeAction newAction) {
		if(newAction instanceof Model2DNoTreeActionMove) {
			Model2DNoTreeActionMove actionTyped = (Model2DNoTreeActionMove)newAction;
			if(actionTyped.instance == instance) {
				actionTyped.oldTranslate = oldTranslate;
				return newAction;
			}
		}
		return null;
	}

}
