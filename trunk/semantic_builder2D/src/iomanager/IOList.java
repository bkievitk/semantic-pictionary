package iomanager;

import templates.UserMessage;
import tools.WordPair;

public class IOList implements LoadWord {
	
	private UserMessage messager;
	
	String[] wordSet = {
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
	
	public IOList(UserMessage messager) {
		this.messager = messager;
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
	
}
