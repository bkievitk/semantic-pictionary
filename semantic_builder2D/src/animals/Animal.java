package animals;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import animals.functional.FunctionalUnit;

import creator2DTree.*;

public class Animal implements Serializable {
	
	private static final long serialVersionUID = 5427228437016408225L;

	// The model of the animal.
	public Model2DTree model;
	
	// Spare parts that you have collected.
	public Vector<PrimitiveInstance2DTree> parts = new Vector<PrimitiveInstance2DTree>();
	public Vector<FunctionalUnit> functionalUnits = new Vector<FunctionalUnit>();
		
	// Location of the center piece.
	public double x = 50;
	public double y = 50;
	
	// Basic statistics.
	public long points;
	public int health;
	public int maxHealth;
	
	// Link to the world that you are in.
	// This is used to contact other agents in the world.
	public AnimalWorld world;
	
	// Create an animal based on a model.
	public Animal(final Model2DTree model, AnimalWorld world) {
		this.model = model;
		this.world = world;
		
		buildFunctionalList(model.root);

		// When model changes, update the functional component list.
		model.addUpdateListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				buildFunctionalList();
			}
		});
		
	}

	public Animal(final Model2DTree model) {
		this(model,null);
	}
					
	public String encodeString() {
		
		// Convert model first.
		String modelString = model.toReduced();
		
		// Then convert all spare parts.
		for(PrimitiveInstance2DTree part : parts) {
			modelString += ":" + part.toReduced();
		}
		 
		// Then convert basic data.
		modelString += ":" + points + "," + health + "," + maxHealth;
		
		return modelString;
	}
	
	public static Animal decodeString(String str, AnimalWorld world) {
		
		// Split into chunks.
		String[] parts = str.split(":");
		
		// Decode the model part first.
		Model2DTree model = new Model2DTree();
		Animal animal = new Animal(model);
		animal.world = world;
		animal.model.fromReduced(parts[0]);
		animal.buildFunctionalList();
		
		// Decode each of the spare parts.
		for(int i=1;i<parts.length-1;i++) {
			animal.parts.add(PrimitiveInstance2DTree.fromReduced(parts[i],animal));
		}

		// Decode the basic data.
		String[] basics = parts[parts.length-1].split(",");
		animal.points = Long.parseLong(basics[0]);
		animal.health = Integer.parseInt(basics[1]);
		animal.maxHealth = Integer.parseInt(basics[2]);
		
		return animal;
	}

	/**
	 * The functional list must be rebuilt ever time the model changes to remove or add a new functional component.
	 */
	public void buildFunctionalList() {
		functionalUnits.clear();
		buildFunctionalList(model.root);
	}
	
	/**
	 * Find each functional module in the model and add to the main list.
	 * @param node
	 * @param world
	 */
	private void buildFunctionalList(PrimitiveInstance2DTree node) {
		
		if(node.functional != null) {
			node.functional.rebindModule(node,this);
			functionalUnits.add(node.functional);
		}
		for(PrimitiveInstance2DConnection child : node.children) {
			buildFunctionalList(child.child);
		}
	}
	
	/**
	 *  A time step has passed.
	 */
	public void tick() {	
		for(FunctionalUnit unit : functionalUnits) {
			unit.tick();
		}
	}

	/**
	 * Render into graphical context.
	 * @param g
	 */
	public void renderAnimal(Graphics g) {
		
		AffineTransform rootTransform = new AffineTransform();
		rootTransform.translate(x,y);
		rootTransform.scale(20, 20);
		rootTransform.rotate(model.root.rotation[0]/100.0);
			
		// Render.
		model.root.render(g, rootTransform,model.getSelected(),false);
	}
	
}
