package iomanager;

import java.util.Vector;

import modelTools.GeonModel;

public interface LoadModel {

	/**
	 * Load a model someone else made.
	 * @param model		Model is loaded into here.
	 * @param playerID	Need your id so you don't load your own or a model you have already identified.
	 * @param gameType	Type of game (3D,2D tree or not)
	 * @return -1 if no model was loaded, else the model id.
	 */
	public abstract int loadModel(GeonModel model, int playerID, String gameType);
	public abstract int loadModel(Vector<String> featureSet, int playerID, String gameType);
}
