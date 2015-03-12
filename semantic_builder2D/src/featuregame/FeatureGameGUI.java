/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FeatureGameGUI.java
 *
 * Created on Jul 11, 2011, 8:10:39 PM
 */
package featuregame;

import iomanager.IOWeb;

import java.util.*;
import java.io.*;
import java.net.URL;

import javax.imageio.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import templates.PanelLogin;
import templates.PanelLoginCallback;
import tools.Lexicon;
import tools.UserCredentials;

/**
 * This is the main controller of the feature game.
 * @author Gabriel
 */

public class FeatureGameGUI extends JPanel implements PanelLoginCallback {
    
	private static final long serialVersionUID = -5141858247318644554L;

	// Nice to have a randomizer available to everyone.
	public static Random random = new Random();
    
    // Standard background.
    private BufferedImage backgroundImage;
    
    // Word knowledge.
    private Lexicon lexicon;
    
    // Possible overall game states.
    public enum State { Login, StartScreen, InstructionsDescribe, Describing, ResultsA, InstructionsGuess, Guessing, ResultsB, Conclusion };
    public static final State[] STATE_ORDER = {State.Login, State.StartScreen, State.InstructionsDescribe, State.Describing, State.ResultsA, State.InstructionsGuess, State.Guessing, State.ResultsB, State.Conclusion, State.Conclusion};
    
    // The introduction instructions.
    private String startScreenInstruction = "Welcome to the Feature version of Geon Pictionary!\n\nIf this is your first time playing, click \"Instructions\" to learn the rules.\n\nIf you've played before, click \"Begin Game\" to begin.";
    
    private String[] describeRoundInstructions = {	"In this game, you're on a team with an artificially intelligent teammate. Your job is to get your teammate to guess your word by providing clues. For instance, if your word was \"dog\", you might give clues like \"has four legs\", \"is furry\", etc.",
													"You are on Team 1. Your teammate is a computer.\n\nBoth of your opponents are computers, too.",
													"Press Enter after typing each clue. If you're done typing clues, you can press Enter a second time. The better your clues, the better your teammate's guesses will be.",
													"You'll be able to see your opponents playing while you play, although their words and clues will be hidden from you.",
													"Each word your teammate guesses correctly is 1,000 points for both of you, so it's important to take the time to give good answers.",
													"Your teammate is programmed to know about what objects are like, but she doesn't know anything about what words look or sound like.\n\nSo, if your word is \"crowbar\", clues like \"rhymes with snowbar\" or \"starts with C\" won't work at all.\n\nYou'll do better if you stick to descriptions of what crowbars are like (\"is heavy\", \"made of metal\", etc.).",
													"The first match is " + GameState.TOTAL_ROUNDS + " rounds. Get ready!\n\n\n\n(Click OK to begin.)"};
		    
    private String[] guessRoundInstructions = {		"This time, it's your turn to be the guesser.\n\nGuess the objects from the hints that your teammate provides. This time, you won't be able to see your opponents play, but they're still competing against you."};
    
    private String wrapupInstruction = "Thanks for playing! Your participation will help improve researchers' understanding of how people represent the meanings of everyday words.";
    
    // Layout.
    private CardLayout mainLayout;
    private JPanel mainPanel;
    
    // User information.
    public UserCredentials credentials;

    // Game information.
    public GameState gameState;
    private State state;

    // Panels.
    private PanelStart startPanel;
    private PanelLogin loginPanel;
    private PanelInstructions describeRoundInstructionsPanel;
    private PanelDescribe describePanel;
    private PanelResults describeResultsPanel;
    private PanelInstructions guessRoundInstructionsPanel;
    private PanelGuess guessPanel;
    private PanelResults guessResultsPanel;
    private PanelConclude conclusion;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
        	FeatureGameGUI gui = new FeatureGameGUI(null);

