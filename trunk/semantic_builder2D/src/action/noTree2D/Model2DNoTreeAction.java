package action.noTree2D;

import action.ModelAction;
import modelTools.GeonModel;
import creator2DNoTree.Model2DNoTree;

/**
 * This is an action module that can be performed and undone from a model.
 * @author bkievitk
 */

public abstract class Model2DNoTreeAction extends ModelAction {
	
	/**
	 * Perform this action on the model.
	 * @param model Model to perform on.
	 */
	public abstract void performAction(Model2DNoTree model);
	public void performAction(GeonModel model) { performAction((Model2DNoTree)model); }

	/**
	 * Undo this action once performed on the model.
	 * @param model Model to perform on.
	 */
	public abstract void undoAction(Model2DNoTree model);
	public void undoAction(GeonModel model) { undoAction((Model2DNoTree)model); }
	
	/**
	 * Combine both this old action with the new given action.
	 * @param newAction New action to join.
	 * @return Either null if you can't combine, otherwise new joint action.
	 */
	public Model2DNoTreeAction combine(Model2DNoTreeAction newAction) {
		return null;
	}
	public ModelAction combine(ModelAction newAction) { return combine((Model2DNoTreeAction)newAction); }
}
