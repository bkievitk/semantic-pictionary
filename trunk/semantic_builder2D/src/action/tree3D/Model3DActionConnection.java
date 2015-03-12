package action.tree3D;

import modelTools.GeonModel;
import creator3DTree.ObjectAttachment;


public class Model3DActionConnection extends Model3DAction {

	private short[] oldChild;
	private short[] oldParent;
	private short[] newChild;
	private short[] newParent;
	private ObjectAttachment attachment;
	
	public Model3DActionConnection(short[] newChild, short[] newParent, ObjectAttachment attachment) {
		
		if(attachment == null) {
			error = true;
			return;
		}
		
		oldChild = attachment.childAttachmentPoint;
		oldParent = attachment.parentAttachmentPoint;
		this.newChild = newChild;
		this.newParent = newParent;
		this.attachment = attachment;
	}
	
	public void performAction(GeonModel model) {
		attachment.parentAttachmentPoint = newParent;
		attachment.childAttachmentPoint = newChild;
	}

	public void undoAction(GeonModel model) {
		attachment.parentAttachmentPoint = oldParent;
		attachment.childAttachmentPoint = oldChild;
	}

}
