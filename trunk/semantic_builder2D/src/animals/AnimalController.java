package animals;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Vector;

public class AnimalController implements KeyListener {
	
	public Animal animal;
	public HashSet<Character> keys = new HashSet<Character>();
	public Vector<KeyAssignment> assignments;
	public char[] basicMovement;

	public AnimalController(Animal animal) {
		this.animal = animal;
		basicMovement = new char[4];
		basicMovement[0] = 'w';
		basicMovement[1] = 'a';
		basicMovement[2] = 's';
		basicMovement[3] = 'd';
		assignments = new Vector<KeyAssignment>();
	}
	
	public void decodeString(String rep) {
		String[] parts = rep.split(":");
		
		for(int i=0;i<4;i++) {
			basicMovement[i] = parts[0].charAt(i);
		}

		assignments.clear();
		for(int i=1;i<parts.length;i++) {
			String[] assignment = parts[i].split(",");
			char key = assignment[0].charAt(0);
			int type = Integer.parseInt(assignment[1]);
			boolean direction = assignment[2].charAt(0) == 't';
			
			Vector<Integer> id = new Vector<Integer>();
			for(int j=3;j<assignment.length;j++) {
				id.add(Integer.parseInt(assignment[j]));
			}
			
			assignments.add(new KeyAssignment(key, animal.model.root.getFromID(id), type, animal.model,  direction));
		}
	}
	
	public String encodeString() {
		String rep = new String(basicMovement);
		
		for(KeyAssignment assignment : assignments) {
			char dir;
			if(assignment.direction) { dir = 't'; } else { dir = 'f'; }
			
			String assnStr = assignment.key + "," + assignment.type + "," + dir;
			
			for(int i=0;i<assignment.id.size();i++) {
				assnStr += "," + assignment.id.get(i);
			}
			
			rep += ":" + assnStr;
		}
		
		return rep;
	}
	
	public AnimalController(Animal animal, Vector<KeyAssignment> myAssignments, char[] basicMovement) {
		this.animal = animal;
		this.assignments = myAssignments;
		this.basicMovement = basicMovement;
	}
	
	public void tick() {
		if(keys.size() > 0) {			
			for(char c : keys) {
				for(KeyAssignment assignment : assignments) {
					if(assignment.key == c) {
						assignment.tick();
					}
				}
			}
			for(int i=0;i<basicMovement.length;i++) {
				if(keys.contains(basicMovement[i])) {
					if(i == 0) {
						animal.y--;
					} else if(i == 1) {
						animal.x--;
					} else if(i == 2) {
						animal.y++;
					} else if(i == 3) {
						animal.x++;
					}
					animal.model.updateModel();
				}
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		keys.add(e.getKeyChar());
	}

	public void keyReleased(KeyEvent e) {
		keys.remove(e.getKeyChar());		
	}

	public void keyTyped(KeyEvent e) {
		
	}
}
