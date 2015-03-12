package featuregame;

import iomanager.IOWeb;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import creator3DTree.Model3DTree;

import tools.Lexicon;
import tools.UserCredentials;

/**
 * Store game state information and manipulate changes.
 * @author bkievitk
 */

public class GameState {

    // Number of items to show in the drop box.
    static final int MAX_BOX_ITEMS = 5;

    // Used to indicate a prefix not in the defined list.
    public static final String NEW_FEATURE_PREFIX = "[new: ";
    
	// Round counts.
    public static final int TOTAL_ROUNDS = 3;
    public static final int TOTAL_CLUES = 10;
    
	// Team IDs.
	public static final int HUMAN_TEAM = 0;
	public static final int AI_TEAM = 1;
    private static int[] teams = {HUMAN_TEAM, AI_TEAM};

	// Player states.
    public enum PlayerState { Thinking, Typing, Guessing, Results, Finished };
    
    // AI status.
    private int aiNumLettersTyped = 0;
    private String currentAIFeature = "";

    // Main references.
    private PanelDescribe mainGUI;
    private Lexicon lexicon;

    // Team state information.
    private int featuresDone[];				// How many clues have been filled in.
    private PlayerState[] playerStates;		// State.
    private int roundsComplete[];			// How many rounds have been completed. 
    private String targetWords[][];			// List of words to represent for all rounds.
    private int scores[];					// Scores for both teams.
    private String guessWords[][];			// Guessed words for each round.
    private String allFeatures[][][];		// Features entered for this round.
    private long allFeatureTimes[][][];		// Features times entered for this round.
    
    // Starting time.
    private long startTime;
    
    // This gets filled with options with auto-complete.
    private DefaultListModel listModel = new DefaultListModel();
    
    //private String password;
    //private int playerID;
    public UserCredentials credentials;
        
    public GameState(Lexicon lexicon, UserCredentials credentials) {
        this.lexicon = lexicon;
        this.credentials = credentials;
        
        // Select a clue for the AI to start typing.
        // This isn't real or thought of. It's a random feature.
        if(lexicon == null) {
        	currentAIFeature = null;
        } else {
        	currentAIFeature = FeatureGameGUI.maskText(lexicon.getRandomFeature());
        }
        
        // Build arrays.
        playerStates = new PlayerState[2];
        roundsComplete = new int[2];
        targetWords = new String[2][TOTAL_ROUNDS];
        guessWords = new String[2][TOTAL_ROUNDS];
        allFeatures = new String[2][TOTAL_ROUNDS][TOTAL_CLUES];
        allFeatureTimes = new long[2][TOTAL_ROUNDS][TOTAL_CLUES];
        scores = new int[2];
        featuresDone = new int[2];
        
        // Do reset.
        blockReset(true);
    }

   /* public String getPassword() {
    	return password;
    }
    
    public int getPlayerID() {
    	return playerID;
    }
    
    public void setPlayerInfo(String password, int playerID) {
		System.out.println("B " + password);

		
        this.password = password;
        this.playerID = playerID;
    }*/
    
    
    /**
     * Reset all team information.
     */
    public void blockReset(boolean resetScores) {
    	
    	// Fill team initial state.
    	for(int team : teams) {
    		playerStates[team] = PlayerState.Thinking;
    		roundsComplete[team] = 0;
    		targetWords[team] = buildTargetWordList(team);
    		if(resetScores) {
    			scores[team] = 0;
    		}

            // Clear features for all rounds.
    		for(int i=0;i<allFeatures[team].length;i++) {
	            for(int j=0;j<allFeatures[team][roundsComplete[team]].length;j++) {
	            	allFeatures[team][i][j] = null;
	            }
    		}
    	}

    	// Modify show.
    	if(mainGUI != null) {
    		mainGUI.updateScores();
	    	mainGUI.updateWords();
	        mainGUI.updateRoundsRemaining();
	        for(int team : teams) {
	        	mainGUI.clearAllFeatures(team);
	        }
    	}
        
    	startTime = System.currentTimeMillis();
    }
    
    /**
     * The human made a guess at the primary object.
     * @param guess
     */
    public boolean humanGuess(String guess) {
    	
    	// Add guess.
    	guessWords[HUMAN_TEAM][roundsComplete[HUMAN_TEAM]] = guess;
    	
    	// See if it is right.
    	if(guess.equals(getCorrectAnswer())) {
    		addToScore(HUMAN_TEAM, 1000);
    	}
    	
		IOWeb.saveFeatureGuessRound(credentials.userID, credentials.password, this);
		
    	// Update round counter.
    	roundsComplete[HUMAN_TEAM] ++;
    	    	
        return isFinished(HUMAN_TEAM);
    }
    
