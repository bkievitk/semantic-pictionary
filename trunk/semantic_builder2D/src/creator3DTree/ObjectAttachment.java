package creator3DTree;

import tools.MyArrays;

/**
 * Represents the attachment between two objects.
 * @author brentkk
 */

public class ObjectAttachment {
	public static final short RIGHT = 1;   // X+
	public static final short LEFT = -1;   // X-
	public static final short TOP = 1;     // Y+
	public static final short BOTTOM = -1; // Y-
	public static final short FRONT = 1;   // Z+
	public static final short BACK = -1;   // Z-
	public static final short CENTER = 0;  // X,Y, or Z centered.

	public PrimitiveInstance3DTree parent;
	public PrimitiveInstance3DTree child;

	// Use x,y,z default values r,l,t,b,b,c above.
	public short[] parentAttachmentPoint = null;
	public short[] childAttachmentPoint = null;

	/**
	 * Create an attachment.
	 * @param parent Parent object.
	 * @param child Child object.
	 * @param parentAttachmentPoint Location of attachment for parent.
	 * @param childAttachmentPoint Location of attachment for child.
	 * @param linkup If true, ask the parent to link to this.
	 */
	public ObjectAttachment(PrimitiveInstance3DTree parent, PrimitiveInstance3DTree child, short[] parentAttachmentPoint, short[] childAttachmentPoint, boolean linkup) {
		this.parent = parent;
		this.child = child;
		this.parentAttachmentPoint = parentAttachmentPoint;
		this.childAttachmentPoint = childAttachmentPoint;
		
		// Link the classes together.
		child.parent = this;
		
		if(linkup) {
			parent.children.add(this);
		}
	}
	
	public ObjectAttachment() {		
	}
	
	/**
	 * Create a copy of the attachment using the same children.
	 */
	public ObjectAttachment clone() {
		short[] newParentAttachmentPoint = MyArrays.copyOf(parentAttachmentPoint);
		short[] newChildAttachmentPoint = MyArrays.copyOf(childAttachmentPoint);
		ObjectAttachment objectAttachment = new ObjectAttachment(parent,child,newParentAttachmentPoint,newChildAttachmentPoint,false);
		return objectAttachment;
	}

	/**
	 * Create a copy of the attachment recursively creating the children.
	 * @param parent Parent node.
	 * @return Copy.
	 */
	public ObjectAttachment cloneRecursive(PrimitiveInstance3DTree parent) {
		ObjectAttachment clone = new ObjectAttachment();
		clone.parentAttachmentPoint = MyArrays.copyOf(parentAttachmentPoint);
		clone.childAttachmentPoint = MyArrays.copyOf(childAttachmentPoint);
		clone.parent = parent;		
		clone.child = child.cloneRecursive(clone);	
		return clone;
	}
	
}
