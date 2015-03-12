package action.tree3D;

import java.awt.Color;

import modelTools.GeonModel;

import creator3DTree.ObjectAttachment;
import creator3DTree.PrimitiveInstance3DTree;


public class Model3DActionAdd extends Model3DAction {

	private ObjectAttachment attachment;
	
	public Model3DActionAdd(int childType, PrimitiveInstance3DTree parent) {

		// Error if you have not given a valid parent.
		if(parent == null) {
			error = true;
			return;
		}
		
		// Create a new object.
		PrimitiveInstance3DTree neutral = new PrimitiveInstance3DTree();
		neutral.type = childType;
		neutral.scale[PrimitiveInstance3DTree.X] = 100;
		neutral.scale[PrimitiveInstance3DTree.Y] = 100;
		neutral.scale[PrimitiveInstance3DTree.Z] = 100;
		neutral.rotation[PrimitiveInstance3DTree.X] = 0;
		neutral.rotation[PrimitiveInstance3DTree.Y] = 0;
		neutral.rotation[PrimitiveInstance3DTree.Z] = 0;
		
		// Use default color.
		neutral.color = Color.WHITE;
		setup(neutral, parent);
	}
	
	public Model3DActionAdd(PrimitiveInstance3DTree child, PrimitiveInstance3DTree parent) {
		setup(child,parent);
	}
	
	public void setup(PrimitiveInstance3DTree child, PrimitiveInstance3DTree parent) {
		
		// Error if you have not given a valid parent or child.
		if(parent == null || child == null) {
			error = true;
			return;
		}
		
		// Attach the centers together.
		short[] attachment1 = new short[3];
		attachment1[0] = ObjectAttachment.CENTER;
		attachment1[1] = ObjectAttachment.CENTER;
		attachment1[2] = ObjectAttachment.CENTER;
		short[] attachment2 = new short[3];
		attachment2[0] = ObjectAttachment.CENTER;
		attachment2[1] = ObjectAttachment.CENTER;
		attachment2[2] = ObjectAttachment.CENTER;
		attachment = new ObjectAttachment(parent,child,attachment1,attachment2, false);
	}
	
	public void performAction(GeonModel model) {
		attachment.parent.children.add(attachment);
	}

	public void undoAction(GeonModel model) {
		attachment.parent.children.remove(attachment);
	}

}
