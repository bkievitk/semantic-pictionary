package action.tree3D;

import modelTools.GeonModel;
import creator3DTree.ObjectAttachment;

public class Model3DActionClone extends Model3DAction {

	private ObjectAttachment attachment;
	
	public Model3DActionClone(ObjectAttachment attachment, boolean bind) {

		if(attachment == null) {
			error = true;
			return;
		}
		
		if(bind) {
			// This will create a new attachment that references the same subtree.
			this.attachment = attachment.clone();
		} else {
			// This will recursively create a new subtree.
			this.attachment = attachment.cloneRecursive(attachment.parent);
		}
	}
	
	public void performAction(GeonModel model) {
		attachment.parent.children.add(attachment);
	}

	public void undoAction(GeonModel model) {
		attachment.parent.children.remove(attachment);
	}

}
