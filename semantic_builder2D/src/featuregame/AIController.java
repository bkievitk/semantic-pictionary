package featuregame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import featuregame.GameState.PlayerState;

public class AIController implements ActionListener {

	// Spit a tick every UPDATE_INTERVAL ms.
    private Timer timer;

	// How often the timer gets called.
    public static final int UPDATE_INTERVAL = 5;

    // Static event durations.te
    private static final long RESULTS_DURATION = 1000;       		// 1 second
    private static final long THINKING_DURATION = 6000;       		// 1 second
    private static final long EARLY_FINISH_BONUS_DURATION = 1000;	// 1 second
    private static final long HUMAN_GUESS_DURATION = 1000;       	// 1 second

    // Event durations that change each time.
    private long currentAIKeypressDuration = 1000;       			// 1 second

    // Each player has an entry for the time of the last event.
    private long lastEventTime[];
    
    // Information about the game.
    private GameState gameState;
    private PanelDescribe mainGUI;
    
	public AIController(GameState gameState, PanelDescribe mainGUI) {	
		this.gameState = gameState;
		this.mainGUI = mainGUI;

		// Set time to current.
		lastEventTime = new long[2];
		long time = System.currentTimeMillis();
		lastEventTime[GameState.HUMAN_TEAM] = time;
		lastEventTime[GameState.AI_TEAM] = time;

		// Build timer.
		timer = new Timer(UPDATE_INTERVAL, this);
        timer.setInitialDelay(UPDATE_INTERVAL);
	}
	
	private void setAIKeypressDuration() {
		//currentAIKeypressDuration = 5; // Anything less than UPDATE_INTERVAL will result in a duration equal to UPDATE_INTERVAL; this is fine.
    	currentAIKeypressDuration = FeatureGameGUI.random.nextInt(275) + 25;

	}
	
	/**
	 * Timer event.
	 */
    public void actionPerformed(ActionEvent e) {   

    	
    	// All are finished.
    	if (gameState.isFinished(GameState.HUMAN_TEAM) && gameState.isFinished(GameState.AI_TEAM)) {
    		// Do nothing
    		return;
    	}
    	
    	
    	// Human is finished.
        if (gameState.isFinished(GameState.HUMAN_TEAM)) {
        	// If the human player's team finishes, the match ends instantly.
        	
        	gameState.setPlayerState(GameState.AI_TEAM, PlayerState.Finished);
        	mainGUI.endMatch();
        	return;
        }
        else
        // Human is playing (not finished).
        {
			 switch(gameState.getPlayerState(GameState.HUMAN_TEAM)) {
			 
			 	case Thinking:
			 		// Ignored for human player.
			 	case Typing:
			 		// Waiting for human to type features in.
			 	break;
			 	
			 	case Guessing:
		         	if (timeUp(HUMAN_GUESS_DURATION,GameState.HUMAN_TEAM)) {      		
		        		
		         		// Check results.
		        		gameState.submitGuess(GameState.HUMAN_TEAM);
		        		
		         		// Turn to results.
		         		gameState.setPlayerState(GameState.HUMAN_TEAM, GameState.PlayerState.Results);
		             }
			 	break;
			 	
			 	case Results:
		            // Show results.
			        if (timeUp(RESULTS_DURATION, GameState.HUMAN_TEAM)) {
			        	mainGUI.hideRoundResults(GameState.HUMAN_TEAM);
		        		gameState.advanceDescriptionRound(GameState.HUMAN_TEAM);
			        } else {
		    	    	mainGUI.showRoundResults(GameState.HUMAN_TEAM);
			        }
			 	break;
			 }
        }
        

        // AI team is finished.
        if (gameState.isFinished(GameState.AI_TEAM)) {
        	// If the evil AI team finished first, they rack up points while waiting for the human team to finish.
            // The game will eventually end when the human team finishes.
        	
        	if (timeUp(EARLY_FINISH_BONUS_DURATION, GameState.AI_TEAM)) {
        		gameState.addToScore(GameState.AI_TEAM, 10);
            }
        } else {
        	// AI team is playing (not yet finished).
        	
        	switch(gameState.getPlayerState(GameState.AI_TEAM)) {
    		
	    		case Thinking:
	    			
	    			// Time for computer to think of feature.
	            	if (timeUp(THINKING_DURATION,GameState.AI_TEAM)) {
	            			
            			// Select a new random duration.
	            		setAIKeypressDuration();
	            		gameState.setPlayerState(GameState.AI_TEAM, GameState.PlayerState.Typing);
	                }
	            break;
            
        		case Typing:        		
	            	// If it is type to type a key.
	                if (timeUp(currentAIKeypressDuration,GameState.AI_TEAM)) {
	                    
	                    // Choose a time to wait if the human is not finished.
	                	setAIKeypressDuration();
	
	                    // Track letters typed.
	                    gameState.typeAILetter();
	                }
        		break;
	            
        		case Guessing:
		        	// See if guess is finished.
		        	if (timeUp(HUMAN_GUESS_DURATION,GameState.AI_TEAM)) {        		
		        		// Check results.
		        		gameState.submitGuess(GameState.AI_TEAM);
		        		
		        		// Turn to results.
		        		gameState.setPlayerState(GameState.AI_TEAM, GameState.PlayerState.Results);
		            }
		        break;
		        
        		case Results:
        			// Show results.
        	        if (timeUp(RESULTS_DURATION, GameState.AI_TEAM)) {
        	        	mainGUI.hideRoundResults(GameState.AI_TEAM);
	            		gameState.advanceDescriptionRound(GameState.AI_TEAM);
        	        } else {
            	    	mainGUI.showRoundResults(GameState.AI_TEAM);
        	        }
            	break;
        	}        	
        }       
    }
    
    /**
     * Start the clock.
     */
    public void startAI() {
    	timer.start();
    }
    
    /**
     * Stop the clock.
     */
    public void stopAI() {
    	timer.stop();
    }

    /**
     * Check if time is up.
     * If it is, then it resets the event timer.
     * @param timeRequired
     * @param team
     * @return
     */
    private boolean timeUp(long timeRequired, int team) {
    	if (System.currentTimeMillis() - lastEventTime[team] > timeRequired) {
    		lastEventTime[team] = System.currentTimeMillis();
    		return true;
    	}
    	return false;
    }  
    
}
