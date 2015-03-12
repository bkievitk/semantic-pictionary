package action.tree3D;

import modelTools.GeonModel;
import creator3DTree.ObjectAttachment;
import creator3DTree.PrimitiveInstance3DTree;


public class Model3DActionMove extends Model3DAction {

	ObjectAttachment oldAttachment;
	ObjectAttachment newAttachment;
	
	public Model3DActionMove(PrimitiveInstance3DTree toMove, PrimitiveInstance3DTree moveTo) {
				
		// Can not move an item onto itself or one of its children.
		if(toMove == null || moveTo == null || isChild(toMove, moveTo)) {
			error = true;
			return;
		}
		
		// Use old attachment.
		oldAttachment = toMove.parent;
		
		// Attach the centers together.
		short[] attachment1 = new short[3];
		attachment1[0] = ObjectAttachment.CENTER;
		attachment1[1] = ObjectAttachment.CENTER;
		attachment1[2] = ObjectAttachment.CENTER;
		short[] attachment2 = new short[3];
		attachment2[0] = ObjectAttachment.CENTER;
		attachment2[1] = ObjectAttachment.CENTER;
		attachment2[2] = ObjectAttachment.CENTER;
		newAttachment = new ObjectAttachment(moveTo,toMove,attachment1,attachment2, false);
	}
	
	public boolean isChild(PrimitiveInstance3DTree parent, PrimitiveInstance3DTree possibleChild) {
		if(parent == possibleChild) {
			return true;
		}
		for(ObjectAttachment attach : parent.children) {
			if(isChild(attach.child,possibleChild)) {
				return true;
			}
		}
		return false;
	}
	
	public void performAction(GeonModel model) {
		
		// Undo the old attachment.
		oldAttachment.parent.children.remove(oldAttachment);
		
		// Redo the new attachment.
		newAttachment.parent.children.add(newAttachment);
	}
	
	public void undoAction(GeonModel model) {
		// Redo the old attachment.
		oldAttachment.parent.children.add(oldAttachment);
		
		// Undo the new attachment.
		newAttachment.parent.children.remove(newAttachment);
	}

}
