package animals;

import java.util.Vector;

import modelTools.PrimitiveInstance;
import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;

public class KeyAssignment {
		
	public char key;
	public transient PrimitiveInstance2DTree object;
	public Vector<Integer> id;
	public int type;
	public transient Model2DTree model;
	public boolean direction;
	
	public KeyAssignment(char key, PrimitiveInstance2DTree object, int type, Model2DTree model, boolean direction) {
		this.key = key;
		this.object = object;
		this.type = type;
		this.model = model;
		this.direction = direction;
		id = object.getID();
	}
	
	public void connect(Model2DTree model) {
		this.model = model;
		object = model.root.getFromID(id);
	}
	
	public String toString() {
		String ret = key + " ";
		
		if(type == 0) {
			ret += "rot ";
		} else if(type == 1) {
			ret += "sclX ";
		} else if(type == 2) {
			ret += "sclY ";
		} else if(type == 3) {
			ret += "scl ";
		}
		
		if(direction) {
			ret += "+";
		} else {
			ret += "-";
		}
		return ret;
	}
	
	public void tick() {
		
		switch(type) {
			case 0:
				// rotation
				if(object.isEditable(PrimitiveInstance.EDIT_ROTATION)) {
					if(direction) {
						object.rotation[0] += 1;
					} else {
						object.rotation[0] -= 1;
					}
				}
			break;
			case 1:
				// scaleX
				if(object.isEditable(PrimitiveInstance.EDIT_SCALE)) {
					if(direction) {
						if(object.scale[0] < 350) {
							object.scale[0] += 1;
						}
					} else {
						if(object.scale[0] > 0) {
							object.scale[0] -= 1;
						}
					}
				}
			break;
			case 2:
				// scaleY
				if(object.isEditable(PrimitiveInstance.EDIT_SCALE)) {
					if(direction) {
						if(object.scale[1] < 350) {
							object.scale[1] += 1;
						}
					} else {
						if(object.scale[1] > 0) {
							object.scale[1] -= 1;
						}
					}
				}
			break;
			case 3:
				// scale
				if(object.isEditable(PrimitiveInstance.EDIT_SCALE)) {
					if(direction) {
						if(object.scale[0] < 350 && object.scale[1] < 350) {
							object.scale[0] += 1;
							object.scale[1] += 1;
						}
					} else {
						if(object.scale[0] > 0 && object.scale[1] > 0) {
							object.scale[0] -= 1;
							object.scale[1] -= 1;
						}
					}
				}
			break;
		}
	}	
}
