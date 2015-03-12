package featuregame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;


public class PanelDescribe extends JPanel {

	private static final long serialVersionUID = -4997294681123294976L;
	
	private JLabel roundsRemaining[] = new JLabel[2];
	private JLabel word[] = new JLabel[2];
	private JTextField features[][] = new JTextField[2][10];
	private JLabel status[] = new JLabel[2];
	private JLabel score[] = new JLabel[2];
	private JLabel guesses[] = new JLabel[2];
	private JLabel guessResults[] = new JLabel[2];
	private JPanel footers[] = new JPanel[2];
	private JList selectionBox = new JList();
	
	// State of the game.
	private GameState gameState;
	private AIController controller;
	private FeatureGameGUI mainGUI;
	
	public PanelDescribe(FeatureGameGUI mainGUI, final GameState gameState) {
		this.mainGUI = mainGUI;
		this.gameState = gameState;
		
		// The controller is built here.
		// This will manage AI logic and any other temporal requirements.
		controller = new AIController(gameState, this);
		
		// Visual.
		setLayout(new BorderLayout());
		
		// The selection box will show the available features.
		selectionBox.setFixedCellHeight(14);
		selectionBox.setModel(gameState.getListModel());
		selectionBox.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				humanWordEntered();
			}
		});
		add(selectionBox,BorderLayout.NORTH);

		// Main content is divided into two panels, one for each team.
		JPanel content = new JPanel();
		content.setOpaque(false);
		add(content,BorderLayout.CENTER);
		GridLayout layout = new GridLayout(0,2);
		layout.setHgap(60);
		content.setLayout(layout);

		// Spacing and then add in panels.
		setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
		content.add(teamPanel(0,"Team 1","You","Friendbot"));
		content.add(teamPanel(1,"Team 2","Evilbot","Beardbot"));
		
		// Link all of the human text boxes to listeners.
		for(int i=0;i<features[GameState.HUMAN_TEAM].length;i++) {
			
			// Track index as final so it can be referenced in the listener.
			final int index = i;
			
			// Listener.
			features[GameState.HUMAN_TEAM][i].addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}	
				public void keyReleased(KeyEvent arg0) {
					
					// They hit the enter key.
					if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						humanWordEntered();
					} else if(arg0.getKeyCode() == KeyEvent.VK_DOWN) {
						selectionBox.setSelectedIndex(selectionBox.getSelectedIndex()+1);
					} else if(arg0.getKeyCode() == KeyEvent.VK_UP) {
						selectionBox.setSelectedIndex(selectionBox.getSelectedIndex()-1);
					} else {
						// They hit a different key.
						String text = features[GameState.HUMAN_TEAM][index].getText();
			            if (!text.equals("")) {
			            	gameState.updateListModel(text);
			            	if (selectionBox.getSelectedIndex() == -1) {
		            	        selectionBox.setSelectedIndex(0);
		            	    }
				        }
					}
				}
			
			});
		}
	}
	
	// What is assumed to be the case when this method is called:
	// - Both teams should already be finished (either by reaching the last round or by having their states set to Finished).
	public void endMatch() {
		mainGUI.advanceGameState();
	}
	
	/**
	 * Start running the AI controller.
	 */
	public void start() {
		controller.startAI();
	}
	
	/**
	 * Show the results for a given round.
	 * @param team
	 */
	public void showRoundResults(int team) {
		footers[team].removeAll();

		String lastGuess = gameState.lastGuess(team);
		guesses[team].setText("Guessed " + (team == GameState.HUMAN_TEAM ? lastGuess : FeatureGameGUI.maskText(lastGuess)));
		
		// Show results.
		if(gameState.lastGuessCorrect(team)) {
			guessResults[team].setText("Correct");
		} else {
			guessResults[team].setText("Incorrect");
		}

		footers[team].add(guesses[team]);
		footers[team].add(guessResults[team]);
		
		footers[team].invalidate();
		footers[team].validate();
		footers[team].repaint();
	}

	/**
	 * Hide results for a round and show status and score.
	 * @param team
	 */
	public void hideRoundResults(int team) {
		footers[team].removeAll();
		footers[team].add(status[team]);
		footers[team].add(score[team]);
		footers[team].invalidate();
		footers[team].validate();
		footers[team].repaint();
	}
	
	/**
	 * The human player has completed a feature.
	 */
	public void humanWordEntered() {
		
		// Feature number.
		int clueID = gameState.getClueID(GameState.HUMAN_TEAM);
		
		// Outside of valid range.
		if (clueID >= features[GameState.HUMAN_TEAM].length) {
			return;
		}
			
		// Get typed feature from selection box.
		String feature = (String)selectionBox.getSelectedValue();
		gameState.setFeature(GameState.HUMAN_TEAM, feature);
				
		if(feature != null) {
			// If this is a new feature, then get it from the text box instead.
	        if (feature.startsWith(GameState.NEW_FEATURE_PREFIX)) {
	            feature = features[GameState.HUMAN_TEAM][clueID].getText();
	        }
	
	        // Make sure they have not used this feature already.
	        for (int i = 0; i < clueID; i++) {
	            if (features[GameState.HUMAN_TEAM][i].getText().equals(feature)) {
	                //complain("Description already used");
	                return;
	            }
	        }
	
	        // Clue is finished now.
			gameState.incrementHumanFeatureCount();
	
			// Set the text box and do not allow them to change it again.
	        features[GameState.HUMAN_TEAM][clueID].setText(feature);
	    	features[GameState.HUMAN_TEAM][clueID].setEditable(false);
	
	        // If it was the last textbox, submit their responses.
	        if (clueID == features[GameState.HUMAN_TEAM].length - 1) {
	        	gameState.cluesFinished(GameState.HUMAN_TEAM);	        	
	        } else {
	            // If it wasn't the last one, make the next text box visible and set the focus accordingly
	            //save(String.valueOf(System.currentTimeMillis()) + "\t" + feature);
	        	features[GameState.HUMAN_TEAM][clueID + 1].setEditable(true);
	        	features[GameState.HUMAN_TEAM][clueID + 1].requestFocusInWindow();
	        }
	        
	        // List model is now empty.
	        gameState.updateListModel(null); 
		} else {
			// They hit enter in an empty text box.
			
			if (clueID != 0)
			{
				gameState.cluesFinished(GameState.HUMAN_TEAM);
		        gameState.updateListModel(null);
			}
		}
    }
	
	/**
	 * Update text to show how many rounds are remaining.
	 */
	public void updateRoundsRemaining() {
		// Human rounds.
		int humanRoundsRemaining = gameState.getRoundsRemaining(GameState.HUMAN_TEAM);
       	roundsRemaining[GameState.HUMAN_TEAM].setText(humanRoundsRemaining + " " + (humanRoundsRemaining != 1 ? "rounds" : "round") + " remaining");
       	
       	// AI rounds.
		int aiRoundsRemaining = gameState.getRoundsRemaining(GameState.AI_TEAM);
       	roundsRemaining[GameState.AI_TEAM].setText(aiRoundsRemaining + " " + (aiRoundsRemaining != 1 ? "rounds" : "round") + " remaining");
    }
	
	/**
	 * Update the words.
	 */
	public void updateWords() {
		word[GameState.HUMAN_TEAM].setText(gameState.getWord(GameState.HUMAN_TEAM));
		word[GameState.AI_TEAM].setText(FeatureGameGUI.maskText(gameState.getWord(GameState.AI_TEAM)));
	}
	
	/**
	 * Reset the human features.
	 */
	public void clearAllFeatures(int team) {
		
		// Clear each text box.
		for (JTextField t : features[team]) {
            t.setEditable(false);
            t.setText("");
        }
		
		// If human, then enable the first textbox and move focus there.
		if(team == GameState.HUMAN_TEAM) {
			
			System.out.println("Re-enabling.");
			features[GameState.HUMAN_TEAM][0].setEditable(true);
			features[GameState.HUMAN_TEAM][0].requestFocusInWindow();
		}
	}
	
	/**
	 * Update AI features.
	 */
	public void updateAIFeatures() {
		if(gameState.getClueID(GameState.AI_TEAM) < features[GameState.AI_TEAM].length) {
			features[GameState.AI_TEAM][gameState.getClueID(GameState.AI_TEAM)].setText(gameState.getAIClueString());
		}
	}
	
	/**
	 * Update game status.
	 */
	public void updateStatus() {
		
	}
	
	/**
	 * Update scores.
	 */
	public void updateScores() {
		score[GameState.HUMAN_TEAM].setText("Score: " + gameState.getScore(GameState.HUMAN_TEAM));
		score[GameState.AI_TEAM].setText("Score: " + gameState.getScore(GameState.AI_TEAM));
	}
	
	/**
	 * Build the panel for this team.
	 * @param teamNumber
	 * @param teamName
	 * @param player1Name
	 * @param player2Name
	 * @return
	 */
	private JPanel teamPanel(int teamNumber, String teamName, String player1Name, String player2Name) {
		setOpaque(false);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		
		JPanel header = new JPanel(new GridLayout(0,1));
		header.setOpaque(false);
		
		GridLayout featureSpacer = new GridLayout(0,1);
		featureSpacer.setVgap(5);

		JPanel center = new JPanel(featureSpacer);
		center.setOpaque(false);
		
		JPanel footer = new JPanel(new GridLayout(0,1));
		footers[teamNumber] = footer;
		footer.setOpaque(false);
		
		panel.add(header,BorderLayout.NORTH);
		panel.add(center,BorderLayout.CENTER);
		panel.add(footer,BorderLayout.SOUTH);
		
		JLabel teamLabel = new JLabel(teamName);
		teamLabel.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 24));
		teamLabel.setForeground(new java.awt.Color(0, 0, 102));
		teamLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(teamLabel);
		
		guesses[teamNumber] = new JLabel();
		guesses[teamNumber].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		guesses[teamNumber].setForeground(new java.awt.Color(0, 0, 102));
		guesses[teamNumber].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
		guessResults[teamNumber] = new JLabel();
		guessResults[teamNumber].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		guessResults[teamNumber].setForeground(new java.awt.Color(0, 0, 102));
		guessResults[teamNumber].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				
		JLabel player1Label = new JLabel(player1Name);
		player1Label.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		player1Label.setForeground(new java.awt.Color(0, 0, 204));
		player1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(player1Label);

		JLabel player2Label = new JLabel(player2Name);
		player2Label.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		player2Label.setForeground(new java.awt.Color(0, 0, 204));
		player2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(player2Label);

		roundsRemaining[teamNumber] = new JLabel("? rounds remaining");
		roundsRemaining[teamNumber].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14));
		roundsRemaining[teamNumber].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(roundsRemaining[teamNumber]);
        
		JLabel hint = new JLabel("Press Enter when done typing clues");
		hint.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(hint);
        
		JLabel yourWordLabel = new JLabel("Your word:");
		yourWordLabel.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14));
		yourWordLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(yourWordLabel);
        
		word[teamNumber] = new JLabel("word");
		word[teamNumber].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		word[teamNumber].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		header.add(word[teamNumber]);
		
		for(int i=0;i<features[teamNumber].length;i++) {
			features[teamNumber][i] = new JTextField();
			
			if(teamNumber != 0 || i != 0) {
				features[teamNumber][i].setEditable(false);
			}
			
			center.add(features[teamNumber][i]);
		}
		
		status[teamNumber] = new JLabel("------");
		status[teamNumber].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		status[teamNumber].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		footer.add(status[teamNumber]);
		
		score[teamNumber] = new JLabel("Score: 0");
		score[teamNumber].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		score[teamNumber].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		footer.add(score[teamNumber]);
		
		return panel;
	}
	
}
