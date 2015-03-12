package modelTools;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import my3D.JPanel3D;
import my3D.Material;

import shapes3D.Cone3D;
import shapes3D.Cube3D;
import shapes3D.Cylinder3D;
import shapes3D.Handle3D;
import shapes3D.Sphere3D;
import shapes3D.Wedge3D;
import tools.MyGrayFilter;


public class Primitive3D extends PrimitiveBase {

	// Keep a list of all primitives.
	public static final Hashtable<String,Primitive3D> shapes = buildShapes();
	
	// Id for each one.
	public int id;
	
	// Create an image and a grayed out image.
	public ImageIcon img;
	public ImageIcon disabled;
	
	// Define a name.
	public String name;
	
	// Track IDs so each can have a unique one.
	public static int idOn = 0;

	public Primitive3D(ImageIcon img, String name){
		this.img = img;
		disabled = MyGrayFilter.getDisabledIcon(null, img);;
		this.name = name;

		// Set id.
		id = idOn;
		idOn++;
	}
	

	public boolean equals(Object o) {
		if(o instanceof Primitive3D) {
			return ((Primitive3D)o).id == id;
		}
		return false;
	}
	
	public int hashCode() {
		return id;
	}
	
	/**
	 * This function sets up all of the default geons.
	 * @return
	 */
	public static Hashtable<String,Primitive3D> buildShapes() {
		Hashtable<String,Primitive3D> shapes = new Hashtable<String,Primitive3D>();
		
		double[] zeros = {0,0,0};
		double[] ones = {1.5,1.5,1.5};
		
		Cylinder3D cylinder = new Cylinder3D(zeros, 1, 2, 20, new Material(Color.WHITE,"Cylinder", null));
		shapes.put("cylinder",new Primitive3D(new ImageIcon(JPanel3D.renderObject(cylinder)),"cylinder"));
		
		Sphere3D sphere = new Sphere3D(zeros, 1, 20, 20, new Material(Color.WHITE,"Sphere", null));
		shapes.put("sphere",new Primitive3D(new ImageIcon(JPanel3D.renderObject(sphere)),"sphere"));
		
		Wedge3D wedge = new Wedge3D(zeros, ones, new Material(Color.WHITE,"Wedge", null));
		shapes.put("wedge",new Primitive3D(new ImageIcon(JPanel3D.renderObject(wedge)),"wedge"));
		
		Cube3D cube = new Cube3D(zeros, ones, new Material(Color.WHITE,"Box", null));
		shapes.put("box",new Primitive3D(new ImageIcon(JPanel3D.renderObject(cube)),"box"));
		
		Cone3D cone = new Cone3D(zeros, 1, 1.5, 20, new Material(Color.WHITE,"Cone", null));
		shapes.put("cone",new Primitive3D(new ImageIcon(JPanel3D.renderObject(cone)),"cone"));
		
		Handle3D handle = new Handle3D(zeros, 1, 2, 10, new Material(Color.WHITE,"Handle", null));
		shapes.put("handle",new Primitive3D(new ImageIcon(JPanel3D.renderObject(handle)),"handle"));
		
		return shapes;
	}
}
