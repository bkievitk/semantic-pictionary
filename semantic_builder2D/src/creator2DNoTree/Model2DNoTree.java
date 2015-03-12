package creator2DNoTree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import modelTools.GeonModel;
import modelTools.Primitive2D;

import templates.WindowRender;


public class Model2DNoTree extends GeonModel {

	private static final long serialVersionUID = 5784721301118902864L;

	// A list of all objects.
	public Vector<PrimitiveInstance2DNoTree> objects = new Vector<PrimitiveInstance2DNoTree>();

	// Object type selected for creation.
	// By clicking on the screen, this is the object that will be added.
	public Primitive2D selectedToMake = null;

	/**
	 * A reduced object description.
	 */
	public String toReduced() {
		
		String representation = "";
		
		// Add each object info indipendently to the string.
		for(PrimitiveInstance2DNoTree object : objects) {
			representation += object.color.getRed() + "," + object.color.getGreen() + "," + object.color.getBlue() + ",";
			representation += object.rotation + ",";
			representation += object.scale[0] + "," + object.scale[1] + ",";
			representation += object.shape.id + ",";
			representation += object.translate[0] + "," + object.translate[1];
			
			// Add object seperator.
			if(object != objects.get(objects.size()-1)) {
				representation += ":";
			}
		}
		return representation; 
	}
	
	/**
	 * Build this model from a reduced representation.
	 */
	public void fromReduced(String reduced) {
		// First, clear what you have.
		objects.clear();

		// Split into objects.
		String[] primitives = reduced.split(":");
		for(String primitive : primitives) {
			
			// Split into properties.
			String[] properties = primitive.split(",");
			
			Color color = new Color(Integer.parseInt(properties[0]),Integer.parseInt(properties[1]),Integer.parseInt(properties[2]));
			int rotation = Integer.parseInt(properties[3]);
			int scaleX = Integer.parseInt(properties[4]);
			int scaleY = Integer.parseInt(properties[5]);
			Primitive2D shape = Primitive2D.getByID(Integer.parseInt(properties[6]));
			int dx = Integer.parseInt(properties[7]);
			int dy = Integer.parseInt(properties[8]);

			// Create parsed object.
			PrimitiveInstance2DNoTree object = new PrimitiveInstance2DNoTree(rotation, dx, dy, scaleX, scaleY, shape);
			object.color = color;
			objects.add(object);
		}
	}
	
	/**
	 * An XML parsing.
	 */
	public String toXML() { 
		String representation = "";
		for(PrimitiveInstance2DNoTree object : objects) {
			representation = "<object";
			representation += object.color.getRed() + "," + object.color.getGreen() + "," + object.color.getBlue() + ",";
			representation += object.rotation + ",";
			representation += object.scale[0] + "," + object.scale[1] + ",";
			representation += object.shape.id + ",";
			representation += object.translate[0] + "," + object.translate[1];
		}
		
		return representation;
	};
	
	public int countObjects() {
		return objects.size();
	}
	
	public PrimitiveInstance2DNoTree getSelected() {
		// Force the getSelected into the correct instance type.
		return (PrimitiveInstance2DNoTree)selected;
	}
	
	public void clear() {
		super.clear();
		objects.clear();
	}

	public BufferedImage thumbnail(BufferedImage background, int width, int height, double scale) {
		BufferedImage img;
		if(background == null) {
			img = WindowRender.makeBackground(width,height);
		} else {
			img = background;
		}
		
		Graphics g = img.getGraphics();
		
		for(PrimitiveInstance2DNoTree object : objects) {
			object.render(g, getSelected());
		}
		
		return img;
	}
}
