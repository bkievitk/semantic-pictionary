package action.tree2D;

import java.awt.Color;

import modelTools.Primitive2D;

import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;


public class Model2DTreeActionAdd extends Model2DTreeAction {

	private PrimitiveInstance2DConnection attachment;

	public Model2DTreeActionAdd(PrimitiveInstance2DTree child, PrimitiveInstance2DTree parent) {

		// Error if you have not given a valid parent.
		if(parent == null) {
			error = true;
			return;
		}
		
		setup(child, parent);

	}
	
	public Model2DTreeActionAdd(Primitive2D shape, PrimitiveInstance2DTree parent) {

		// Error if you have not given a valid parent.
		if(parent == null) {
			error = true;
			return;
		}
		
		// Create a new object.
		PrimitiveInstance2DTree neutral = new PrimitiveInstance2DTree(shape);
		
		// Use default color.
		neutral.color = Color.WHITE;
		setup(neutral, parent);
	}
	
	//public Model2DActionAdd(Object2D child, Object2D parent) {
	//	setup(child,parent);
	//}
	
	public void setup(PrimitiveInstance2DTree child, PrimitiveInstance2DTree parent) {
		
		// Error if you have not given a valid parent or child.
		if(parent == null || child == null) {
			error = true;
			return;
		}
		
		attachment = new PrimitiveInstance2DConnection(child,parent);
		child.parent = attachment;
	}
	
	public void performAction(Model2DTree model) {
		attachment.parent.children.add(attachment);
	}

	public void undoAction(Model2DTree model) {
		attachment.parent.children.remove(attachment);
	}

}
