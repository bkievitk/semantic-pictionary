package featuregame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PanelResults extends BackgroundPanel {

	private static final long serialVersionUID = 3010540053684427234L;

	// Scores.
	private JLabel team1Score;
	private JLabel team2Score;
	
	// Indicates round number.
	private JLabel scoreLabel;
	
	// Need to hold the game state for information.
	private GameState gameState;
	
	public PanelResults(final FeatureGameGUI mainGUI, final GameState gameState, Image image) {
		super(image);
		
		this.gameState = gameState;
		
		setLayout(new BorderLayout());
		
		JPanel resultsPanel = new JPanel(new GridLayout(0,1));
		resultsPanel.setOpaque(false);
		add(resultsPanel,BorderLayout.NORTH);
		
		resultsPanel.add(new JLabel(""));
		
		scoreLabel = new JLabel();
		scoreLabel.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 24));
		scoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		resultsPanel.add(scoreLabel);
		
		JLabel team1Label = new JLabel();
		team1Label.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		team1Label.setForeground(new java.awt.Color(0, 0, 204));
		team1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		team1Label.setText("Team 1");
		resultsPanel.add(team1Label);
		
		team1Score = new JLabel();
		team1Score.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		team1Score.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		resultsPanel.add(team1Score);
		
		JLabel team2Label = new JLabel();
		team2Label.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		team2Label.setForeground(new java.awt.Color(0, 0, 204));
		team2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		team2Label.setText("Team 2");
		resultsPanel.add(team2Label);
		
		team2Score = new JLabel();
		team2Score.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		team2Score.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		resultsPanel.add(team2Score);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		add(bottomPanel,BorderLayout.SOUTH);
		
		JButton ok = new JButton("ok");
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.add(ok,BorderLayout.EAST);
        
        ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainGUI.advanceGameState();
			}
        });
	}
	
	public void update(int round) {
		team1Score.setText(gameState.getScore(0) + "");
		team2Score.setText(gameState.getScore(1) + "");
		scoreLabel.setText("* * * Scores after Round " + round + " * * *");
	}
}
