package admin;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import modelTools.GeonModel;
import creator2DNoTree.Model2DNoTree;
import creator2DTree.Model2DTree;
import creator3DTree.Model3DTree;

public class ModelData implements Serializable {
	
	private static final long serialVersionUID = -5108111129168664303L;
	
	public int modelID;
	public int wordID;
	public String word;
	public int playerID;
	public String playerName;
	public String representation;
	public int timesSolved;
	public String gameType;
	public transient GeonModel model;
	public int direction;
	public Player playerData;
	public Date time;
	public String line;

	public transient int correctGuesses = 0;
	public transient int incorrectGuesses = 0;

	public static final int DIRECTION_LEFT = -1;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_UNKNOWN = 0;
	public static final int DIRECTION_BAD = 2;
	
	public ModelData(String line) {
		this.line = line;
		
		String[] parts = line.split("~");
		
		if(parts.length >= 6) {
			modelID = Integer.parseInt(parts[0]);
			wordID = Integer.parseInt(parts[1]);
			playerID = Integer.parseInt(parts[2]);
			playerName = parts[3];
			representation = parts[4];
			gameType = parts[5];
			word = parts[6];
			
			if(parts.length > 6) {
				try {
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					time = formatter.parse(parts[7]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			buildModel();
		} else {
			System.out.println("Error line: [" + line + "]");
		}
	}
	
	public boolean buildModel() {
		try {
			if(			gameType.equals(main.MainApplet.GAME_3D_TREE) || 
						gameType.equals(main.MainApplet.GAME_3D_TREE + ":build")) {
	       		model = new Model3DTree();
	       		model.fromReduced(representation);
	        } else if(	gameType.equals(main.MainApplet.GAME_2D_NO_TREE) || 
	        			gameType.equals(main.MainApplet.GAME_2D_NO_TREE + ":build")) {
	    		model = new Model2DNoTree();
	    		model.fromReduced(representation);
	        } else if(	gameType.equals(main.MainApplet.GAME_2D_TREE) || 
		    			gameType.equals(main.MainApplet.GAME_2D_TREE + ":build") ||
		    			gameType.equals(main.MainApplet.GAME_SUBJECT_POOL_1) ||
		    			gameType.equals(main.MainApplet.GAME_SUBJECT_POOL_2) ||
		    			gameType.equals(main.MainApplet.GAME_SUBJECT_POOL_1 + ":build") ||
		    			gameType.equals(main.MainApplet.GAME_MECHANICAL_TURK_1) ||
		    			gameType.equals(main.MainApplet.GAME_MECHANICAL_TURK_2) ||
		    			gameType.equals("1")) {
	    		model = new Model2DTree();
	    		model.fromReduced(representation);
	        } else if(gameType.equals("feature")) {
	        	// Different model.
	        } else {
	        	System.out.println("Model type [" + gameType + "] unknown.");
	        	return false;
	        }
		} catch(Exception e) {
			//e.printStackTrace();
			System.out.println("Unable to parse model [" + gameType + "]:");
			System.out.println(representation);
			return false;
		}
		return true;
	}
	
	public String toString() {
		return "modelID:" + modelID + " wordID:" + wordID + " player:" + playerName + " gameType:" + gameType;
	}
}
