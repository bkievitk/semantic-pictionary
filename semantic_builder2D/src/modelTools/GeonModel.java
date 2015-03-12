package modelTools;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import action.ModelAction;


/**
 * The abstract GeonModel is used to represent a cluster of geons.
 * These can be in tree form, free form or other.
 * This also manages the undo/redo actions.
 * 
 * @author bkievitk
 */

public abstract class GeonModel implements Serializable {
	
	private static final long serialVersionUID = 1401522510789620625L;

	// These are fired when the geon model changes.
	transient private Vector<ChangeListener> updateListeners = new Vector<ChangeListener>();
	
	// These are fired when a new geon instance is selected as active.
	transient private Vector<ChangeListener> selectListeners = new Vector<ChangeListener>();

	// Need to store actions so we can undo/redo them.
	public transient LinkedList<ModelAction> performed = new LinkedList<ModelAction>();
	public transient LinkedList<ModelAction> undone = new LinkedList<ModelAction>();

	// This is the currently selected geon instance.
	public PrimitiveInstance selected;
	
	public void addUpdateListener(ChangeListener cl) {
		if(updateListeners == null) {
			updateListeners = new Vector<ChangeListener>();
		}
		updateListeners.add(cl);
	}
	
	public void addSelectListener(ChangeListener cl) {
		if(selectListeners == null) {
			selectListeners = new Vector<ChangeListener>();
		}
		selectListeners.add(cl);
	}
	

	public PrimitiveInstance getSelected() {
		return selected;
	}
	
	public void clear() {
		performed.clear();
		undone.clear();
		selected = null;
		updateModel();
	}

	// These must be implemented for each type of model.
	// This is the short form which is used to store to databases, etc.
	public abstract String toReduced();
	public abstract void fromReduced(String reduced);
	public abstract String toXML();
	public abstract int countObjects();
	
	/**
	 * Undo the last action.
	 */
	public void undo() {
		
		// Only if there is an action to undo.
		if(performed.size() > 0) {
			
			// Get action and undo it.
			ModelAction action = performed.removeLast();
			action.undoAction(this);
			
			// Add to the undone stack so you can redo it if needed.
			undone.addLast(action);
			updateModel();
			setSelected(null);
		}
	}
	
	/**
	 * Redo an undone action.
	 */
	public void redo() {
		
		// Only if there is an action to redo.
		if(undone.size() > 0) {
			ModelAction action = undone.removeLast();
			action.performAction(this);
			performed.addLast(action);
			setSelected(null);
		}
	}
	

	/**
	 * Perform the given action on the model.
	 * @param action Action to perform.
	 */
	public void performAction(ModelAction action) {
		if(!action.error) {
			
			// If you have done an action before.
			if(performed.size() > 0) {
				
				// See if you can combine this action with the last one.
				ModelAction combined = performed.getLast().combine(action);
				
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
		}
	}
	
	public void setSelected(PrimitiveInstance object) {
		
		// See if you have re-selected the same object.
		if(selected == object || object == null) {
			// If this is a re-selection then un-select it.
			selected = null;
		} else {
			// If this is a new selection, set it and then inform the object.
			selected = object;
		}
		
		// Inform listeners of change.
		selectObject();
		updateModel();
	}
	
	private void selectObject() {		
		ChangeEvent event = new ChangeEvent(this);
		for(ChangeListener c : selectListeners) {
			c.stateChanged(event);
		}
	}
	
	public void updateModel() {
		ChangeEvent event = new ChangeEvent(this);
		if(updateListeners != null) {
			for(ChangeListener c : updateListeners) {
				c.stateChanged(event);
			}
		}
	}
	
	public abstract BufferedImage thumbnail(BufferedImage background, int width, int height, double scale);
}
