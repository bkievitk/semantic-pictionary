package creator3DTree;

import java.awt.Color;
import java.util.*;

import javax.swing.tree.*;

import modelTools.Primitive3D;
import modelTools.PrimitiveInstance;
import my3D.Material;
import my3D.Object3D;
import my3D.TransformMy3D;

import shapes3D.*;
import tools.MyArrays;


/**
 * This represents one primitive object.
 * @author brentkk
 */

public class PrimitiveInstance3DTree extends PrimitiveInstance {

	private static final long serialVersionUID = 5104003808251259511L;
	
	// Family relations.
	public ObjectAttachment parent;
	public Vector<ObjectAttachment> children = new Vector<ObjectAttachment>();
	
	// Type of this primitive.
	// This must be of the primitive type specified above.
	public int type;
	
	
	// Used to write out names for category.
	public static final String[] XYZ = {"x","y","z"};
	public static final String[] RGB = {"r","g","b"};
	
	// Types of primitives.
	public static final int CYLINDER = 0;
	public static final int SPHERE = 1;
	public static final int WEDGE = 2;
	public static final int CUBE = 3;
	public static final int CONE = 4;
	public static final int HANDLE = 5;
	public static final int PYRAMID = 6;
	public static final int HALFSPHERE = 7;

	// Axis have values.
	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	

	/**
	 * Count how many primitives are below this one plus itself.
	 * @return Number of primitives.
	 */
	public int countObjects() {
		int count = 1;
		for(ObjectAttachment child : children) {
			count += child.child.countObjects();
		}
		return count;
	}
	
	public PrimitiveInstance3DTree cloneRecursive() {
		return cloneRecursive(null);
	}
		
	/**
	 * Recursively clone this and all of its attachments.
	 * @param attachment The parent attachment.
	 * @return Cloned copy.
	 */
	public PrimitiveInstance3DTree cloneRecursive(ObjectAttachment attachment) {
		
		PrimitiveInstance3DTree clone = new PrimitiveInstance3DTree();

		// Copy over simple properties.
		clone.parent = attachment;
		clone.type = type;
		clone.scale = MyArrays.copyOf(scale);
		clone.rotation = MyArrays.copyOf(rotation);
		clone.color = new Color(color.getRGB());
				
		// Copy all of the children.
		for(ObjectAttachment oa : children) {
			clone.children.add(oa.cloneRecursive(this));
		}
		
		return clone;
	}
	
	/**
	 * Add this and its children to the tree model.
	 * @param parent Parent in the tree model.
	 */
	public void addToTree(DefaultMutableTreeNode parent) {
		
		// Add this.
		DefaultMutableTreeNode primitive = new DefaultMutableTreeNode(this);
		parent.add(primitive);
		
		// Add children.
		for(ObjectAttachment child : children) {
			PrimitiveInstance3DTree childNode = child.child;
			childNode.addToTree(primitive);
		}
	}
	
	/**
	 * Take the structure of the object and add it to the 3D world.
	 * @param p
	 */
	public void addToWorld(Object3D p, PrimitiveInstance3DTree selected) {
		addToWorld(p,0,selected);
	}
	

	public TransformMy3D getOrientationOf() {
		TransformMy3D t = new TransformMy3D();
		t.combine(getOrientationOfHelper());
		return t;
	}
	
	private TransformMy3D getOrientationOfHelper() {
		TransformMy3D t = new TransformMy3D();
		if(parent != null) {
			TransformMy3D rotate = myMakeRotationTransform(rotation);
			t.combine(rotate);
			
			t.combine(parent.parent.getOrientationOfHelper());
		}
		
		return t;
	}
	
