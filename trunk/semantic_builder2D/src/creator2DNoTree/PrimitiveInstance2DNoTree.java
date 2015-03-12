package creator2DNoTree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

import modelTools.Primitive2D;
import modelTools.PrimitiveInstance;


/**
 * An object in the model.
 * @author bkievitk
 *
 */
public class PrimitiveInstance2DNoTree extends PrimitiveInstance {

	private static final long serialVersionUID = 7303124573894921998L;

	public int[] translate = new int[2];
	
	public Color color = Color.RED;		
	public Primitive2D shape = null;	
	public Polygon pInWorld = null;
			
	public PrimitiveInstance cloneRecursive() {
		return new PrimitiveInstance2DNoTree(rotation[0], translate[0], translate[1], scale[0], scale[1], shape);
	}
	
	public int countObjects() {
		return 1;
	}
	
	public PrimitiveInstance2DNoTree getClicked(int x, int y) {
		if(pInWorld == null || !pInWorld.contains(x, y)) {
			return null;
		}
		
		return this;
	}
	
	public void addChild(PrimitiveInstance obj) {
	}
	
	public PrimitiveInstance2DNoTree(int rotation, int dx, int dy, int scaleX, int scaleY, Primitive2D shape) {
		this.rotation = new int[1];
		this.scale = new int[2];
		
		this.rotation[0] = rotation;
		translate[0] = dx;
		translate[1] = dy;
		scale[0] = scaleX;
		scale[1] = scaleY;
		this.shape = shape;
		buildTransform();
	}
	
	public PrimitiveInstance2DNoTree(Primitive2D shape, int dx, int dy) {
		this.rotation = new int[1];
		this.scale = new int[2];
		
		rotation[0] = 0;
		translate[0] = dx;
		translate[1] = dy;
		scale[0] = 100;
		scale[1] = 100;
		this.shape = shape;
		buildTransform();
	}
	
	public PrimitiveInstance2DNoTree(Primitive2D shape) {
		this.rotation = new int[1];
		this.scale = new int[2];
		
		rotation[0] = 0;
		translate[0] = 0;
		translate[1] = 0;
		scale[0] = 100;
		scale[1] = 100;
		this.shape = shape;
		buildTransform();
	}
		
	public AffineTransform myTranslation(short[] attachments, float[] scale) {	
		AffineTransform transform = new AffineTransform();
		transform.translate(attachments[0] * scale[0], attachments[1] * scale[1]);
		return transform;
	}
	
	public void render(Graphics g, PrimitiveInstance2DNoTree selected) {

		AffineTransform transform = new AffineTransform();
		transform.translate(translate[0], translate[1]);
		transform.rotate(rotation[0]/100.0);
		transform.scale(scale[0]/100.0*20, scale[1]/100.0*20);
		
		pInWorld = transformPolygon(shape.shape,transform);
		
		g.setColor(color);
		g.fillPolygon(pInWorld);
		
		if(this == selected) {
			
			// Choose a dark color for a light item.
			if((color.getRed() + color.getGreen() + color.getBlue())/3 > 255/2) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawPolygon(pInWorld);
		}
	}
	
	public static Polygon transformPolygon(Vector<Point2D.Double> p, AffineTransform t) {
		
		Polygon ret = new Polygon();
		for(int i=0;i<p.size();i++) {			
			Point2D pointOut = t.transform(p.get(i), null);
			ret.addPoint((int)pointOut.getX(), (int)pointOut.getY());
		}
		return ret;
	}
	
	public AffineTransform buildTransform() {
		AffineTransform transform = new AffineTransform();
		transform.translate(translate[0], translate[1]);
		transform.scale(scale[0]/100.0, scale[1]/100.0);
		transform.rotate(rotation[0]/100.0);
		return transform;
	}
}