    /**
     * Simulate the AI team making a guess by awarding it points with some arbitrary probability.
     */
    public void aiGuess() {
    	int r = Model3DTree.rand.nextInt(100);
    	if(r < 66) {
    		addToScore(AI_TEAM, 1000);
    	}
	}
    
    public String getCorrectAnswer()
    {
    	return targetWords[HUMAN_TEAM][roundsComplete[HUMAN_TEAM]].trim();
    }
    
    /**
     * Fill in all features for this round.
     * @param team
     */
    public void fillFeatures(int team) {
    	ArrayList<String> features = lexicon.getFeatures(getWord(team), TOTAL_CLUES);
    	for(int i=0;i<features.size();i++) {
    		allFeatures[team][roundsComplete[team]][i] = features.get(i);
    	}
    }
    
    /**
     * Get all entered features.
     * @param team
     * @return
     */
    public Vector<String> getEnteredFeatures(int team) {
    	Vector<String> features = new Vector<String>();
    	for(String feature : allFeatures[team][roundsComplete[team]]) {
    		if(feature != null) {
    			features.add(feature);
    		}
    	}
    	return features;
    }
    
    public String getGuessedWord(int team) {
    	return guessWords[team][roundsComplete[team]];
    }
    
    /**
     * Get all entered feature times.
     * @param team
     * @return
     */
    public Vector<Long> getEnteredTimes(int team) {
    	Vector<Long> features = new Vector<Long>();
    	for(int i=0;i<allFeatures[team][roundsComplete[team]].length;i++) {
    		if(allFeatures[team][roundsComplete[team]][i] != null) {
    			features.add(allFeatureTimes[team][roundsComplete[team]][i]);
    		}
    	}
    	return features;
    }

    /**
     * Human just finished a feature. Update.
     */
    public void incrementHumanFeatureCount() {    	
    	featuresDone[HUMAN_TEAM] ++;
    }
    
    /**
     * This is the list of features that the user may by typing.
     * @return
     */
    public ListModel getListModel() {
    	return listModel;
    }
    
    /**
     * Fill in the features based on the target word.
     * @param text
     */
    public void updateListModel(String text) {
    	
    	// Clear old.
	    listModel.clear();
	    
	    if(text != null) {
		    // Add found features.
		    ArrayList<String> choices = lexicon.getMatchingFeatures(text, MAX_BOX_ITEMS);
		    for (String s : choices) {
		        listModel.addElement(s);
		    }
		    
		    // Add as new feature if no matches.
		    if (!choices.contains(text)) {
		        listModel.addElement(NEW_FEATURE_PREFIX + text + "]");
		    }
	    }
    }
    
    /**
     * Need to link the GUI as soon as possible.
     * @param mainGUI
     */
    public void linkGUI(PanelDescribe mainGUI) {
    	this.mainGUI = mainGUI;
    	mainGUI.updateWords();
        mainGUI.updateRoundsRemaining();
    }
        
    /**
     * This builds the list of words that will be targets throughout all rounds.
     * This is done beforehand to make it easier not to supply doubles of words.
     * @param team
     * @return
     */
    private String[] buildTargetWordList(int team) {
    	String[] targetWordList = new String[TOTAL_ROUNDS];
    	for(int i=0;i<TOTAL_ROUNDS;i++) {
    		String answer;
    		do {
    			if(lexicon == null) {
    				answer = null;
    			} else {
    				answer = lexicon.getRandomConcept();
    			}
    		} while(contains(answer,targetWordList,i));
    		targetWordList[i] = answer;
    	}
    	return targetWordList;
    }
    
