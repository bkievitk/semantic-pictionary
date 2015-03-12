package iomanager;

import java.util.Vector;

import modelTools.GeonModel;
import templates.UserMessage;
import tools.WordPair;

public class IOManager implements LoadModel, LoadWord, SaveGuess, SaveModel, FlagModel {

	private LoadModel loadModel;
	private LoadWord loadWord;
	private SaveGuess saveGuess;
	private SaveModel saveModel;
	private FlagModel flagModel;
	private UserMessage messager;
	
	public IOManager(LoadModel loadModel, LoadWord loadWord, SaveGuess saveGuess, SaveModel saveModel, FlagModel flagModel, UserMessage messager) {
		this.loadModel = loadModel;
		this.loadWord = loadWord;
		this.saveGuess = saveGuess;
		this.saveModel = saveModel;
		this.flagModel = flagModel;
		this.messager = messager;
	}
	
	public void setLoadModel(LoadModel loadModel) {
		this.loadModel = loadModel;
	}
	
	public void setLoadWord(LoadWord loadWord) {
		this.loadWord = loadWord;
	}
	
	public void setSaveGuess(SaveGuess saveGuess) {
		this.saveGuess = saveGuess;
	}
	
	public void setSaveModel(SaveModel saveModel) {
		this.saveModel = saveModel;
	}
	
	public void setFlagModel(FlagModel flagModel) {
		this.flagModel = flagModel;
	}
	
	public boolean isFlagModelSet() {
		return flagModel != null;
	}
	
	public static boolean showLineResult(String line, UserMessage messager) {
		if(line.startsWith("ERROR")) {
	    	messager.showMessage(line.substring(5), UserMessage.ERROR);
	    	return false;
	    } else if(line.startsWith("SUCCESS")) {
	    	messager.showMessage(line.substring(7), UserMessage.INFORM);
	    	return true;
	    } else {
	    	messager.showMessage("Invalid return type: [" + line, UserMessage.ERROR);
	    	return false;
	    }
	}
	
	public static String getResults(String line, UserMessage messager) {
		if(line.startsWith("ERROR")) {
	    	messager.showMessage(line.substring(5), UserMessage.ERROR);
	    	return null;
	    } else if(line.startsWith("SUCCESS")) {
	    	return line.substring(7);
	    } else {
	    	messager.showMessage("Invalid return type: [" + line + "]", UserMessage.ERROR);
	    	return null;
	    }
	}

	public int loadModel(GeonModel model, int playerID, String gameType) {
		if(loadModel == null) {
			messager.showMessage("No module to load model.", UserMessage.ERROR);
			return -1;
		}
		
		return loadModel.loadModel(model,playerID,gameType);
	}

	public int loadModel(Vector<String> featureSet, int playerID, String gameType) {
		if(loadModel == null) {
			messager.showMessage("No module to load model.", UserMessage.ERROR);
			return -1;
		}
		return loadModel.loadModel(featureSet,playerID,gameType);
	}

	public WordPair loadWord(int playerID, String gameType) {
		if(loadWord == null) {
			messager.showMessage("No module to load word.", UserMessage.ERROR);
			return null;
		}
		return loadWord.loadWord(playerID, gameType);
	}

	public boolean saveWordGuess(int playerID, String password, String gameType, int direction, int modelID, int guessID) {
		if(saveGuess == null) {
			messager.showMessage("No module to save guess.", UserMessage.ERROR);
			return false;
		}
		return saveGuess.saveWordGuess(playerID, password, gameType, direction, modelID, guessID);
	}

	public boolean saveModel(GeonModel model, int wordID, int playerID, String password, String gameType) {
		if(saveModel == null) {
			messager.showMessage("No module to save model.", UserMessage.ERROR);
			return false;
		}
		return saveModel.saveModel(model, wordID, playerID, password, gameType);
	}

	public boolean saveModel(String model, int wordID, int playerID, String password, String gameType) {
		if(saveModel == null) {
			messager.showMessage("No module to save model.", UserMessage.ERROR);
			return false;
		}
		return saveModel.saveModel(model, wordID, playerID, password, gameType);
	}

	public boolean flagWord(int playerID, String password, int modelID) {
		if(flagModel == null) {
			messager.showMessage("No module to flag model.", UserMessage.ERROR);
			return false;
		}
		return flagModel.flagWord(playerID, password, modelID);
	}
}
