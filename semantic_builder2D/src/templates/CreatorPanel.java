package templates;

import iomanager.IOManager;
import java.awt.*;
import javax.swing.*;
import modelTools.GeonModel;
import tools.CompletionListener;
import tools.UserCredentials;
import tools.WordPair;

/**
 * This is the panel template for the creator system.
 * READY
 * @author bkievitk
 */

public class CreatorPanel extends JPanel implements CompletionListener {

	private static final long serialVersionUID = -2212571272025406848L;

	// Display the active word.
	public JLabel label = new JLabel();
	public IOManager iomanager;
	public GeonModel model;
	
	// Game info.
	public UserCredentials credentials;
	public WordPair word = null;
	public String gameType;
	
	public WindowOptions windowOptions;
	
	public static JPanel labeledPanel(Component c, String label) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.BLACK);
		JLabel textLabel = new JLabel(label);
		textLabel.setBackground(Color.BLACK);
		textLabel.setForeground(Color.WHITE);
		p.add(textLabel,BorderLayout.WEST);
		p.add(c,BorderLayout.CENTER);
		return p;
	}
	
	public static JPanel labelAbovePanel(Component c, String label) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(label),BorderLayout.NORTH);
		p.add(c,BorderLayout.CENTER);
		return p;
	}
	
	public CreatorPanel(UserCredentials credentials, String gameType, 
			WindowRender renderWindow,
			WindowOptions windowOptions,
			WindowPrimitiveEdit primitiveEditor,
			WindowAddPrimitive addPrimitive,
			WindowAttachment attachment,
			GeonModel model,
			UserMessagePanel messager,
			IOManager iomanager) {

		this.iomanager = iomanager;
		this.model = model;
		this.windowOptions = windowOptions;
		
		// Set password and player.
		this.credentials = credentials;
		this.gameType = gameType;
		
		// Set look and feel.
		// This forces consistency across OSs.
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");	
		} catch (Exception excep) {	
			messager.showMessage("Unable to set look and feel.",UserMessage.INFORM);
		}

		// Get the next word to make model of.
		nextWord();
		  	  
		// Get container to hold windows.
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);

		// Panel at the center of the screen.
		JPanel centerPanel = new JPanel(new BorderLayout());
		add(centerPanel,BorderLayout.CENTER);
		
			// Add label.
			if(word != null) {
				label = new JLabel("Your word is: " + word.description);
			} else {
				label = new JLabel("Error loading word.");
			}
			
			label.setBackground(Color.BLACK);
			label.setForeground(Color.WHITE);
			label.setOpaque(true);
			centerPanel.add(label,BorderLayout.NORTH);
			
			// Build the rendering window.			
			centerPanel.add(renderWindow,BorderLayout.CENTER);

			JPanel centerBottom = new JPanel(new BorderLayout());
			centerPanel.add(centerBottom,BorderLayout.SOUTH);
			centerBottom.add(messager,BorderLayout.CENTER);
			
			// Options panel.
			windowOptions.setBackground(Color.BLACK);
			windowOptions.addCompletionListener(this);
			centerBottom.add(windowOptions,BorderLayout.NORTH);
			recolor(windowOptions);
			
		// Window at the right side of the screen.
		Box actionWindow = Box.createVerticalBox();		
		actionWindow.setBackground(Color.BLACK);		
		add(actionWindow,BorderLayout.EAST);
			
			// Show the editor for primitives.
			primitiveEditor.setPreferredSize(new Dimension(300,350));
			actionWindow.add(primitiveEditor,BorderLayout.CENTER);						
			recolor(primitiveEditor);			

			// Show the adder for primitives.
			recolor(addPrimitive);
			actionWindow.add(addPrimitive,BorderLayout.SOUTH);

			if(attachment != null) {
				recolor(attachment);
				actionWindow.add(attachment,BorderLayout.SOUTH);
			}
			
	}

	public void nextWord() {
		word = iomanager.loadWord(credentials.userID,gameType);
	}
	
	public boolean wordComplete() {
		return iomanager.saveModel(model, word.id, credentials.userID, credentials.password, gameType);
	}
	
	public boolean isComplete() {
		return true;
	}
	
	/**
	 * Set the background to black and the foreground to white recursively through children.
	 * @param c Starting container.
	 */
	public static void recolor(Container c) {
		
		Color back = c.getBackground();
		
		if(back.getRed() == 238 && back.getGreen() == 238 && back.getBlue() == 238) {
			c.setBackground(Color.BLACK);
			c.setForeground(Color.WHITE);			
		} 
		
		for(Component c2 : c.getComponents()) {
			if(c2 instanceof Container) {
				recolor((Container)c2);
			}
		}
	}

	public void actionComplete() {	
		// Now that you have completed a model, handle completion.
		if(wordComplete()) {
			
			// Get new word.
			nextWord();
			
			// Show new word.
			if(word != null) {
				label.setText("Your word is: " + word.description);
			} else {
				label.setText("Error loading word.");
			}
	
			model.clear();
			
			repaint();
		}
	}
	
}
