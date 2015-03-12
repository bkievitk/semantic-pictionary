package animals;

import java.io.*;
import java.net.URL;

import creator2DTree.*;
import java.util.*;

public class PartsLoader {
	
	public static String webHostParts = "http://www.indiana.edu/~semantic/geonParts/";
	
	public static PrimitiveInstance2DTree getWeb(String partName) {
		
		if(partName.matches("[a-zA-Z0-9]*")) {
			try {
				ObjectInputStream in = new ObjectInputStream((new URL(webHostParts + partName)).openStream());
				PrimitiveInstance2DTree part = (PrimitiveInstance2DTree)in.readObject();
				return part;
			} catch(IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Vector<PrimitiveInstance2DTree> getParts() {
	    
		Vector<PrimitiveInstance2DTree> parts = loadFromList();
		/*
	    PrimitiveInstance2DTree body = new PrimitiveInstance2DTree(Primitive2D.shapes.get("circle"));
		body.color = Color.GREEN;
		body.setEditable(PrimitiveInstance.EDIT_COLOR, false);
		
	    PrimitiveInstance2DTree bodyPart = new PrimitiveInstance2DTree(Primitive2D.shapes.get("circle"));
	    bodyPart.color = Color.GREEN;
		body.setEditable(PrimitiveInstance.EDIT_MOVE, false);
	    PrimitiveInstance2DConnection conn = new PrimitiveInstance2DConnection(bodyPart,body);
	    body.children.add(conn);
	    bodyPart.parent = conn;
		
		PrimitiveInstance2DTree body2 = new PrimitiveInstance2DTree(Primitive2D.shapes.get("circle"));
		body2.color = Color.ORANGE;
		body2.setEditable(PrimitiveInstance.EDIT_COLOR, false);
				
		parts.add(body);
		parts.add(body2);
				
		//parts.add(getWeb("stinger1"));
		//parts.add(getWeb("shield1"));
		//parts.add(getWeb("motor1"));
		 */
		
		return parts;
	}
	
	public static Vector<PrimitiveInstance2DTree> loadFromList() {
		Vector<PrimitiveInstance2DTree> parts = new Vector<PrimitiveInstance2DTree>();
		
		String[] items = {
		//"motor:[192,192,192,0,74,76,0,-31[128,128,128,0,60,56,4,-47,0,0,0,0,t]]",
		//"arm:[128,128,128,0,275,51,0,-25[192,192,192,544,221,50,0,-41,-1,0,1,0]]",
		//"telescope:[128,128,128,0,200,100,0,-31[192,192,192,0,100,75,0,-46,-1,0,1,0,e]]",
		//"stinger:[255,0,0,0,63,155,1,-31,s[255,0,0,563,51,100,1,-64,0,1,0,1,s][255,0,0,61,55,105,1,-64,0,1,0,1,s]]",
		"multi:[255,0,0,0,76,80,0,-1]",
		"multi:[255,0,0,0,76,80,1,-1]",
		"multi:[255,0,0,0,76,80,2,-1]",
		"multi:[255,0,0,0,76,80,3,-1]",
		"multi:[255,0,0,0,76,80,4,-1]",
		"multi:[255,0,0,0,76,80,5,-1]",
		"multi:[255,0,0,0,76,80,6,-1]"};
		for(String item : items) {
			String[] nameModel = item.split(":");
			String name = nameModel[0];
			String model = nameModel[1];
			System.out.println("Loading model " + name);
			parts.add(PrimitiveInstance2DTree.fromReduced(model,null));
		}
		
		return parts;
	}
	
	public static Vector<PrimitiveInstance2DTree> loadFromFile() {
		try {
			Vector<PrimitiveInstance2DTree> parts = new Vector<PrimitiveInstance2DTree>();
			BufferedReader r = new BufferedReader(new FileReader(new File("test")));
			String line;
			while((line = r.readLine()) != null) {
				String[] nameModel = line.split(":");
				String name = nameModel[0];
				String model = nameModel[1];
				System.out.println("Loading model " + name);
				parts.add(PrimitiveInstance2DTree.fromReduced(model,null));
			}
			return parts;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
