package creator3DTree;

import java.awt.Color;

public class ObjectReduced {

	/**
	 * Write out the object data in reduced readable form.
	 * @param obj
	 * @return
	 */
	public static String toReduced(PrimitiveInstance3DTree obj) {
		String ret;

		// Write out this objects data.
		ret =	obj.type + "" + 
				intFrmt(obj.scale[PrimitiveInstance3DTree.X]) + "" + 
				intFrmt(obj.scale[PrimitiveInstance3DTree.Y]) + "" + 
				intFrmt(obj.scale[PrimitiveInstance3DTree.Z]) + "" + 
				intFrmt(obj.rotation[PrimitiveInstance3DTree.X]) + "" + 
				intFrmt(obj.rotation[PrimitiveInstance3DTree.Y]) + "" + 
				intFrmt(obj.rotation[PrimitiveInstance3DTree.Z]) + "" + 
				intFrmt(obj.color.getRed()) + "" + 
				intFrmt(obj.color.getGreen()) + "" + 
				intFrmt(obj.color.getBlue()) + "";

		// Write out children.
		for(ObjectAttachment child : obj.children) {
			ret = ret + "c" + 
						(child.parentAttachmentPoint[PrimitiveInstance3DTree.X] + 1) + "" +
						(child.parentAttachmentPoint[PrimitiveInstance3DTree.Y] + 1) + "" +
						(child.parentAttachmentPoint[PrimitiveInstance3DTree.Z] + 1) + "" +
						(child.childAttachmentPoint[PrimitiveInstance3DTree.X] + 1) + "" +
						(child.childAttachmentPoint[PrimitiveInstance3DTree.Y] + 1) + "" +
						(child.childAttachmentPoint[PrimitiveInstance3DTree.Z] + 1) + "";
			
			ret = ret + toReduced(child.child);
		}
		ret = ret + "n";
		return ret;
	}
	
	/**
	 * Extract an object stored in the reduced data form.
	 * @param str
	 * @return
	 */
	public static PrimitiveInstance3DTree fromReduced(String str) {
		ObjNum object = fromReduced(str,0,null);
		return object.obj;
	}
		
	/**
	 * Build and object from an XML file.
	 * @param xml XML file as string.
	 * @param beginIndex Offset to read from in xml.
	 * @param parent Parent object.
	 * @return Object and next offset.
	 */
	private static ObjNum fromReduced(String str, int beginIndex, ObjectAttachment parent) {
		
		// Extract type.
		int type = str.charAt(beginIndex) - '0'; beginIndex++;
		
		//Retrieve scale.		
		int[] scale = new int[3];
		scale[0] = intUnFrmt(str,beginIndex); beginIndex+=4;
		scale[1] = intUnFrmt(str,beginIndex); beginIndex+=4;
		scale[2] = intUnFrmt(str,beginIndex); beginIndex+=4;
		
		// Retrieve rotation.
		int[] rotation = new int[3];
		rotation[0] = intUnFrmt(str,beginIndex); beginIndex+=4;
		rotation[1] = intUnFrmt(str,beginIndex); beginIndex+=4;
		rotation[2] = intUnFrmt(str,beginIndex); beginIndex+=4;

		// Retrieve color.
		int red = intUnFrmt(str,beginIndex); beginIndex+=4;
		int green = intUnFrmt(str,beginIndex); beginIndex+=4;
		int blue = intUnFrmt(str,beginIndex); beginIndex+=4;
		Color color = new Color(red,green,blue);
		
		// Build the object.
		PrimitiveInstance3DTree object = new PrimitiveInstance3DTree();
		
		object.type = type;
		object.scale = scale;
		object.rotation = rotation;
		object.color = color;

		// Link the object if you are not root.
		if(parent != null) {
			parent.child = object;
			object.parent = parent;
		}

		// Extract children while there are some.
		while(str.charAt(beginIndex) == 'c') {
			
			// Skip c indicator character.
			beginIndex ++;

			// Extract attachment information.
			short[] parentAttach = new short[3];
			parentAttach[0] = (short)(str.charAt(beginIndex) - '0' - 1); beginIndex++;
			parentAttach[1] = (short)(str.charAt(beginIndex) - '0' - 1); beginIndex++;
			parentAttach[2] = (short)(str.charAt(beginIndex) - '0' - 1); beginIndex++;
			short[] childAttach = new short[3];
			childAttach[0] = (short)(str.charAt(beginIndex) - '0' - 1); beginIndex++;
			childAttach[1] = (short)(str.charAt(beginIndex) - '0' - 1); beginIndex++;
			childAttach[2] = (short)(str.charAt(beginIndex) - '0' - 1); beginIndex++;

			// Build attachment.
			ObjectAttachment attachment = new ObjectAttachment();
			attachment.parent = object;
			attachment.parentAttachmentPoint = parentAttach;
			attachment.childAttachmentPoint = childAttach;
			object.children.add(attachment);

			// Read child.
			ObjNum child = fromReduced(str, beginIndex, attachment);	
			beginIndex = child.num;
			
			// Attach child.
			attachment.child = child.obj;
		}
		
		// Skip child end character.
		beginIndex++;
		
		return new ObjNum(object,beginIndex);
	}
	
	/**
	 * Format an integer as a string.
	 * @param val
	 * @return
	 */
	private static String intFrmt(int val) {
		String sign  = "p";
		if(val < 0) {
			sign = "m";
			val = -val;
		}
		
		if(val < 10) {
			return sign + "00" + val; 
		} else if(val < 100) {
			return sign + "0" + val; 
		} else {
			return sign + "" + val; 
		}
	}

	/**
	 * Take a formatted string and make it into an integer.
	 * @param str
	 * @param index
	 * @return
	 */
	public static int intUnFrmt(String str, int index) {
		char sign = str.charAt(index);
		int v1 = (str.charAt(index+1) - '0');
		int v2 = (str.charAt(index+2) - '0');
		int v3 = (str.charAt(index+3) - '0');
		if(sign == 'p') {
			return v1 * 100 + v2 * 10 + v3;
		} else {
			return -(v1 * 100 + v2 * 10 + v3);
		}
	}
}

class ObjNum {
	
	public PrimitiveInstance3DTree obj;
	public int num;
	
	public ObjNum (PrimitiveInstance3DTree obj, int num) {
		this.obj = obj;
		this.num = num;
	}
}