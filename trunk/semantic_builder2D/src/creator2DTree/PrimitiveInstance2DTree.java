package creator2DTree;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animals.Animal;
import animals.functional.FunctionalManager;
import animals.functional.FunctionalUnit;

import modelTools.Primitive2D;
import modelTools.PrimitiveInstance;

import templates.WindowRender;
import tools.MatchPoint;


/**
 * This is a node in the model of a 2D geon tree.
 * @author bkievitk
 */

public class PrimitiveInstance2DTree extends PrimitiveInstance {

	private static final long serialVersionUID = 252931148659986143L;
	
	// Parent and children.
	public PrimitiveInstance2DConnection parent = null;
	public Vector<PrimitiveInstance2DConnection> children = new Vector<PrimitiveInstance2DConnection>();

	// Shape.
	public Primitive2D shape = null;

	// These are calculated for each rendering.
	public Polygon pInWorld = null;
	public AffineTransform transform;
	public Point2D[][] controlPoints = new Point2D[3][3];

	public FunctionalUnit functional;

	
	/**
	 * Convert the node into a formatted string.
	 * @param node
	 * @return
	 */
	private String toStringNonRec() {
		String representation = "";
		representation += color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ",";
		representation += rotation[0] + ",";
		representation += scale[0] + "," + scale[1] + ",";
		representation += shape.id + ",";
		representation += editable;
		
		// Add functional component if there.
		if(functional != null) {
			representation += "," + functional.encode();
		}
		
		return representation;
	}
	
	public String toReduced() { 
		String representation = "";

		representation += "[";
		representation += toStringNonRec();
		
		if(parent != null) {
			representation += "," + parent.childAttachmentPoint[0] + "," + parent.childAttachmentPoint[1] + ",";
			representation += parent.parentAttachmentPoint[0] + "," + parent.parentAttachmentPoint[1];
		}
		
		for(PrimitiveInstance2DConnection connection : children) {
			representation += connection.child.toReduced();
		}
		representation += "]";

		return representation; 
	}
		
	public static PrimitiveInstance2DTree fromReduced(String reduced, Animal animal) {
		PrimitiveInstance2DTree root = new PrimitiveInstance2DTree(null);
		fromReduced(reduced,0,null,root, animal);
		return root;
	}
	
	private static int fromReduced(String reduced, int start, PrimitiveInstance2DTree parent, PrimitiveInstance2DTree root, Animal animal) {

		int index = reduced.indexOf('[', start);
		
		if(index < 0) {
			return -1;
		}

		// Find end index.
		int endIndex = index+1;
		while(true) {
			if(endIndex >= reduced.length()) {
				return -1;
			}
			if(reduced.charAt(endIndex) == '[' || reduced.charAt(endIndex) == ']') {
				break;
			}
			endIndex++;
		}
		
		String dataSegment = reduced.substring(index+1,endIndex);
		String[] properties = dataSegment.split(",");
		
		int byteID = 0;
		Color color = new Color(Integer.parseInt(properties[byteID++]),Integer.parseInt(properties[byteID++]),Integer.parseInt(properties[byteID++]));
		int rotation = Integer.parseInt(properties[byteID++]);
		int scaleX = Integer.parseInt(properties[byteID++]);
		int scaleY = Integer.parseInt(properties[byteID++]);
		Primitive2D shape = Primitive2D.getByID(Integer.parseInt(properties[byteID++]));
		
		PrimitiveInstance2DTree instance;
		
		if(parent == null) {
			instance = root;
			
			instance.rotation[0] = rotation;
			instance.scale[0] = scaleX;
			instance.scale[1] = scaleY;
			instance.shape = shape;
			instance.color = color;
			
			// Old style.
			if(properties.length == 7) {				
			}
			
			// No functional
			else if(properties.length == 8) {
				instance.editable = Byte.parseByte(properties[byteID++]);
			}

			// Functional
			else {
				instance.editable = Byte.parseByte(properties[byteID++]);
				instance.functional = FunctionalManager.decode(properties[byteID++],instance,animal);			
			}

		} else {
			instance = new PrimitiveInstance2DTree(rotation, scaleX, scaleY, shape);
			instance.color = color;
			
			// Old style.
			if(properties.length == 7 + 4) {				
			}
			
			// No functional
			else if(properties.length == 8 + 4) {
				instance.editable = Byte.parseByte(properties[byteID++]);
			}

			// Functional
			else {
				instance.editable = Byte.parseByte(properties[byteID++]);
				instance.functional = FunctionalManager.decode(properties[byteID++],instance,animal);
			}

			short[] childAttachmentPoint = {Short.parseShort(properties[byteID++]),Short.parseShort(properties[byteID++])};
			short[] parentAttachmentPoint = {Short.parseShort(properties[byteID++]),Short.parseShort(properties[byteID++])};
			PrimitiveInstance2DConnection connector = new PrimitiveInstance2DConnection(instance, parent,childAttachmentPoint, parentAttachmentPoint);
			instance.parent = connector;
			parent.children.add(connector);
		}
		
		while(reduced.charAt(endIndex) == '[') {
			endIndex = fromReduced(reduced, endIndex, instance, root,null);
		}
		
		return endIndex+1;
	}
	
