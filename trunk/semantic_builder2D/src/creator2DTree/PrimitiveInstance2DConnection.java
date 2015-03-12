package creator2DTree;

import java.io.Serializable;

import tools.MyArrays;

/**
 * This is the connection between two PrimitveInstances.
 * Defines the control points.
 * @author bkievitk
 */

public class PrimitiveInstance2DConnection implements Serializable {

	private static final long serialVersionUID = -5679184771177607212L;
	
	// Define the two endpoints of the connection.
	public PrimitiveInstance2DTree child;
	public PrimitiveInstance2DTree parent;

	// Short hands to refer to positions.
	public static final short RIGHT = 1;   // X+
	public static final short LEFT = -1;   // X-
	public static final short TOP = 1;     // Y+
	public static final short BOTTOM = -1; // Y-
	public static final short CENTER = 0;  // X or Y centered.

	// Attachment points.
	public short[] parentAttachmentPoint = null;
	public short[] childAttachmentPoint = null;
	
	public String toString() {
		return parentAttachmentPoint[0] + "," + parentAttachmentPoint[1] + " " + childAttachmentPoint[0] + "," + childAttachmentPoint[1];
	}
	
	public PrimitiveInstance2DConnection(PrimitiveInstance2DTree child, PrimitiveInstance2DTree parent) {
		this.child = child;
		this.parent = parent;

		parentAttachmentPoint = new short[2];
		parentAttachmentPoint[0] = RIGHT;
		parentAttachmentPoint[1] = TOP;
		
		childAttachmentPoint = new short[2];
		childAttachmentPoint[0] = CENTER;
		childAttachmentPoint[1] = CENTER;
	}
	
	public PrimitiveInstance2DConnection(PrimitiveInstance2DTree child, PrimitiveInstance2DTree parent, short[] childAttachmentPoint, short[] parentAttachmentPoint) {
		this.child = child;
		this.parent = parent;
		this.parentAttachmentPoint = parentAttachmentPoint;
		this.childAttachmentPoint = childAttachmentPoint;
	}

	/**
	 * This creates a clone of the connection data.
	 * This will not clone the descendent nodes.
	 */
	public PrimitiveInstance2DConnection clone() {
		short[] newParentAttachmentPoint = MyArrays.copyOf(parentAttachmentPoint);
		short[] newChildAttachmentPoint = MyArrays.copyOf(childAttachmentPoint);
		return new PrimitiveInstance2DConnection(child,parent,newChildAttachmentPoint,newParentAttachmentPoint);
	}

	/**
	 * This creates a clone of the connection data as well as the descendent nodes.
	 * @return
	 */
	public PrimitiveInstance2DConnection cloneRecursive() {
		short[] newParentAttachmentPoint = MyArrays.copyOf(parentAttachmentPoint);
		short[] newChildAttachmentPoint = MyArrays.copyOf(childAttachmentPoint);
		return new PrimitiveInstance2DConnection(child.cloneRecursive(),parent,newChildAttachmentPoint,newParentAttachmentPoint);
	}
}
