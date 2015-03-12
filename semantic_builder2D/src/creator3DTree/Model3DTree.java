package creator3DTree;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import modelTools.GeonModel;
import my3D.Object3D;
import my3D.TransformMy3D;
import my3D.Universe;

import shapes3D.NullObject3D;
import templates.WindowRender;

import action.tree3D.Model3DAction;
import action.tree3D.Model3DActionMove;


/**
 * This holds the root node of the model.
 * Provides accessors to modify the model.
 * You can ask other windows to listen for changes to the model.
 * @author brentkk
 *
 */
public class Model3DTree extends GeonModel {
	
	private static final long serialVersionUID = 4360538306814937422L;
	
	public int id;
	public int wordID;
	public String word;
	
	// Track actions.
	private LinkedList<Model3DAction> performed = new LinkedList<Model3DAction>();
	private LinkedList<Model3DAction> undone = new LinkedList<Model3DAction>();
	public boolean highQuality = true;
	
	// Root object of the drawing.
	public PrimitiveInstance3DTree root;
	
	// Object currently selected.
	private PrimitiveInstance3DTree selected = null;
	public PrimitiveInstance3DTree selectedForMove = null;
	
	// Listeners.
	private Vector<ChangeListener> updateListeners = new Vector<ChangeListener>();
	private Vector<ChangeListener> selectListeners = new Vector<ChangeListener>();
	
	public static Random rand = new Random();
	
	public Model3DTree() {
		// Start with a basic object.
		root = PrimitiveInstance3DTree.defaultModel();
	}
	
	public void clear() {
		System.out.println("Clearing");
		performed.clear();
		undone.clear();
		selected = null;
		selectedForMove = null;
		root = PrimitiveInstance3DTree.defaultModel();
		updateModel();
	}
	
	public void setRoot(Model3DTree copy) {		
		root = copy.root.cloneRecursive(null);
		selected = null;
		selectedForMove = null;
		performed.clear();
		undone.clear();
		updateModel();
	}
	
	public void setRoot(PrimitiveInstance3DTree object) {
		root = object;
		selected = null;
		selectedForMove = null;
		performed.clear();
		undone.clear();
		updateModel();
	}	

	/**
	 * Count how many primitive objects are in the scene.
	 * @return Number of primitive objects.
	 */
	public int countObjects() {
		return root.countObjects();
	}

	/**
	 * Perform the given action on the model.
	 * @param action Action to perform.
	 */
	public void performAction(Model3DAction action) {
		if(!action.error) {
			
			// If you have done an action before.
			if(performed.size() > 0) {
				
				// See if you can combine this action with the last one.
				Model3DAction combined = performed.getLast().combine(action);
				
				// If you could.
				if(combined != null) {
					performed.removeLast();
					action = combined;
				}
			}
			
			// You can no longer redo any more.
			undone.clear();
			
			// Perform action.
			action.performAction(this);
			
			// Add to the queue.
			performed.addLast(action);
			
			// Changed.
			updateModel();
			selectedForMove = null;
		}
	}
	
	/**
	 * Undo the last action.
	 */
	public void undo() {
		
		// Only if there is an action to undo.
		if(performed.size() > 0) {
			
			// Get action and undo it.
			Model3DAction action = performed.removeLast();
			action.undoAction(this);
			
			// Add to the undone stack so you can redo it if needed.
			undone.addLast(action);
			updateModel();
			selectedForMove = null;
		}
	}
	
	/**
	 * Redo an undone action.
	 */
	public void redo() {
		
		// Only if there is an action to redo.
		if(undone.size() > 0) {
			Model3DAction action = undone.removeLast();
			action.performAction(this);
			performed.addLast(action);
			updateModel();
			selectedForMove = null;
		}
	}

	/**
	 * Fired when something informs us that the model has been updated.
	 */
	public void updateModel() {
		ChangeEvent event = new ChangeEvent(this);
		for(ChangeListener c : updateListeners) {
			c.stateChanged(event);
		}
	}

	/**
	 * Fired when a new object is selected or no object is selected.
	 */
	private void selectObject() {
		ChangeEvent event = new ChangeEvent(this);
		for(ChangeListener c : selectListeners) {
			c.stateChanged(event);
		}
	}
	
	/**
	 * Add listener.
	 * @param listener Object wishing to receive updates when the model changes.
	 */
	public void addUpdateListener(ChangeListener listener) {
		updateListeners.add(listener);
	}
	
