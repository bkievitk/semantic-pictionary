package featuregame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PanelConclude extends BackgroundPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5521779742852291079L;
	private JButton playAgainGameButton;
	
	public PanelConclude(final FeatureGameGUI mainGUI, final String instruction, Image img) {
		super(img);
		
		setLayout(new BorderLayout());
				
        final JTextArea text = new JTextArea();
        text.setFont(new java.awt.Font("Arial", 0, 18));
        
        text.setLineWrap(true);
        text.setMargin(new Insets(15,15,15,15)); 
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setEditable(false);
        
        text.setText(instruction);
        add(text,BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        add(bottomPanel,BorderLayout.SOUTH);
        
        playAgainGameButton = new JButton("Play Again");
        
        playAgainGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainGUI.setGameState(FeatureGameGUI.State.Describing, true);
			}
        });
        
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.add(playAgainGameButton, BorderLayout.EAST);
        
	}
}
