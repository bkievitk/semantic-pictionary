package iomanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import modelTools.GeonModel;

import templates.UserMessage;
import tools.WordPair;

public class IOGradPrimaryData implements LoadWord, SaveModel {

	private UserMessage messager;
	
	public static String[] wordSet = {
			"Furniture-Chair",		"Vehicle-Automobile",	"Weapon-Gun",			"Vegetables-Pea",			"Tools-Saw",			"Toys-Top",	
			"Furniture-Couch",		"Vehicle-Truck",		"Weapon-Knife",			"Vegetables-Carrot",		"Tools-Hammer",			"Toys-Yo-Yo",	
			"Furniture-Table",		"Vehicle-Motorcycle",	"Weapon-Hand Grenade",	"Vegetables-Broccoli",		"Tools-Screwdriver",	"Toys-Rattle",	
			"Furniture-Bed",		"Vehicle-Train",		"Weapon-Spear",			"Vegetables-Asparagus",		"Tools-Nail",			"Toys-Jacks",	
			"Furniture-Foot Stool",	"Vehicle-Airplane",		"Weapon-Bullet",		"Vegetables-Green Pepper",	"Tools-Pencil",			"Toys-Train",	
			"Furniture-Bench",		"Vehicle-Boat",			"Weapon-Arrow",			"Vegetables-Onion",			"Tools-Wedge",			"Toys-Balloon",	
			"Furniture-Lamp",		"Vehicle-Scooter",		"Weapon-Missile",		"Vegetables-Potato",		"Tools-Crowbar",		"Toys-Drum",	
			"Furniture-Piano",		"Vehicle-Tank",			"Weapon-Axe",			"Vegetables-Parsley",		"Tools-Knife",			"Toys-Sled",	
			"Furniture-Radio",		"Vehicle-Horse",		"Weapon-Scissors",		"Vegetables-Mushroom",		"Tools-Stapler",		"Toys-Sandbox",	
			"Furniture-Stove",		"Vehicle-Rocket",		"Weapon-Pitchfork",		"Vegetables-Rubarb",		"Tools-Wheelbarrow",	"Toys-Mitt",
			"Furniture-Clock",		"Vehicle-Skis",			"Weapon-Hammer",		"Vegetables-Dandelion",		"Tools-Axe",			"Toys-Horse",
			"Furniture-Telephone",	"Vehicle-Skateboard",	"Weapon-Screwdriver",	"Vegetables-Pumpkin",		"Tools-Anvil",			"Toys-Tennis Racket"};
	public int wordOn = 0;
	
	public IOGradPrimaryData(int playerID, UserMessage messager) {
        try {
        	this.messager = messager;
			URL url = new URL(IOWeb.webHostIO + "getGradWord.php?playerID=" + playerID);
			URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line = in.readLine();
            wordOn = Integer.parseInt(line);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public WordPair loadWord(int playerID, String gameType) {
		if(wordOn < wordSet.length) {
			WordPair wp = new WordPair(wordSet[wordOn] + " (" + (wordOn+1) + " of " + wordSet.length + ")",wordOn);
			wordOn++;
			messager.showMessage("Word read from list.", UserMessage.INFORM);
			return wp;
		} else {
			messager.showMessage("Word outside of list bounds.", UserMessage.ERROR);
			return null;
		}
	}
	
	public boolean saveModel(GeonModel model, int wordID, int playerID, String password, String gameType) {
		
		// Transmit reduced representation.
		String representation = model.toReduced();
		return saveModel(representation, wordID, playerID, password, gameType);
	}
	
	public boolean saveModel(String representation, int wordID, int playerID, String password, String gameType) {
		
		try {
			// Connect to website.
			// Pass all required parameters.
            URL url = new URL(IOWeb.webHostIO + "saveObj.php?wordID=" + wordID + "&playerID=" + playerID + "&password=" + password + "&representation=" + representation + "&gameType=" + gameType);
            URLConnection connection = url.openConnection();

            // Read results.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line = in.readLine();
            in.close();
            return IOManager.showLineResult(line,messager);
        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }	
                
        // There has been an error connecting.
		messager.showMessage("IOError", UserMessage.ERROR);
        return false;
	}
}
