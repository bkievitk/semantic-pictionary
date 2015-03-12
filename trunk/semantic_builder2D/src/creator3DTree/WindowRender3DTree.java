package creator3DTree;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import shapes3D.Cone3D;
import shapes3D.Cylinder3D;
import shapes3D.NullObject3D;

import my3D.JPanel3DChangeObject;
import my3D.Material;
import my3D.Object3D;
import my3D.TransformMy3D;

import creator3DTree.Model3DTree;
import creator3DTree.PrimitiveInstance3DTree;


/**
 * This is a JPanel component that renders 3D worlds.
 * Also shows coordinates and allows rotation of object in view.
 * @author bkievitk
 */

public class WindowRender3DTree extends JPanel3DChangeObject implements ChangeListener {

	private static final long serialVersionUID = -2069445145560863168L;

	// Need for the model.
	public Model3DTree model;
	
	// If true, then show the 3 main coordinate systems.
	public boolean showCoordinates = true;
	public boolean throwObject = true;

	/**
	 * Build a simple default model to demonstrate.
	 * @param args
	 */
	public static void main(String[] args) {
		Model3DTree model = new Model3DTree();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		frame.add(new WindowRender3DTree(model));
		frame.setVisible(true);
	}
	
	/**
	 * Create with a given 3D model.
	 * @param model
	 */
	public WindowRender3DTree(Model3DTree model) {
	    super(model);
	    
	    this.model = model;
	    
	    // Build the model.
		rebuild(null);

		// Register with the model.
	    model.addUpdateListener(this);
	}

	/**
	 * Handle clicks.
	 * Look up object in the object buffer.
	 * If a new object is selected then set it as selected.
	 */
	public void mouseClicked(MouseEvent arg0) {
		if(u.objBuffer != null) {
			Object clicked = u.objBuffer[arg0.getX()][arg0.getY()];
			if(clicked != null) {
				model.setSelected((PrimitiveInstance3DTree)clicked);
			}
		}
	}
	
	/**
	 * Build the 3D world from the ground up.
	 * @param selected The selected object will be specially rendered.
	 */
	public void rebuild(PrimitiveInstance3DTree selected) {
		
		u.root = new NullObject3D();
		
		// Add coordinates if applicable.
		if(showCoordinates) {
			makeCoordinates(u.root);
		}
		
		// Ask the model to build a 3D version.
		model.buildMy3DWorld(u.root);
		
		repaint();
	}
	
	/**
	 * Create the coordinate indicators.
	 */
	public static void makeCoordinates(Object3D object) {
		
		double length = 2;
		double[] center = {0,0,-length/2};
		double[] offset = {0,0,-length};

		// X
		NullObject3D p1 = new NullObject3D();
		p1.children.add(new Cone3D(offset, .2, .4, 10, new Material(Color.BLUE, null, null)));
		p1.children.add(new Cylinder3D(center, .1, length, 10, new Material(Color.BLUE, null, null)));
		p1.transform = TransformMy3D.rotateX(-Math.PI / 2);
		object.children.add(p1);

		// Y
		NullObject3D p2 = new NullObject3D();
		p2.children.add(new Cone3D(offset, .2, .4, 10, new Material(Color.GREEN, null, null)));
		p2.children.add(new Cylinder3D(center, .1, length, 10, new Material(Color.GREEN, null, null)));
		p2.transform = TransformMy3D.rotateY(Math.PI / 2);
		object.children.add(p2);

		// Z
		NullObject3D p3 = new NullObject3D();
		p3.children.add(new Cone3D(offset, .2, .4, 10, new Material(Color.RED, null, null)));
		p3.children.add(new Cylinder3D(center, .1, length, 10, new Material(Color.RED, null, null)));		
		p3.transform = TransformMy3D.rotateZ(Math.PI / 2);
		
		object.children.add(p3);
	}

	/**
	 * This listens to state changes.
	 * When the model state has changed, we must rebuild the model and re-render it.
	 */
	public void stateChanged(ChangeEvent arg0) {	
		rebuild(null);
	}
}
