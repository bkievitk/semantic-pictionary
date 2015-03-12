package iomanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

import featuregame.GameState;

import modelTools.GeonModel;

import templates.UserMessage;
import tools.WordComplete;
import tools.WordPair;


public class IOWeb implements LoadModel, LoadWord, SaveGuess, SaveModel, FlagModel  {

	public static String webHost = "http://www.indiana.edu/~semantic/";
	public static String webHostIO = webHost + "io/";
	
	protected UserMessage messager;
	
	public IOWeb(UserMessage messager) {
		this.messager = messager;
	}

	public static boolean saveFeatureGuessRound(int playerID, String password, GameState gameState) {
		return saveFeatureGuessRound(playerID, password, gameState.getWord(GameState.HUMAN_TEAM), gameState.getGuessedWord(GameState.HUMAN_TEAM), gameState.getEnteredFeatures(GameState.HUMAN_TEAM));
	}
	
	public static boolean saveFeatureGuessRound(int playerID, String password, String word, String guessedWord, Vector<String> features) {
				
		try {
			// Connect to website.
			// Pass all required parameters.			
			String urlString = webHostIO + "saveFeatureGuess.php?playerID=" + playerID + "&password=" + password + "&targetWord=" + word;
						
			for(int i=0;i<features.size();i++) {
				urlString += "&feature" + i +"=" + features.get(i).replaceAll(" ", "%20");
			}
			
			urlString += "&guess=" + guessedWord.replaceAll(" ", "%20");
	
			URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            // Read results.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            //String line = in.readLine();
            
            in.close();
			
			System.out.println("Writing to URL [" + urlString + "]");
            return true;
            
		} catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }	