	private void addToWorld(Object3D p, double offset, PrimitiveInstance3DTree selected) {
		double[] center = {0,0,0};
				
		// Get this objects location.

		Object3D thisObject = null;
		
		// Build the primitive.
		switch(type) {		
			case CUBE: thisObject = new Cube3D(center, 2, new Material(color, this,null)); break;
			case CONE: thisObject = new Cone3D(center, 1, 2, 20, new Material(color, this,null)); break;
			case CYLINDER: thisObject = new Cylinder3D(center, 1, 2, 20, new Material(color, this,null)); break;
			case SPHERE: thisObject = new Sphere3D(center, 1, 20, 20, new Material(color, this,null)); break;
			case WEDGE: thisObject = new Wedge3D(center, 2, new Material(color, this,null)); break;
			case HANDLE: thisObject = new Handle3D(center, 1, 2, 10, new Material(color, this,null)); break;
			case PYRAMID: System.out.println("Pyramid primitive to come."); break;
			case HALFSPHERE: System.out.println("Half sphere primitive to come."); break;
			default: System.out.println("Unknown primitive type."); break;
		}
		
		thisObject.highlight = (selected == this);
		
		thisObject.transform.combine(TransformMy3D.stretch(scale[X]/100.0 + offset, scale[Y]/100.0 + offset, scale[Z]/100.0 + offset));
		p.children.add(thisObject);
		
		// Add all of your children to the universe.
		for(ObjectAttachment child : children) {
			
			NullObject3D newPerspective = new NullObject3D();
			p.children.add(newPerspective);
			
			// First transform to be on the contact point for the initial object.
			TransformMy3D toInitial = myTranslation(child.parentAttachmentPoint, scale,false);
			newPerspective.transform.combine(toInitial);
			
			// Then rotate into the objects space.
			TransformMy3D rotate = myMakeRotationTransform(child.child.rotation);
			newPerspective.transform.combine(rotate);
			
			// Finally transform to be on the contact point for the second object.
			TransformMy3D toSecond = myTranslation(child.childAttachmentPoint, child.child.scale,true);
			newPerspective.transform.combine(toSecond);
			
			// Apply attachment transformations.
			child.child.addToWorld(newPerspective, offset + .001, selected);
			
		}
	}
	
	/**
	 * Create a rotation transform from an array of ints.
	 * @param rotation
	 * @return
	 */
	public TransformMy3D myMakeRotationTransform(int[] rotation) {
		double x = rotation[X] * Math.PI / 180;
		double y = rotation[Y] * Math.PI / 180;
		double z = rotation[Z] * Math.PI / 180;
		TransformMy3D rotate = TransformMy3D.rotateX(x);
		rotate.combine(TransformMy3D.rotateY(y));
		rotate.combine(TransformMy3D.rotateZ(z));
		return rotate;
	}

	/**
	 * Given an attachment scheme and the scale of the object, create a translation vector that will attach to the given point.
	 * @param attachments Attachment scheme.
	 * @param scale Scale of the object.
	 * @return Translation.
	 */
	public TransformMy3D myTranslation(short[] attachments, int[] scale, boolean invert) {		
		double mult = 1;
		if(invert) {
			mult = -1;
		}
		
		double x,y,z;		
		x = mult * (attachments[0]) * scale[X] / 100.0;
		y = mult * (attachments[1]) * scale[Y] / 100.0;
		z = mult * (attachments[2]) * scale[Z] / 100.0;
		
		return TransformMy3D.translate(x, y, z);
		
	}
	
	/**
	 * Remove this object from the structure.
	 * @param child The child object to remove.
	 */
	public void remove(PrimitiveInstance3DTree child) {
		
		// First find this child node.
		ObjectAttachment oa = findChild(child);
		
		// If it exists then remove it from your child list.
		if(oa != null) {
			children.remove(oa);
		}
	}
	
	/**
	 * Find which one of your object attachments contains this particular child.
	 * @param child	Child to find.
	 * @return ObjectAttachment that attaches you to this child.
	 */
	public ObjectAttachment findChild(PrimitiveInstance3DTree child) {
		
		// Look through all attachments.
		for(ObjectAttachment oa : this.children) {
			
			// If this attachment contains your child then return it.
			if(oa.child == child) {
				return oa;
			}
		}
		return null;
	}

	/**
	 * This is what will be rendered in the tree.
	 */
	public String toString() {
		return Primitive3D.shapes.get(type).name;
	}
	
	/**
	 * The default model.
	 * @return
	 */
	public static PrimitiveInstance3DTree defaultModel() {
		return new PrimitiveInstance3DTree();
	}
	
	public PrimitiveInstance3DTree() {
		type = CUBE;
		scale = new int[3];
		rotation = new int[3];
		scale[X] = 100;
		scale[Y] = 100;
		scale[Z] = 100;
		rotation[X] = 0;
		rotation[Y] = 0;
		rotation[Z] = 0;
		color = Color.WHITE;
	}

	public PrimitiveInstance getClicked(int x, int y) {
		return null;
	}

	@Override
	public void addChild(PrimitiveInstance obj) {
		// TODO Auto-generated method stub
		
	}
}