package animals;

import java.io.Serializable;
import java.util.Vector;

import creator2DTree.Model2DTree;


public class RiggedAnimalPacket implements Serializable {

	private static final long serialVersionUID = 6819805134808806539L;
	
	public Model2DTree model;
	public char[] primaryDirections;
	public Vector<KeyAssignment> assignments;
	
	public RiggedAnimalPacket(Model2DTree model, char[] primaryDirections, Vector<KeyAssignment> assignments) {
		this.model = model;
		this.primaryDirections = primaryDirections;
		this.assignments = assignments;
	}
	
	public void reconnect() {
		for(KeyAssignment assn : assignments) {
			assn.connect(model);
		}
	}
}
