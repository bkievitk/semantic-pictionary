package action.tree2D;

import creator2DTree.*;

public class Model2DTreeActionClone extends Model2DTreeAction {

	private PrimitiveInstance2DConnection attachment;
	
	public Model2DTreeActionClone(PrimitiveInstance2DConnection attachment, boolean bind) {

		if(attachment == null) {
			error = true;
			return;
		}
		
		if(bind) {
			// This will create a new attachment that references the same subtree.
			this.attachment = attachment.clone();
		} else {
			// This will recursively create a new subtree.
			this.attachment = attachment.cloneRecursive();
		}
	}
	
	public void performAction(Model2DTree model) {
		attachment.parent.children.add(attachment);
		attachment.child.parent = attachment;
	}

	public void undoAction(Model2DTree model) {
		attachment.parent.children.remove(attachment);
	}

}
