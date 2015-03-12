package action.noTree2D;

import java.awt.Color;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.PrimitiveInstance2DNoTree;


/**
 * Change the color of a primitive object.
 * @author bkievitk
 */

public class Model2DNoTreeActionColor extends Model2DNoTreeAction {

	// Track old and new color for undo functionality.
	private Color oldColor;
	private Color newColor;
	
	// Object working on.
	private PrimitiveInstance2DNoTree object;
	
	public Model2DNoTreeActionColor(Color newColor, PrimitiveInstance2DNoTree object) {

		// Color and instance must be valid.
		if(newColor == null || object == null) {
			error = true;
			return;
		}
		
		oldColor = object.color;
		this.newColor = newColor;
		this.object = object;
	}
	
	public void performAction(Model2DNoTree model) {
		object.color = newColor;
	}

	public void undoAction(Model2DNoTree model) {
		object.color = oldColor;
	}
}