	public Vector<Integer> getID() {
		Vector<Integer> id = new Vector<Integer>();
		getID(id);
		return id;
	}
	
	private void getID(Vector<Integer> id) {
		if(parent != null) {
			parent.parent.getID(id);
			
			// Figure out where you are in your parents list.
			for(int i = 0; i< parent.parent.children.size();i++) {
				if(parent.parent.children.get(i) == parent) {
					id.add(i);
					return;
				}
			}
		}
	}
	
	public PrimitiveInstance2DTree getFromID(Vector<Integer> id) {
		return getFromID(id,0);
	}
	
	private PrimitiveInstance2DTree getFromID(Vector<Integer> id, int atDepth) {
		if(atDepth == id.size()) {
			return this;
		}
		return children.get(id.get(atDepth)).child.getFromID(id,atDepth+1);
	}
	
	public PrimitiveInstance2DTree touching(PrimitiveInstance2DTree piece) {
		if(touch(piece.pInWorld,pInWorld)) {
			return this;
		}
		for(PrimitiveInstance2DConnection child : children) {
			PrimitiveInstance2DTree touch = child.child.touching(piece);
			if(touch != null) {
				return touch;
			}
		}
		return null;
	}
	
	public static boolean touch(Polygon p1, Polygon p2) {
		if(p1 == null || p2 == null) {
			return false;
		}
		
		Rectangle r1 = p1.getBounds();
		Rectangle r2 = p2.getBounds();
		if(	r1.x + r1.width > r2.x && 
			r1.x < r2.x + r2.width &&
			r1.y + r1.height > r2.y && 
			r1.y < r2.y + r2.height) {
			
			for(int i=0;i<p1.npoints;i++) {
				if(p2.contains(p1.xpoints[i],p1.ypoints[i])) {
					return true;
				}
			}
			return (p1.contains(p2.xpoints[0],p2.ypoints[0]));
		}		
		return false;
	}
	
	public BufferedImage thumbnail(BufferedImage background, int width, int height) {
		BufferedImage img;
		if(background == null) {
			img = WindowRender.makeBackground(width,height);
		} else {
			img = background;
		}
		
		Graphics g = img.getGraphics();
		
		// Create root transform to be centered and scaled by 20.
		AffineTransform rootTransform = new AffineTransform();
		rootTransform.translate(img.getWidth()/2,img.getHeight()/2);
		rootTransform.scale(5, 5);
		rootTransform.rotate(this.rotation[0]/100.0);
			
		// Render.
		render(g, rootTransform,null,false);
		
		return img;
	}
	
	/**
	 * Create a clone of this and all of its descendents.
	 * @return
	 */
	public PrimitiveInstance2DTree cloneRecursive() {

		// Create a copy.
		PrimitiveInstance2DTree clone = new PrimitiveInstance2DTree(rotation[0], scale[0],scale[1], shape);

		// Copy over simple properties.
		clone.parent = parent;
		clone.color = new Color(color.getRGB());
				
		// Copy all of the children.
		for(PrimitiveInstance2DConnection oa : children) {
			PrimitiveInstance2DConnection newCon = oa.cloneRecursive();
			newCon.child.parent = newCon;
			clone.children.add(newCon);
		}
		
		return clone;
	}

	/**
	 * Find the best point to match this moved item to.
	 * @param goalObject
	 * @param controlPoints
	 * @return
	 */
	public MatchPoint findBestMatchPoint(PrimitiveInstance2DTree goalObject, Point2D[][] controlPoints) {
		MatchPoint best = null;
		
		// Do not try the goal object or its descendants.
		if(this != goalObject) {
			
			// For every possible parent and child connection.
			for(int xp=-1;xp<=1;xp++) {
				for(int yp=-1;yp<=1;yp++) {
					for(int xc=-1;xc<=1;xc++) {
						for(int yc=-1;yc<=1;yc++) {
							
							// See how good this connection would be.
							short[] childAttachmentPoint = {(short)xc,(short)yc};
							short[] parentAttachmentPoint = {(short)xp,(short)yp};
							MatchPoint mp = new MatchPoint(this, controlPoints, childAttachmentPoint, parentAttachmentPoint);

							// Keep if this is the best so far.
							if(best == null || mp.distance < best.distance) {
								best = mp;
							}
						}
					}
				}
			}

			// See if one of your children has a better match.
			for(PrimitiveInstance2DConnection childConn : children) {
				
				// No match for goal or children of goal.
				if(childConn.child != goalObject) {
					MatchPoint mp = childConn.child.findBestMatchPoint(goalObject, controlPoints);
					if(best == null || mp.distance < best.distance) {
						best = mp;
					}
				}
			}
		}
		
		return best;
	}

