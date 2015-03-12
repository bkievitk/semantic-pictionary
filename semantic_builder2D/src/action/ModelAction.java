package action;

import modelTools.GeonModel;

/**
 * This is the base unit for the undoable actions.
 * READY
 * @author bkievitk
 */

public abstract class ModelAction {
	
	// Error making the action because this is an invalid action.
	public boolean error = false;
	
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
	public ModelAction combine(ModelAction newAction) {
		return null;
	}
}
