package action.tree2D;

import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DConnection;

public class Model2DTreeActionTreeConnection extends Model2DTreeAction {

	private short[] oldChild;
	private short[] oldParent;
	private short[] newChild;
	private short[] newParent;
	private PrimitiveInstance2DConnection attachment;
	
	public Model2DTreeActionTreeConnection(short[] newChild, short[] newParent, PrimitiveInstance2DConnection attachment) {
		
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
	
	public void performAction(Model2DTree model) {
		attachment.parentAttachmentPoint = newParent;
		attachment.childAttachmentPoint = newChild;
	}

	public void undoAction(Model2DTree model) {
		attachment.parentAttachmentPoint = oldParent;
		attachment.childAttachmentPoint = oldChild;
	}

}