        return false;
	}
	
	/**
	 * Save feature results.
	 * @return
	 */
	public static boolean saveFeatureDescribeRound(int playerID, String password, GameState gameState) {
		return saveFeatureDescribeRound(playerID,password,gameState.getWord(GameState.HUMAN_TEAM),gameState.getEnteredFeatures(GameState.HUMAN_TEAM),gameState.getEnteredTimes(GameState.HUMAN_TEAM));
	}
	
	public static boolean saveFeatureDescribeRound(int playerID, String password, String targetWord, Vector<String> features, Vector<Long> times) {
		
		try {
			// Connect to website.
			// Pass all required parameters.			
			String urlString = webHostIO + "saveFeatureDescription.php?playerID=" + playerID + "&password=" + password + "&targetWord=" + targetWord;
			
			for(int i=0;i<features.size();i++) {
				urlString += "&feature" + i +"=" + features.get(i).replaceAll(" ", "%20");
				urlString += "&featureTime" + i +"=" + times.get(i);
			}
			
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            // Read results.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            //String line = in.readLine();
            
            in.close();
			
			
			System.out.println("Writing to URL [" + urlString + "]");
            return true;
            
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }	

        return false;
	}
	
	public boolean flagWord(int playerID, String password, int modelID) {
		try {
			// Connect to website.
            URL url = new URL(webHostIO + "flagObject.php?playerID="+playerID + "&modelID=" + modelID + "&password=" + password);
            URLConnection connection = url.openConnection();

            // Read data.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            // First line must be word and second line, wordID.
            String line = in.readLine();
            
            // Close stream and return.
            in.close();

            return IOManager.showLineResult(line,messager);
        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        
        messager.showMessage("IO ERROR", UserMessage.ERROR);
        return false;
	}
	
	public WordPair loadWord(int playerID, String gameType) {

		System.out.println(gameType);
		try {
			// Connect to website.
            URL url = new URL(webHostIO + "getRndWrd.php?playerID="+playerID + "&gameType=" + gameType);
            URLConnection connection = url.openConnection();

            // Read data.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            // First line must be word and second line, wordID.
            String line = in.readLine();
            
            in.close();

            String partList = IOManager.getResults(line,messager);
            
            if(partList == null) {
            	return null;
            }

            String[] parts = partList.split(",");
            	
        	String word = parts[0];
            int wordID = Integer.parseInt(parts[1]);
        	messager.showMessage("Word found.", UserMessage.INFORM);
        	
            return new WordPair(word,wordID);
        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        
        messager.showMessage("IO Error", UserMessage.ERROR);
        return null;
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
			String webURL = webHostIO + "saveObj.php?wordID=" + wordID + "&playerID=" + playerID + "&password=" + password + "&representation=" + representation + "&gameType=" + gameType;
            System.out.println(webURL);
            
			URL url = new URL(webURL);
            URLConnection connection = url.openConnection();

            // Read results.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line = in.readLine();
            
            in.close();

            System.out.println(line);
            
            return IOManager.showLineResult(line,messager);
            
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }	

        messager.showMessage("IO ERROR", UserMessage.ERROR);
        return false;
	}

	public int loadModel(Vector<String> featureSet, int playerID, String gameType) {
        
		try {
			// Connect to website.
            URL url = new URL(webHostIO + "getRndObj.php?playerID=" + playerID + "&gameType=" + gameType);
            
            URLConnection connection = url.openConnection();
            
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line = in.readLine();
            in.close();
                        
            String partList = IOManager.getResults(line,messager);
            
            if(partList == null) {
            	return -1;
            }
            
            String[] parts = partList.split(",");
            
            int modelID = Integer.parseInt(parts[0]);
            for(int i=1;i<parts.length;i++) {
            	String feature = parts[i].trim();
            	if(feature.length() > 0) {
             		featureSet.add(feature);
             	}
            }
        	return modelID;
        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }	
        
    	messager.showMessage("IOError", UserMessage.ERROR);
        return -1;
	}
	
	public int loadModel(GeonModel model, int playerID, String gameType) {

		try {
			// Connect to website.
            URL url = new URL(webHostIO + "getRndObj.php?playerID=" + playerID + "&gameType=" + gameType);
            
            System.out.println(url.toString());
            URLConnection connection = url.openConnection();
            
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line = in.readLine();
            in.close();
                        
            String results = IOManager.getResults(line,messager);
            
            if(results == null) {
            	return -1;
            }
            
            int split = results.indexOf(',');
            
            if(split <= 0) {
            	messager.showMessage("Invalid format.", UserMessage.ERROR);
            	return -1;
            }
            
            int modelID = Integer.parseInt(results.substring(0,split));
            model.fromReduced(results.substring(split+1));
            
            return modelID;
        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }	
        
    	messager.showMessage("IOError", UserMessage.ERROR);
        return -1;
	}
	
	public boolean saveWordGuess(int playerID, String password, String gameType, int direction, int modelID, int guessID) {

		try {
						
			// Connect to website.
            String urlString = (webHostIO + "saveGuess.php" +
            		"?playerID=" + playerID + 
            		"&password=" + password + 
            		"&modelID=" + modelID + 
            		"&guessID=" + guessID + 
            		"&gameType=" + gameType + 
            		"&direction=" + direction);

            System.out.println("[" + urlString + "]");
            URL url = new URL(urlString);
            
            URLConnection connection = url.openConnection();
            
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line = in.readLine();
            in.close();
            
            return IOManager.showLineResult(line,messager);
        }
        catch (MalformedURLException e) {
        	System.out.println(e);
        }
        catch (IOException ee) {
        	System.out.println(ee);
        }	
        
    	messager.showMessage("IOError", UserMessage.ERROR);
    	return false;
	}
	
	public int getLabelCount(int playerID, String gameType) {
		try {
			// Connect to website.
            URL url = new URL(webHostIO + "countLabels.php?playerID=" + playerID + "&gameType=" + gameType);
            
            URLConnection connection = url.openConnection();
            
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line = in.readLine();
            in.close();
            
            return Integer.parseInt(line);
        }
        catch (MalformedURLException e) {
        	System.out.println(e);
        }
        catch (IOException ee) {
        	System.out.println(ee);
        }	
        
    	messager.showMessage("IOError", UserMessage.ERROR);
    	return -1;
	}
	
	public int getModelCount(int playerID, String gameType) {
		try {
			// Connect to website.
            URL url = new URL(webHostIO + "countModels.php?playerID=" + playerID + "&gameType=" + gameType);
            
            URLConnection connection = url.openConnection();
            
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line = in.readLine();
            in.close();
            
            return Integer.parseInt(line);
        }
        catch (MalformedURLException e) {
        	System.out.println(e);
        }
        catch (IOException ee) {
        	System.out.println(ee);
        }	
        
    	messager.showMessage("IOError", UserMessage.ERROR);
    	return -1;
	}
	
	public static WordComplete getWords(Hashtable<String,Integer> wordIDs) {
		WordComplete wc = new WordComplete();
		
		try {
			// Connect to website.
            URL url = new URL(webHostIO + "getAllWords.php");
            
            URLConnection connection = url.openConnection();
            
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line;
            while((line = in.readLine()) != null) {
            	String[] parts = line.split(",");
            	wc.addWord(parts[0]);
            	wordIDs.put(parts[0], Integer.parseInt(parts[1]));
            }
            
            return wc;
		} catch (MalformedURLException e) {
        	System.out.println(e);
        } catch (IOException ee) {
        	System.out.println(ee);
        }	
		
		return null;
	}
}
