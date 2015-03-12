package action.tree3D;

import modelTools.GeonModel;
import creator3DTree.ObjectAttachment;

public class Model3DActionDelete extends Model3DAction {

	private ObjectAttachment attachment;
	
	public Model3DActionDelete(ObjectAttachment attachment) {

		if(attachment == null) {
			error = true;
			return;
		}
		
		this.attachment = attachment;
	}
	
	public void performAction(GeonModel model) {
		attachment.parent.children.remove(attachment);
	}

	public void undoAction(GeonModel model) {
		attachment.parent.children.add(attachment);
	}

}