    /**
     * Simple helper to see if the array contains the given object up to the size limit.
     * @param o
     * @param os
     * @param size
     * @return
     */
    private boolean contains(Object o, Object[] os, int size) {
    	if(o == null) {
    		return false;
    	}
    	for(int i=0;i<size&&i<os.length;i++) {
    		if(o.equals(os[i])) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * See if a team is finished with the current set of rounds.
     * @param team
     * @return
     */
    public boolean isFinished(int team) {
    	return roundsComplete[team] >= TOTAL_ROUNDS;
    }
    
    /**
     * Update score.
     * @param team
     * @param amount
     */
    public void addToScore(int team, int amount) {
    	scores[team] += amount;
    	mainGUI.updateScores();
    }
    
    /**
     * AI types a letter of their feature.
     */
    public void typeAILetter() {    	
    	
    	// Update count.
    	aiNumLettersTyped ++;
    	
    	// If the computer types a regular character, display it.
        if (aiNumLettersTyped <= currentAIFeature.length()) {
        	mainGUI.updateAIFeatures();
        } else {
        	// If the computer reached the end of a clue...
        	featuresDone[AI_TEAM]++;
            currentAIFeature = FeatureGameGUI.maskText(lexicon.getRandomFeature());
            aiNumLettersTyped = 0;
            playerStates[AI_TEAM] = PlayerState.Thinking;

            // If the computer reached the end of the textboxes, its partner can make a guess...
            if (featuresDone[AI_TEAM] >= TOTAL_CLUES) {
                cluesFinished(AI_TEAM);
            }
            	
            // Show results.
            mainGUI.updateAIFeatures();
        }
    }
    
    public void setFeature(int team, String feature) {
    	if(roundsComplete[team] < allFeatureTimes[team].length) {
    		if(featuresDone[team] < allFeatureTimes[team][roundsComplete[team]].length) {
	    		allFeatureTimes[team][roundsComplete[team]][featuresDone[team]] = System.currentTimeMillis() - startTime;
	    		allFeatures[team][roundsComplete[team]][featuresDone[team]] = feature;
    		}
    	}
    }
    
    /**
     * Update the state of a player.
     * @param team
     * @param state
     */
    public void setPlayerState(int team, PlayerState state) {
    	playerStates[team] = state;
    	if (state == PlayerState.Finished) 
    	{
    		roundsComplete[team] = TOTAL_ROUNDS;
    	}
    }
    
    /**
     * New feature description round for either team.
     * @param team
     */
    public void advanceDescriptionRound(int team) {
    	
    	// Save results.
    	if(team == HUMAN_TEAM) {
    		IOWeb.saveFeatureDescribeRound(credentials.userID, credentials.password, this);
    	}
    	
    	// Back to thinking.
    	playerStates[team] = PlayerState.Thinking;
    	
    	// Update round counter.
    	roundsComplete[team] ++;
    	
    	// Reset clues.
    	featuresDone[team] = 0;
    	
        if (isFinished(team)) {
            if (isFinished(1 - team)) {
            	// Both teams are finished.
            	mainGUI.endMatch();
            } else {
            	// Only the given team is finished.
            }
        } else {
        	
        	// Make visual changes.
        	mainGUI.updateWords();
            mainGUI.clearAllFeatures(team);
        }
        mainGUI.updateRoundsRemaining();
    }

    public String[] nonNulls(String[] array) {
    	int i;
    	for(i=0;i<array.length;i++) {
    		if(array[i] == null) {
    			i--;
    			break;
    		}
    	}
    	String[] ret = new String[i];
    	for(i=0;i<ret.length;i++) {
    		ret[i] = array[i];
    	}
    	
    	return ret;
    }
    
    /**
     * The feature writer has entered all of their clues.
     * Now the AI must guess the word.
     */
    public void cluesFinished(int team) {
    	
    	// Enter guessing round.
    	playerStates[team] = PlayerState.Guessing;
    	
    	if(team == HUMAN_TEAM) {
	    	// Perform and store guess.
	    	guessWords[team][roundsComplete[team]] = lexicon.makeGuess(nonNulls(allFeatures[team][roundsComplete[team]]), targetWords[team][roundsComplete[team]]);
    	} else {
    		if(FeatureGameGUI.random.nextDouble() < .7) {
    			guessWords[team][roundsComplete[team]] = targetWords[team][roundsComplete[team]];
    		} else {
    			guessWords[team][roundsComplete[team]] = lexicon.getRandomConcept();
    		}
    	}
    }
        
    public void submitGuess(int team) {
    	if(roundsComplete[team] >= guessWords[team].length) {
    		
    	} else {
	    	if(guessWords[team][roundsComplete[team]].equals(targetWords[team][roundsComplete[team]])) {
	    		addToScore(team, 1000);
	    	}
    	}
    }
    
    public boolean lastGuessCorrect(int team) {
    	return guessWords[team][roundsComplete[team]].equals(targetWords[team][roundsComplete[team]]);
    }
    
    public String lastGuess(int team) {
    	return guessWords[team][roundsComplete[team]];
    }
        
    public int getRoundsRemaining(int team) {
    	return TOTAL_ROUNDS - roundsComplete[team];
    }
    
    public PlayerState getPlayerState(int team) {
    	return playerStates[team];
    }

    public String getWord(int team) {
    	if(roundsComplete[team] >= targetWords[team].length) {
    		return "Complete";
    	}
    	return targetWords[team][roundsComplete[team]];
    }
    
    public int getScore(int team) {
    	return scores[team];
    }
    
    public int getClueID(int team) {
    	return featuresDone[team];
    }
    
    public String getAIClueString() {
    	return currentAIFeature.substring(0,aiNumLettersTyped);
    }

	

}
