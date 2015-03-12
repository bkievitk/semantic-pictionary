package iomanager;

import java.io.*;
import java.util.regex.Pattern;

import modelTools.GeonModel;

import tools.WordPair;
import templates.UserMessage;

public class IOFile implements LoadWord, SaveGuess {

	private int wordID = 0;
	
	private BufferedReader words;
	private BufferedWriter saveModelsTo;

	private BufferedReader savedModels;
	private BufferedWriter saveGuessesTo;
	
	private UserMessage messager;
	
	public IOFile(File words, File saveModelsTo, File savedModels, File saveGuessesTo, UserMessage messager) {
		try {
			this.messager = messager;
			this.words = new BufferedReader(new FileReader(words));
			this.saveModelsTo = new BufferedWriter(new FileWriter(saveModelsTo));
			this.savedModels = new BufferedReader(new FileReader(savedModels));
			this.saveGuessesTo = new BufferedWriter(new FileWriter(saveGuessesTo));
		} catch(IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e) {
			
		}
	}
	
	
	public WordPair loadWord(int playerID, String gameType) {
		if(words == null) {
			messager.showMessage("Unable to lead word since word set is empty.", UserMessage.ERROR);
			return null;
		}
		
		try {
			String line;
			
			while(true) {
				// Read lines until you get a dead one and exit or a live one and not comment.
				line = words.readLine();
				if(line == null) {
					messager.showMessage("Unable to load word since all words have been read.", UserMessage.ERROR);
					return null;
				} 
				
				line = line.trim();
				
				if(line.length() > 0 && line.charAt(0) != '#') {
					break;
				}
			}
			
			// See if it has a number id leading it.
			String[] parts = line.split(",");
			if(parts.length > 1 && Pattern.matches("^\\d*$", parts[0].trim())) {
				messager.showMessage("Word read from file.", UserMessage.INFORM);
				return new WordPair(parts[1].trim(),Integer.parseInt(parts[0].trim()));
			}
			
			// Add the next id to word.
			WordPair word = new WordPair(line,wordID);
			wordID++;
			messager.showMessage("Word read from file.", UserMessage.INFORM);
			return word;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public boolean saveModel(GeonModel model, int wordID, int playerID, String password, String gameType) {	
		if(saveModelsTo != null) {
			try {
				saveModelsTo.write(model.toReduced() + "~" + wordID + "~" + playerID + "~" + password + "\r\n");
				saveModelsTo.flush();
				messager.showMessage("Model saved to file.", UserMessage.INFORM);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				messager.showMessage("IOError", UserMessage.ERROR);
				return false;
			}
		}
		messager.showMessage("No file was specified.", UserMessage.ERROR);
		return false;
	}
	
	public int loadModel(GeonModel model, int playerID, String gameType) {
		try {
			String[] modelData = savedModels.readLine().split("~");
			model.fromReduced(modelData[0]);
			messager.showMessage("Model sucessfully loaded from file.", UserMessage.INFORM);
		} catch (IOException e) {
			messager.showMessage("Error loading model from file.", UserMessage.ERROR);
			e.printStackTrace();
			return -1;
		}
		
		return 1;
	}
	
	public boolean saveWordGuess(int playerID, String password, String gameType, int direction, int modelID, int guessID) {
		try {
			saveGuessesTo.write(playerID + "~" + password + gameType + "~" + direction + "~" + modelID + "~" + guessID + "\r\n");
			messager.showMessage("Word guess saved to file.", UserMessage.INFORM);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			messager.showMessage("Word guess not sucessfully saved to file.", UserMessage.ERROR);
			return false;
		}
	}
}
