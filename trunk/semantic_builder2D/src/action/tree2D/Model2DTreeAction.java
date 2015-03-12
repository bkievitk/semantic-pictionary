package action.tree2D;

import action.ModelAction;
import modelTools.GeonModel;
import creator2DTree.Model2DTree;

public abstract class Model2DTreeAction extends ModelAction {
	
	/**
	 * Perform this action on the model.
	 * @param model Model to perform on.
	 */
	public abstract void performAction(Model2DTree model);
	public void performAction(GeonModel model) { performAction((Model2DTree)model); }

	/**
	 * Undo this action once performed on the model.
	 * @param model Model to perform on.
	 */
	public abstract void undoAction(Model2DTree model);
	public void undoAction(GeonModel model) { undoAction((Model2DTree)model); }
	
	/**
	 * Combine both this old action with the new given action.
	 * @param newAction New action to join.
	 * @return Either null if you can't combine, otherwise new joint action.
	 */
	public Model2DTreeAction combine(Model2DTreeAction newAction) {
		return null;
	}
	public ModelAction combine(ModelAction newAction) { return combine((Model2DTreeAction)newAction); }
}