	/**
	 * Count the number of objects below this and this one.
	 * @return
	 */
	public int countObjects() {
		int sum = 0;
		for(PrimitiveInstance2DConnection child : children) {
			sum += child.child.countObjects();
		}
		return sum + 1;
	}

	/**
	 * Find out which object was clicked on.
	 * @param x
	 * @param y
	 * @return
	 */
	public PrimitiveInstance2DTree getClicked(int x, int y) {
		if(pInWorld == null) {
			return null;
		}

		// Try children first.
		for(PrimitiveInstance2DConnection connect : children) {
			PrimitiveInstance2DTree clicked = connect.child.getClicked(x, y);
			if(clicked != null) {
				return clicked;
			}
		}
		
		// If you haven't found one then try yourself.
		// This way the root objects is the deapest.
		if(pInWorld.contains(x, y)) {
			return this;
		}
		
		
		return null;
	}
	
	public PrimitiveInstance2DTree(int rotation, int scaleX, int scaleY, Primitive2D shape) {
		this.rotation = new int[1];
		this.scale = new int[2];
		
		this.rotation[0] = rotation;
		scale[0] = scaleX;
		scale[1] = scaleY;
		this.shape = shape;
		buildTransform();
	}
	
	public PrimitiveInstance2DTree(int rotation, int[] scale, Primitive2D shape) {
		this.rotation = new int[1];
		this.scale = new int[2];

		this.rotation[0] = rotation;
		this.scale[0] = scale[0];
		this.scale[1] = scale[1];
		this.shape = shape;
		buildTransform();
	}
	
	public PrimitiveInstance2DTree(Primitive2D shape) {
		this.rotation = new int[1];
		this.scale = new int[2];

		rotation[0] = 0;
		scale[0] = 100;
		scale[1] = 100;
		this.shape = shape;
		buildTransform();
	}

	public void addChild(PrimitiveInstance obj) {
		if(obj instanceof PrimitiveInstance2DTree) {
			PrimitiveInstance2DTree obj1 = (PrimitiveInstance2DTree)obj;
			PrimitiveInstance2DConnection connection = new PrimitiveInstance2DConnection(obj1,this);
			children.add(connection);
			obj1.parent = connection;
		}
	}
	
	public AffineTransform myTranslation(short[] attachments, float[] scale) {	
		AffineTransform transform = new AffineTransform();
		transform.translate(attachments[0] * scale[0], attachments[1] * scale[1]);
		return transform;
	}
	
	public void renderControlPoints(Graphics g) {
		g.setColor(Color.RED);
		for(Point2D[] pts : controlPoints) {
			for(Point2D pt : pts) {
				g.fillOval((int)pt.getX()-2,(int)pt.getY()-2,4,4);
			}
		}
		for(PrimitiveInstance2DConnection child : children) {
			child.child.renderControlPoints(g);
		}
		
	}

	/**
	 * Render from this node down using px and py as offsets.
	 * @param g
	 * @param px
	 * @param py
	 */
	public void renderChunk(Graphics g, int px, int py) {

		AffineTransform draggedTransform = new AffineTransform();
		double tx = transform.getTranslateX();
		double ty = transform.getTranslateY();
		draggedTransform.translate(px-tx, py-ty);
		draggedTransform.concatenate(transform);

		// Fade the color.
		Color faded = new Color(color.getRed(),color.getGreen(),color.getBlue(),100);
		g.setColor(faded);
		g.fillPolygon(Primitive2D.transformPolygon(shape.shape,draggedTransform));

		// Render all of your children faded as well.
		for(PrimitiveInstance2DConnection child : children) {
			AffineTransform newSpace = (AffineTransform)draggedTransform.clone();
			
			// We need to undo our scaling.
			newSpace.scale(1/(scale[0]/100.0), 1/(scale[1]/100.0));			
			
			newSpace.translate(child.parentAttachmentPoint[0] * (scale[0]/100.0), child.parentAttachmentPoint[1] * (scale[1]/100.0));
			newSpace.translate(-child.childAttachmentPoint[0] * (child.child.scale[0]/100.0), -child.childAttachmentPoint[1] * (child.child.scale[1]/100.0));
			newSpace.rotate(child.child.rotation[0]);
						
			child.child.renderChunk(g, newSpace);
		}
	}