	/**
	 * Add listener.
	 * @param listener Object wishing to receive updates when the selected object changes.
	 */
	public void addSelectListener(ChangeListener listener) {
		selectListeners.add(listener);		
	}
		
	public void setSelected() {
		int id = rand.nextInt(countObjects()); 
		setSelected(id);
	}
	
	public int setSelected(int id) {
		return setSelected(id,0,root);
	}
	
	public int setSelected(int id, int currentId, PrimitiveInstance3DTree current) {
		if(id == currentId) {
			selected = current;
			return -1;
		}
		
		for(ObjectAttachment a : current.children) {
			currentId = setSelected(id,currentId+1,a.child);
			if(currentId < 0) {
				return -1;
			}
		}
		
		return currentId;
	}
	
	/**
	 * Set this object to be your selected object.
	 * @param object Selected object.
	 */
	public void setSelected(PrimitiveInstance3DTree object) {
		
		if(object == null) {
			System.out.println("Error");
			return;
		}

		if(selectedForMove != null) {
			// You have an item selected to be moved. Now move it.
			
			Model3DActionMove action = new Model3DActionMove(selectedForMove,object);
			performAction(action);
			
			selectedForMove = null;		
		} else {
	
			// See if you have re-selected the same object.
			if(selected == object) {
				// If this is a re-selection then un-select it.
				selected = null;
			} else {
				// If this is a new selection, set it and then inform the object.
				selected = object;
			}
			
			// Inform listeners of change.
			selectObject();
		}
		
		updateModel();
	}
	
	/**
	 * Get the object currently selected.
	 * @return Selected object.
	 */
	public PrimitiveInstance3DTree getSelected() {
		return selected;
	}

	/**
	 * Build this tree up from the given root node.
	 * @param rootNode
	 */
	public void buildFromRoot(DefaultMutableTreeNode rootNode) {
		root.addToTree(rootNode);
	}
	
	/**
	 * Ask to build a new Java3D model based on this model.
	 * @param universe Universe to build model in.
	 * @param currentTransform Transform to build model starting point.
	 */
	public void buildMy3DWorld(Object3D p) {
		root.addToWorld(p,selected);
	}
	
	/**
	 * Convert to XML format.
	 */
	//public String toXML() {
	//	return "<?xml version=\"1.0\" encoding='UTF-8'?>\n" + ObjectXML.toXML(root);
	//}
	
	/**
	 * Convert to XML format.
	 */
	public String toEnglish(String name) {
		return "A " + name + " is made up of a " + ObjectEnglish.toEnglish(root);
	}

	public String toReduced() {
		return ObjectReduced.toReduced(root);
	}
	
	public void fromReduced(String str) {
		setRoot(ObjectReduced.fromReduced(str));
	}
	
	/**
	 * Create Model from XML file.
	 * @param xml
	 */
	public void fromXML(String xml) {
		
		// Read until you get an object tag.
		//int startObject = xml.indexOf("<object>");
		
		// Load object.
		//root = ObjectXML.fromXML(xml, startObject);

		// If failed then fall back with the basic item.
		if(root == null) {
			root = PrimitiveInstance3DTree.defaultModel();
		}
		
		// Clear all undo actions as we now have a new object to work with.
		performed.clear();
		undone.clear();

		updateModel();
	}

	/**
	 * Read until you get to a non-whitespace character.
	 * @param xml XML to read from.
	 * @param start Starting offset.
	 * @return New offset.
	 */
	public static int skipWhiteSpace(String xml, int start) {
		for(;start<xml.length();start++) {
			if(!isWhiteSpace(xml.charAt(start))) {
				return start;
			}
		}
		return -1;
	}

	/**
	 * True if char is whitespace.
	 * @param c Character to test.
	 * @return
	 */
	public static boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public BufferedImage thumbnail(BufferedImage background, int width, int height, double scale) {
		BufferedImage img;
		if(background == null) {
			img = WindowRender.makeBackground(width,height);
		} else {
			img = background;
		}
		
		Universe u = new Universe();
		
		NullObject3D root = new NullObject3D();
		WindowRender3DTree.makeCoordinates(root);
		buildMy3DWorld(root);

		u.root = root;
		u.view = TransformMy3D.translate(0,0,-Object3D.SCREEN_DISTANCE - 10);
		u.view.combine(TransformMy3D.rotateX(-.5));
		u.view.combine(TransformMy3D.rotateZ(.1));
		u.view.combine(TransformMy3D.stretch(scale,scale,scale));
		
		u.render(img,Universe.RENDER_NORMAL);
		
		return img;
	}
}
