package iomanager;

import modelTools.GeonModel;

public interface SaveModel {
	/**
	 * Save a model to output device.
	 * @param model		Need the model.
	 * @param wordID	Need the wordID that the model represents.
	 * @param playerID	Need the creators id.
	 * @param password	Need the creators password.
	 * @param gameType	Type of game (3D,2D tree or not)
	 * @return A string result message.
	 */
	public abstract boolean saveModel(GeonModel model, int wordID, int playerID, String password, String gameType);
	public abstract boolean saveModel(String model, int wordID, int playerID, String password, String gameType);
}
