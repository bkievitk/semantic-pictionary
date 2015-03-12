package action.tree2D;

import creator2DTree.*;

public class Model2DTreeActionMove extends Model2DTreeAction {

	PrimitiveInstance2DConnection oldAttachment;
	PrimitiveInstance2DConnection newAttachment;
	
	public Model2DTreeActionMove(PrimitiveInstance2DTree toMove, PrimitiveInstance2DTree moveTo) {
				
		// Can not move an item onto itself or one of its children.
		if(toMove == null || moveTo == null || isChild(toMove, moveTo)) {
			error = true;
			return;
		}
		
		// Use old attachment.
		oldAttachment = toMove.parent;
		newAttachment = new PrimitiveInstance2DConnection(toMove,moveTo);
	}
	
	public boolean isChild(PrimitiveInstance2DTree parent, PrimitiveInstance2DTree possibleChild) {
		if(parent == possibleChild) {
			return true;
		}
		for(PrimitiveInstance2DConnection attach : parent.children) {
			if(isChild(attach.child,possibleChild)) {
				return true;
			}
		}
		return false;
	}
	
	public void performAction(Model2DTree model) {
		
		// Undo the old attachment.
		oldAttachment.parent.children.remove(oldAttachment);
		
		// Redo the new attachment.
		newAttachment.parent.children.add(newAttachment);
		newAttachment.child.parent = newAttachment;
	}
	
	public void undoAction(Model2DTree model) {
		// Redo the old attachment.
		oldAttachment.parent.children.add(oldAttachment);
		oldAttachment.child.parent = oldAttachment;
		
		// Undo the new attachment.
		newAttachment.parent.children.remove(newAttachment);
	}

}
