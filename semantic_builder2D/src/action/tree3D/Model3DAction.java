package action.tree3D;

import action.ModelAction;
import modelTools.GeonModel;

/**
 * To change a Model3D object, you need to pass it a Model3DAction.
 * These do everything from change the color to create copies.
 * @author brentkk
 */

public abstract class Model3DAction extends ModelAction {
	
	/**
	 * Perform this action on the model.
	 * @param model Model to perform on.
	 */
	public abstract void performAction(GeonModel model);

	/**
	 * Undo this action once performed on the model.
	 * @param model Model to perform on.
	 */
	public abstract void undoAction(GeonModel model);
	
	/**
	 * Combine both this old action with the new given action.
	 * @param newAction New action to join.
	 * @return Either null if you can't combine, otherwise new joint action.
	 */
	public Model3DAction combine(Model3DAction newAction) {
		return null;
	}
}
