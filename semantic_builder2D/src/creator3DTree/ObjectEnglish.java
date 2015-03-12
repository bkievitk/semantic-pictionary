package creator3DTree;

import modelTools.Primitive3D;

public class ObjectEnglish {

	/**
	 * Write out the object as English words.
	 * @param obj
	 * @return
	 */
	public static String toEnglish(PrimitiveInstance3DTree obj) {

		String xStr;
		switch(obj.scale[PrimitiveInstance3DTree.X]) {
			case 25: xStr = "very narrow, "; break;
			case 50: xStr = "narrow, "; break;
			case 100: xStr = ""; break;
			case 200: xStr = "wide, "; break;
			case 400: xStr = "very wide, "; break;
			default: xStr = "unknown X scale, ";
		}

		String yStr;
		switch(obj.scale[PrimitiveInstance3DTree.Y]) {
			case 25: yStr = "very short, "; break;
			case 50: yStr = "short, "; break;
			case 100: yStr = ""; break;
			case 200: yStr = "tall, "; break;
			case 400: yStr = "very tall, "; break;
			default: yStr = "unknown Y scale, ";
		}

		String zStr;
		switch(obj.scale[PrimitiveInstance3DTree.Z]) {
			case 25: zStr = "very shallow, "; break;
			case 50: zStr = "shallow, "; break;
			case 100: zStr = ""; break;
			case 200: zStr = "deap, "; break;
			case 400: zStr = "very deap, "; break;
			default: zStr = "unknown Z scale, ";
		}
		
		String colorStr;
		switch(obj.color.getRGB()) {
			case 0x000000: colorStr = "black"; break;
			case 0x808080: colorStr = "dark gray"; break;
			case 0x800000: colorStr = "dark red"; break;
			case 0x808000: colorStr = "dark yellow"; break;
			case 0x008000: colorStr = "dark green"; break;
			case 0x008080: colorStr = "dark blue green"; break;
			case 0x000080: colorStr = ""; break;
			case 0x800080: colorStr = ""; break;
			case 0x808040: colorStr = ""; break;
			case 0x004040: colorStr = ""; break;
			case 0x0080FF: colorStr = ""; break;
			case 0x004080: colorStr = ""; break;
			case 0x8000FF: colorStr = "purple"; break;
			case 0x804000: colorStr = "brown"; break;
			case 0xFFFFFF: colorStr = "white"; break;
			case 0xC0C0C0: colorStr = "light gray"; break;
			case 0xFF0000: colorStr = "red"; break;
			case 0xFFFF00: colorStr = "yellow"; break;
			case 0x00FF00: colorStr = "green"; break;
			case 0x00FFFF: colorStr = "blue green"; break;
			case 0x0000FF: colorStr = "blue"; break;
			case 0xFF00FF: colorStr = "pink"; break;
			case 0xFFFF80: colorStr = "light yellow"; break;
			case 0x00FF80: colorStr = ""; break;
			case 0x80FFFF: colorStr = ""; break;
			case 0x8080FF: colorStr = "purple"; break;
			case 0xFF0080: colorStr = ""; break;
			case 0xFF8040: colorStr = "orange"; break;
			default: colorStr = "unknown color";
		}
		
		String ret = colorStr + ", " + xStr + yStr + zStr + Primitive3D.shapes.get(obj.type);
		
		for(ObjectAttachment child : obj.children) {
			String childRet = toEnglish(child.child);
			String childAttach = "";
			String parentAttach = "";
			
			switch(child.parentAttachmentPoint[PrimitiveInstance3DTree.X]) {
				case -1: parentAttach += "right, "; break;
				case 0: parentAttach += ""; break;
				case 1: parentAttach += "left, "; break;
				default: parentAttach = "unknown X attachment, ";
			}
			switch(child.parentAttachmentPoint[PrimitiveInstance3DTree.Y]) {
				case -1: parentAttach += "top, "; break;
				case 0: parentAttach += ""; break;
				case 1: parentAttach += "bottom, "; break;
				default: parentAttach = "unknown Y attachment, ";
			}
			switch(child.parentAttachmentPoint[PrimitiveInstance3DTree.Z]) {
				case -1: parentAttach += "front, "; break;
				case 0: parentAttach += ""; break;
				case 1: parentAttach += "back, "; break;
				default: parentAttach = "unknown Z attachment, ";
			}
			if(parentAttach.length() == 0) {
				parentAttach = "center, ";
			}
			
			switch(child.childAttachmentPoint[PrimitiveInstance3DTree.X]) {
				case -1: childAttach += "right, "; break;
				case 0: childAttach += ""; break;
				case 1: childAttach += "left, "; break;
				default: zStr = "unknown X attachment, ";
			}
			switch(child.childAttachmentPoint[PrimitiveInstance3DTree.Y]) {
				case -1: childAttach += "top, "; break;
				case 0: childAttach += ""; break;
				case 1: childAttach += "bottom, "; break;
				default: zStr = "unknown Y attachment, ";
			}
			switch(child.childAttachmentPoint[PrimitiveInstance3DTree.Z]) {
				case -1: childAttach += "front, "; break;
				case 0: childAttach += ""; break;
				case 1: childAttach += "back, "; break;
				default: zStr = "unknown Z attachment, ";
			}
			if(childAttach.length() == 0) {
				childAttach = "center, ";
			}
			
			ret = ret + " whose " + parentAttach + "is attached to the " + childAttach + "of a " + childRet;
		}
		
		return ret;
	}
}
