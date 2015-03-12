package modelTools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;

import tools.MyGrayFilter;


/**
 * This defines each of the primitive geon types for the 2D models.
 * @author bkievitk
 */

public class Primitive2D extends PrimitiveBase implements Serializable {
	
	private static final long serialVersionUID = -1147227430815188509L;
	
	// Keep a list of all primitives.
	public static final Hashtable<String,Primitive2D> shapes = buildShapes();
	public static final Vector<Primitive2D> shapesVec = valsFromHash(shapes);
		
	// Id for each one.
	public int id;
	
	// This is the actual shape of the geon.
	// This will later be scalled, translated and rotated.
	public Vector<Point2D.Double> shape;
	
	// Create an image and a grayed out image.
	public ImageIcon img;
	public ImageIcon disabled;
	
	// Define a name.
	public String name;
	
	public Polygon polygon;
	
	// Track IDs so each can have a unique one.
	public static int idOn = 0;
	
	public static Vector<Primitive2D> valsFromHash(Hashtable<String,Primitive2D> h) {
		Vector<Primitive2D> ret = new Vector<Primitive2D>();
		for(Primitive2D p : h.values()) {
			ret.add(p);
		}
		return ret;
	}
		
	public boolean equals(Object o) {
		if(o instanceof Primitive2D) {
			return ((Primitive2D)o).id == id;
		}
		return false;
	}
	
	public int hashCode() {
		return id;
	}
	
	public static Primitive2D getByID(int id) {
		for(Primitive2D prim : shapes.values()) {
			if(prim.id == id) {
				return prim;
			}
		}
		return null;
	}
	
	public Primitive2D(Vector<Point2D.Double> shape, String name){
		
		// Set id.
		id = idOn;
		idOn++;
		
		// Set values.
		this.shape = shape;
		this.name = name;
		
		// Get image by rendering the shape.
		ImageIcon img = render(shape);
		this.img = img;
		
		// Apply gray filter.
		disabled = MyGrayFilter.getDisabledIcon(null, img);
	}
	
	private ImageIcon render(Vector<Point2D.Double> shape) {

		// For rendering, use a back background of 50 by 50 pixels.
		BufferedImage background = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
		Graphics g = background.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,50,50);

		// Create a transform to move it to the center.
		AffineTransform t = new AffineTransform();
		t.translate(background.getWidth()/2, background.getHeight()/2);
		t.scale(20, 20);
		
		// Render it as gray.
		g.setColor(Color.GRAY);
		g.fillPolygon(transformPolygon(shape, t));
		
		return new ImageIcon(background);
	}

	/**
	 * This takes a set of points and a transform and applys the transform.
	 * @param p
	 * @param t
	 * @return
	 */
	public static Polygon transformPolygon(Vector<Point2D.Double> p, AffineTransform t) {
		Polygon ret = new Polygon();
		for(int i=0;i<p.size();i++) {			
			Point2D pointOut = t.transform(p.get(i), null);
			ret.addPoint((int)pointOut.getX(), (int)pointOut.getY());
		}
		return ret;
	}

	/**
	 * This function sets up all of the default geons.
	 * @return
	 */
	public static Hashtable<String,Primitive2D> buildShapes() {
		Hashtable<String,Primitive2D> shapes = new Hashtable<String,Primitive2D>();

		// Create  a square.
		Vector<Point2D.Double> squareShape = new Vector<Point2D.Double>();
		squareShape.add(new Point2D.Double(-1, 1));
		squareShape.add(new Point2D.Double(-1, -1));
		squareShape.add(new Point2D.Double(1, -1));
		squareShape.add(new Point2D.Double(1, 1));
		shapes.put("square",new Primitive2D(squareShape,"square"));

		// Create a triangle.
		Vector<Point2D.Double> triangleShape = new Vector<Point2D.Double>();
		triangleShape.add(new Point2D.Double(-1, 1));
		triangleShape.add(new Point2D.Double(1, 1));
		triangleShape.add(new Point2D.Double(0, -1));
		shapes.put("triangle",new Primitive2D(triangleShape,"triangle"));

		// Create a hexagon.
		Vector<Point2D.Double> hexagonShape = new Vector<Point2D.Double>();
		for(int i=0;i<6;i++) {
			hexagonShape.add(new Point2D.Double(Math.sin(Math.PI*2*i/6), Math.cos(Math.PI*2*i/6)));
		}
		shapes.put("hexagon",new Primitive2D(hexagonShape,"hexagon"));

		// Create an octagon.
		Vector<Point2D.Double> octagonShape = new Vector<Point2D.Double>();
		for(int i=0;i<8;i++) {
			octagonShape.add(new Point2D.Double(Math.sin(Math.PI*2*i/8), Math.cos(Math.PI*2*i/8)));
		}
		shapes.put("octagon",new Primitive2D(octagonShape,"octagon"));

		// Create an approximate circle simply by using a 20 sized polygon.
		// We do this instead of a real circle so there is a constancy to the code.
		Vector<Point2D.Double> circleShape = new Vector<Point2D.Double>();
		for(int i=0;i<20;i++) {
			circleShape.add(new Point2D.Double(Math.sin(Math.PI*2*i/20), Math.cos(Math.PI*2*i/20)));
		}
		shapes.put("circle",new Primitive2D(circleShape,"circle"));

		// Create a half of a circle.
		Vector<Point2D.Double> hemiCircleShape = new Vector<Point2D.Double>();
		for(int i=0;i<=20;i++) {
			hemiCircleShape.add(new Point2D.Double(Math.sin(Math.PI*i/20)*2-1, Math.cos(Math.PI*i/20)));
		}
		shapes.put("hemicircle",new Primitive2D(hemiCircleShape,"hemicircle"));

		// Create a handle looking shape.
		Vector<Point2D.Double> handleShape = new Vector<Point2D.Double>();
		for(int i=0;i<=20;i++) {
			handleShape.add(new Point2D.Double(Math.sin(Math.PI*i/20)*2-1, Math.cos(Math.PI*i/20)));
		}
		for(int i=20;i>=0;i--) {
			handleShape.add(new Point2D.Double(Math.sin(Math.PI*i/20)*.7*2-1, Math.cos(Math.PI*i/20)*.7));
		}
		shapes.put("handle",new Primitive2D(handleShape,"handle"));
		
		return shapes;
	}
}