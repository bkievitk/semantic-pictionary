package action.tree3D;

import modelTools.GeonModel;
import creator3DTree.PrimitiveInstance3DTree;


public class Model3DActionType extends Model3DAction {
	
	private int oldType;
	private int newType;
	private PrimitiveInstance3DTree object;
	
	public Model3DActionType(int newType, PrimitiveInstance3DTree object) {
		
		if(object == null) {
			error = true;
			return;
		}
		
		oldType = object.type;
		this.newType = newType;
		this.object = object;
	}
	
	public void performAction(GeonModel model) {
		object.type = newType;
	}

	public void undoAction(GeonModel model) {
		object.type = oldType;
	}
}