	private void renderChunk(Graphics g, AffineTransform parentSpace) {

		transform = (AffineTransform)parentSpace.clone();
		transform.scale(scale[0]/100.0, scale[1]/100.0);
				
		Color faded = new Color(color.getRed(),color.getGreen(),color.getBlue(),100);
		g.setColor(faded);
		
		g.fillPolygon(Primitive2D.transformPolygon(shape.shape,transform));
		
		for(PrimitiveInstance2DConnection child : children) {
			AffineTransform newSpace = (AffineTransform)parentSpace.clone();
			
			newSpace.translate(child.parentAttachmentPoint[0] * (scale[0]/100.0), child.parentAttachmentPoint[1] * (scale[1]/100.0));
			newSpace.translate(-child.childAttachmentPoint[0] * (child.child.scale[0]/100.0), -child.childAttachmentPoint[1] * (child.child.scale[1]/100.0));
			newSpace.rotate((child.child.rotation[0]/100.0));
						
			child.child.renderChunk(g, newSpace);
		}
	}

	/**
	 * Get the bounding points for this object if it were to be moved to px, py.
	 * @param px
	 * @param py
	 * @return
	 */
	public Point2D[][] getControlPoints(int px, int py) {
		AffineTransform draggedTransform = new AffineTransform();		
		
		double tx = transform.getTranslateX();
		double ty = transform.getTranslateY();
		draggedTransform.translate(px-tx, py-ty);
		draggedTransform.concatenate(transform);
		
		Point2D[][] controlPoints = new Point2D[3][3];
		
		for(int x=-1;x<=1;x++) {
			for(int y=-1;y<=1;y++) {
				controlPoints[x+1][y+1] = draggedTransform.transform(new Point(x,y), null);
			}
		}
		
		return controlPoints;
	}
	
	public void renderFunctionalLabels(Graphics g) {
		if(functional != null) {
			Rectangle bounds = pInWorld.getBounds();			
			g.drawString(functional.toString(), bounds.x+bounds.width/2, bounds.y+bounds.height/2);
		}

		for(PrimitiveInstance2DConnection child : children) {
			child.child.renderFunctionalLabels(g);
		}
	}
	
	/**
	 * Render this object and all of it's children.
	 * @param g
	 * @param parentSpace
	 * @param selected
	 * @param dragged
	 */
	public void render(Graphics g, AffineTransform parentSpace, PrimitiveInstance2DTree selected, boolean dragged) {

		AffineTransform objSpace = (AffineTransform)parentSpace.clone();
		//objSpace.rotate(rotation/100.0);
		
		
		// If this item is being dragged, it will be handled by the renderChunk function.
		if(this == selected && dragged) {
			return;
		}
		
		transform = (AffineTransform)objSpace.clone();
		transform.scale(scale[0]/100.0, scale[1]/100.0);
		
		pInWorld = Primitive2D.transformPolygon(shape.shape,transform);

		// Calculate control points.
		for(int x=-1;x<=1;x++) {
			for(int y=-1;y<=1;y++) {
				controlPoints[x+1][y+1] = transform.transform(new Point(x,y), null);
			}
		}

		// Render yourself.
		g.setColor(color);
		g.fillPolygon(pInWorld);
		
		// Render the selected item with a boarder.
		if(this == selected) {
			
			// Choose a dark color for a light item.
			if((color.getRed() + color.getGreen() + color.getBlue())/3 > 255/2) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawPolygon(pInWorld);
		}

		// Render all children.
		for(PrimitiveInstance2DConnection child : children) {
			AffineTransform newSpace = (AffineTransform)objSpace.clone();
			newSpace.translate(child.parentAttachmentPoint[0] * (scale[0]/100.0), child.parentAttachmentPoint[1] * (scale[1]/100.0));
			double dx = child.childAttachmentPoint[0] * (child.child.scale[0] / 100.0);
			double dy = child.childAttachmentPoint[1] * (child.child.scale[1] / 100.0);
			newSpace.translate(-dx,-dy);
			newSpace.rotate(child.child.rotation[0] / 100.0,dx,dy);						
			child.child.render(g, newSpace, selected, dragged);
		}
	}
	

	/**
	 * Build your tranform based on your rotation and scale.
	 * @return
	 */
	public AffineTransform buildTransform() {
		AffineTransform transform = new AffineTransform();
		transform.scale(scale[0], scale[1]);
		transform.rotate(rotation[0]);
		return transform;
	}

}
