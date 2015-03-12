package featuregame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

public class PanelGuess extends BackgroundPanel implements ActionListener {

	private static final long serialVersionUID = 6197539123777334044L;

	private JLabel[] featureLabels;
	private JTextField answerField;
	private JLabel roundsRemainingLabel;
	private JLabel scoreLabel;
	private JLabel resultLabel;
	private GameState gameState;
	private Timer timer;
	private boolean matchOver;
	public static final int UPDATE_INTERVAL = 1000;
	
	public PanelGuess(final GameState gameState, Image image) {
		super(image);
	
		this.gameState = gameState;
		
		// Build timer.
		timer = new Timer(UPDATE_INTERVAL, this);
        timer.setInitialDelay(UPDATE_INTERVAL);
		
		setLayout(new GridLayout(0,1));
		
		featureLabels = new JLabel[GameState.TOTAL_CLUES];
		for(int i=0;i<featureLabels.length;i++) {
			featureLabels[i] = new JLabel();
			featureLabels[i].setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
			featureLabels[i].setForeground(new java.awt.Color(0, 0, 204));
			featureLabels[i].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			add(featureLabels[i]);
		}
		
		answerField = new JTextField();
		add(answerField);
		answerField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				endRound();
			}
		});

		JLabel pressEnter = new JLabel();
		pressEnter.setFont(new java.awt.Font("Arial", 0, 10));
		pressEnter.setForeground(new java.awt.Color(0, 0, 204));
		pressEnter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		pressEnter.setText("Press Enter to submit answer");
		add(pressEnter);
		
		resultLabel = new JLabel();
		resultLabel.setFont(new java.awt.Font("Arial", Font.ITALIC, 24));
		resultLabel.setForeground(new java.awt.Color(0, 0, 204));
		resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		resultLabel.setText("");
		add(resultLabel);
		
		roundsRemainingLabel = new JLabel();
		roundsRemainingLabel.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14));
		roundsRemainingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(roundsRemainingLabel);
		        
		scoreLabel = new JLabel();
		scoreLabel.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		scoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(scoreLabel);
		
	}
	
	public void endRound()
	{
		String guess = answerField.getText();
		String correctAnswer = gameState.getCorrectAnswer();
		
		if (correctAnswer.equals(guess))
		{
			resultLabel.setText("Correct!");
		}
		else
		{
			resultLabel.setText("Sorry, answer was '" + correctAnswer + "'");
		}
		answerField.setEditable(false);
		timer.restart();
	}
	
	public void showRound()
	{
		// Get features.
		gameState.fillFeatures(GameState.HUMAN_TEAM);
		Vector<String> features = gameState.getEnteredFeatures(GameState.HUMAN_TEAM);
		
		// Fill feature labels.
		for(int i=0;i<features.size();i++) {
			featureLabels[i].setText(features.get(i));
		}
		
		// Set all fields.
		answerField.setText("");
		roundsRemainingLabel.setText(gameState.getRoundsRemaining(GameState.HUMAN_TEAM) + " round(s) remaining");
		scoreLabel.setText("Score: " + gameState.getScore(GameState.HUMAN_TEAM));
		
		answerField.setEditable(true);
		answerField.requestFocusInWindow();
	}
	
	/**
	 * Timer event.
	 */
    public void actionPerformed(ActionEvent e) {  
    	
    	timer.stop();
    	resultLabel.setText("");
    	
    	String guess = answerField.getText();
    	matchOver = gameState.humanGuess(guess);
    	gameState.aiGuess();	// Randomly give the AI some points now and then.
    	
    	if (!matchOver) {
			showRound();
		}
    }
}
