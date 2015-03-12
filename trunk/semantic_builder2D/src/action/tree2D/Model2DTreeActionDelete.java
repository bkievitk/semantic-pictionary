package action.tree2D;

import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;


public class Model2DTreeActionDelete extends Model2DTreeAction {

	private PrimitiveInstance2DConnection attachment;
	
	public Model2DTreeActionDelete(PrimitiveInstance2DTree object) {
		this(object.parent);
	}
	
	public Model2DTreeActionDelete(PrimitiveInstance2DConnection attachment) {

		if(attachment == null) {
			error = true;
			return;
		}
		
		this.attachment = attachment;
	}
	
	public void performAction(Model2DTree model) {
		attachment.parent.children.remove(attachment);
	}

	public void undoAction(Model2DTree model) {
		attachment.parent.children.add(attachment);
	}

}