        	JFrame frame = new JFrame();
        	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	frame.setSize(800,600);
        	frame.add(gui);
            frame.setVisible(true);
            
        } catch (IOException x) {
            System.err.format("loading gui %s%n", x);
        }
     
    }
    
    /** 
     * Creates new form FeatureGameGUI 
     **/
    public FeatureGameGUI(UserCredentials credentials) throws IOException {
    	
    	this.credentials = credentials;
    	
    	// Initialization.
        initMemberVariables();
        initComponents();

        // Set up the game state.
        gameState.linkGUI(describePanel);
        
        // Choose where to start.
        setGameState(State.Login);
        
    }
    
    /**
     * Startup variable definitions.
     * @throws IOException
     */
    private void initMemberVariables() throws IOException
    {
        // The lexicon reads data form a file so we put it in the initialization.
        lexicon = new Lexicon();

        // Game data.
        gameState = new GameState(lexicon, credentials);
        
        // Load background image.
        try {
            URL url = new URL(IOWeb.webHost + "imgs/featureBackground.png");
            backgroundImage = ImageIO.read(url.openStream());
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
    
	public void setPlayerInfo(UserCredentials credentials) {
    	if(gameState != null) {
    		gameState.credentials = credentials;
    	}
    	advanceGameState();
	}
	
    /**
     * Simple getter for name.
     * @return
     */
    public String getPlayerName() {
    	return credentials.userName;
    }
    
    /**
     * Simple getter for password.
     * @return
     */
    public String getPlayerPassword() {
    	return credentials.password;
    }
    
    /**
     * Simple getter for id.
     * @return
     */
    public int getPlayerID() {
    	return credentials.userID;
    }
    
    /**
     * Advance to the next game state.
     */
    public void advanceGameState() {
    	for(int i=0; i<STATE_ORDER.length;i++) {    		
    		if(state.equals(STATE_ORDER[i])) {
    			setGameState(STATE_ORDER[i+1]);
    			return;
    		}
    	}
    	return;
    }
    
    /**
     * Sets the current state.
     * Pulls up the appropriate window.
     * @param state
     */
    public void setGameState(State state, boolean resetScores) {
        this.state = state;
        mainLayout.show(mainPanel, state.name());
        
        // This runs the initializations that are needed.
        switch(state) {
	    	case Describing:
	    		gameState.blockReset(resetScores);
	           	describePanel.start();
	    	break;
	    	case ResultsA:
	    		describeResultsPanel.update(1);
	    	break;
	    	case ResultsB:
	    		guessResultsPanel.update(2);
	    	break;
	    	case Guessing:
	    		gameState.blockReset(resetScores);
	    		guessPanel.showRound();
	    	break;
        }
    }
    
    public void setGameState(State state) {
    	setGameState(state, false);
    }
        
    /**
     * Perform initialization of the GUI.
     */
    private void initComponents() {

    	// Set layout.
    	mainLayout = new CardLayout();
    	mainPanel = new BackgroundPanel(backgroundImage);
    	mainPanel.setLayout(mainLayout);
    	
    	setLayout(new BorderLayout());
    	add(mainPanel,BorderLayout.CENTER);

    	// Login.
    	loginPanel = new PanelLogin(this, backgroundImage);
    	mainPanel.add(loginPanel,State.Login.name());
    	
    	// Start panel.
    	startPanel = new PanelStart(this, startScreenInstruction, backgroundImage);
        mainPanel.add(startPanel,State.StartScreen.name());
    	
    	// Instructions.
    	describeRoundInstructionsPanel = new PanelInstructions(this, describeRoundInstructions, backgroundImage);
        mainPanel.add(describeRoundInstructionsPanel,State.InstructionsDescribe.name());

        // Main game panel.
        describePanel = new PanelDescribe(this, gameState);
        mainPanel.add(describePanel,State.Describing.name());

        // Results panel.
        describeResultsPanel = new PanelResults(this, gameState, backgroundImage);
        mainPanel.add(describeResultsPanel,State.ResultsA.name());

    	// Instructions.
    	guessRoundInstructionsPanel = new PanelInstructions(this, guessRoundInstructions, backgroundImage);
        mainPanel.add(guessRoundInstructionsPanel,State.InstructionsGuess.name());
        
        // Guess panel.
        guessPanel = new PanelGuess(gameState, backgroundImage);
        mainPanel.add(guessPanel,State.Guessing.name());

        // Results panel.
        guessResultsPanel = new PanelResults(this, gameState, backgroundImage);
        mainPanel.add(guessResultsPanel,State.ResultsB.name());

        // Conclusion.
        conclusion = new PanelConclude(this, wrapupInstruction, backgroundImage);
        mainPanel.add(conclusion,State.Conclusion.name());
        
    }

    /**
     * Return a string identical to s, but with non-space characters replaced with asterisks.
     * @param s
     * @return
     */
    public static String maskText(String s) {
    	if(s == null) {
    		return null;
    	}
    	
        String retString = "";

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);        
            retString += (c == ' ' ? " " : "*");
        }

        return retString;
    }
    
}
