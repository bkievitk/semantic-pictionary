package modelTools;

import java.awt.Color;
import java.io.Serializable;

/**
 * This is the type of object that the model is made of.
 * @author bkievitk
 */

public abstract class PrimitiveInstance implements Serializable {
	
	private static final long serialVersionUID = -668122909565574221L;
	
	public int[] scale;
	public int[] rotation;
	public Color color;

	public static final int EDIT_ROTATION = 0;
	public static final int EDIT_SCALE = 1;
	public static final int EDIT_COLOR = 2;
	public static final int EDIT_SHAPE = 3;
	public static final int EDIT_ADD = 4;
	public static final int EDIT_MOVE = 5;
	public static final String[] EDIT_NAMES = {"rotation","scale","color","shape","add","move"};
	
	protected byte editable = (byte)0xFF;
	
	public boolean isEditable(int type) {
		return ((editable >> type) & 1) == 1;
	}
	
	public void setEditable(int type, boolean value) {
		// Turn on bit first.
		editable |= (1 << type);
		if(!value) {
			editable ^= (1 << type);
		}
	}
	
	public abstract PrimitiveInstance cloneRecursive();
	public abstract int countObjects();
	public abstract PrimitiveInstance getClicked(int x, int y);
	public abstract void addChild(PrimitiveInstance obj);
}
