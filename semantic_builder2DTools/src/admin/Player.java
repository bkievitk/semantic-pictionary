package admin;

public class Player {

	public int playerID;
	public int score;
	public int handedness;
	public int gender;
	public int ethnicity;
	public int race;
	
	public Player(String line) {
		String[] parts = line.split("~");
		playerID = 		Integer.parseInt(parts[0].trim());
		score = 		Integer.parseInt(parts[1].trim());
		handedness = 	Integer.parseInt(parts[2].trim());
		gender = 		Integer.parseInt(parts[3].trim());
		ethnicity = 	Integer.parseInt(parts[4].trim());
		race = 			Integer.parseInt(parts[5].trim());
	}
}
