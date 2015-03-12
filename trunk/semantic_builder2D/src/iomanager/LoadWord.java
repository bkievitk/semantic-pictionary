package iomanager;

import tools.WordPair;

public interface LoadWord {
	/**
	 * Load a new word to build a model of.
	 * @param playerID	Need your id so we don't give you one you already made.
	 * @return The word and its id.
	 */
	public abstract WordPair loadWord(int playerID, String gameType);
}
