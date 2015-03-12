package iomanager;

public interface SaveGuess {

	/**
	 * Save a guess you have made.
	 * @param playerID	Need your id.
	 * @param password	Need your password.
	 * @param modelID	Need the model you were guessing.
	 * @param guessID	Need your guess.
	 * @return A string result message.
	 */
	public abstract boolean saveWordGuess(int playerID, String password, String gameType, int direction, int modelID, int guessID);
}
