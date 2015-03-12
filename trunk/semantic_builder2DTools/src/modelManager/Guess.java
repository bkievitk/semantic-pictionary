package modelManager;

import iomanager.IOWeb;

import java.io.*;
import java.net.*;
import java.util.*;

import tools.WeightedObject;

import admin.ModelData;
import admin.ModelManager;


public class Guess implements Serializable {
	private static final long serialVersionUID = 3858229949125571825L;
	
	public String guesserName;
	public int guesserID;
	public int modelID;
	public int guessID;
	public int direction;
	public String gameType;
	public String time;	
	public transient ModelData model;
	
	public static void main(String[] args) {
		Vector<Guess> guesses = getAllGuesses(new File("8-21-14_guesses.dat"));		
		Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
		ModelManager.refineModels(models);			
		linkModels(models, guesses);
	}
	
	public static void linkModels(Vector<ModelData> models, Vector<Guess> guesses) {
		Hashtable<Integer,ModelData> modelsByID = new Hashtable<Integer,ModelData>();
		for(ModelData model : models) {
			if(modelsByID.containsKey(model.modelID)) {
				System.out.println("Two models with the same ID");
			}
			
			modelsByID.put(model.modelID, model);
		}

		int badModels = 0;
		int goodGuess = 0;
		int badGuess = 0;
		for(Guess guess : guesses) {
			ModelData model = modelsByID.get(guess.modelID);
			if(model == null) {
				badModels++;
			} else {
				guess.model = model;
				
				if(guess.guessID == model.wordID) {
					goodGuess++;
					model.correctGuesses ++;
				} else {
					badGuess++;
					model.incorrectGuesses ++;
				}
			}
		}

		System.out.println("Bad models: " + badModels + "/" + guesses.size());
		System.out.println("Good guesses: " + goodGuess);
		System.out.println("Bad guesses: " + badGuess);
		System.out.println("fraction: " + (goodGuess / (double)(goodGuess + badGuess)));
		System.out.println("baseline: " + (1 / 745.0));

		Hashtable<String,Integer> correctGuesses = new Hashtable<String,Integer>();
		Hashtable<String,Integer> incorrectGuesses = new Hashtable<String,Integer>();
		for(ModelData model : models) {
			Integer countC = correctGuesses.remove(model.word);
			if(countC == null) {
				countC = 0;
			}
			
			Integer countIC = incorrectGuesses.remove(model.word);
			if(countIC == null) {
				countIC = 0;
			}
			
			correctGuesses.put(model.word, countC + model.correctGuesses);
			incorrectGuesses.put(model.word, countIC + model.incorrectGuesses);
		}
		
		WeightedObject<String>[] wordWeights = new WeightedObject[correctGuesses.size()];
		int i = 0;
		
		for(String word : correctGuesses.keySet()) {
			int correct = correctGuesses.get(word);
			int incorrect = incorrectGuesses.get(word);
			double fract = correct / (double)(correct + incorrect);
			if(correct + incorrect == 0) {
				fract = 0;
			}
			wordWeights[i] = new WeightedObject<String>(word, fract);
			i++;
		}
		
		Arrays.sort(wordWeights);
		for(WeightedObject<String> wordW : wordWeights) {
			System.out.println(wordW.object + " " + wordW.weight);
		}

	}
	
	public Guess(String line) {
		String[] parts = line.split("~");
		
		if(parts.length != 7) {
			System.out.println("Line error [" + line + "]");
		} else {
			guesserName = parts[0];
			guesserID = Integer.parseInt(parts[1]);
			modelID = Integer.parseInt(parts[2]);
			guessID = Integer.parseInt(parts[3]);
			direction = Integer.parseInt(parts[4]);
			gameType = parts[5];
			time = parts[6];
		}
	}
	
	public static Vector<Guess> getAllGuesses(File file) {
		
		// Read from file.
		try {
			if(file != null && file.exists()) {
				
				FileInputStream fis = new FileInputStream(file);
				boolean isSerialized = fis.read() == 172 && fis.read() == 237;
				fis.close();

				if(isSerialized) {
					System.out.println("Reading guess data from file as serialized data " + file);
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
					Vector<Guess> guesses = (Vector<Guess>)ois.readObject();
					ois.close();
					return guesses;
				} else {
					System.out.println("Reading guess data from file as ASCII line data " + file);
					return getAllGuesses(new BufferedReader(new FileReader(file)));
				}
				
			}
		} catch(IOException e) {
			System.out.println("Failed to read guess data from file.");
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to read guess data from file due to file not being serialized correctly.");
		}
		
		// Read from web.
		try {
			
			System.out.println("Reading guess data from web.");
			
			// Connect to website.
            URL url = new URL(IOWeb.webHostIO + "adminGetSPictionaryGuess.php?adminPassword=" + ModelManager.adminPassword);
            URLConnection connection = url.openConnection();

            // Read data.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            
            Vector<Guess> guesses = getAllGuesses(in);
            try {
            	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            	out.writeObject(guesses);
            	out.close();
            } catch(IOException e) {
            	e.printStackTrace();
            }
            
            return guesses;
		} catch(IOException e) {
			System.out.println("Failed to read model data from web.");
			e.printStackTrace();
			return null;
		}
	}
	
	public static Vector<Guess> getAllGuesses(BufferedReader in) throws IOException {
    	Vector<Guess> guesses = new Vector<Guess>();
        
        String line;
        
        while((line = in.readLine()) != null) {
        	if(line.split("~").length != 7) {
        		System.out.println("Line error [" + line + "]");
        	} else {
        		guesses.add(new Guess(line));
        	}
        }  
        
        return guesses;
	}
}
