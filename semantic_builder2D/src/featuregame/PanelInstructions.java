package featuregame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PanelInstructions extends BackgroundPanel {

	private static final long serialVersionUID = 6640948524881342705L;
	
	private int instructionsOn;
	private JButton ok;
	
	public PanelInstructions(final FeatureGameGUI mainGUI, final String[] instructions, Image img) {
		super(img);
		
		setLayout(new BorderLayout());
				
        final JTextArea text = new JTextArea();
        text.setFont(new java.awt.Font("Arial", 0, 18));
        
        text.setLineWrap(true);
        text.setMargin(new Insets(15,15,15,15)); 
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setEditable(false);
        
        text.setText(instructions[instructionsOn]);
        add(text,BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        add(bottomPanel,BorderLayout.SOUTH);
        
        ok = new JButton("ok");
        
        ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				instructionsOn++;
				if(instructionsOn >= instructions.length) {
					mainGUI.advanceGameState();
				} else {
					text.setText(instructions[instructionsOn]);
				}
			}
        });
        
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.add(ok,BorderLayout.EAST);
        
	}
}
