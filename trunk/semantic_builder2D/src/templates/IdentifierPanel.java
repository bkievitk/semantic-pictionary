package templates;

import iomanager.IOManager;
import iomanager.IOWeb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import modelTools.GeonModel;

import tools.JCompleteBox;
import tools.UserCredentials;
import tools.WordComplete;
import tools.WordPair;

public class IdentifierPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -3268963223189756341L;

	// Model of the object.
	public GeonModel model;
			
	// Game information.
	public UserCredentials credentials;
	public WordPair obj2 = null;
	public int modelID;

	// Confirmation.
	public JButton confirm;
	public JCompleteBox complete;
	
	public IOManager iomanager;
	public String gameType;
	public String gameSolveType;
	public UserMessagePanel messager;
	public JPanel guessPanel;
	public JComboBox directionality;
	
	public Hashtable<String,Integer> wordIDs;
	
	/**
	 * Clean up when you're done.
	 */
	public void destroy() {
		model = null;
	}

	/**
	 * Set everything up.
	 * @param playerID	ID on system.
	 * @param password	Password.
	 * @param applet	Used to load images if applicable.
	 */
	public IdentifierPanel(final UserCredentials credentials, Container container,
			WindowRender renderWindow,
			GeonModel model,
			UserMessagePanel messager,
			final IOManager iomanager,
			String gameType,
			String gameSolveType) {

		this.messager = messager;
		this.iomanager = iomanager;
		this.gameType = gameType;
		this.gameSolveType = gameSolveType;
		
		// Set password and player.
		this.credentials = credentials;
		
		// Set look and feel.
		// This forces consistency across OSs.
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");	
		} catch (Exception excep) {	System.out.println("Unable to set look and feel."); }
		
		// Get container to hold windows.
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);

		add(messager,BorderLayout.SOUTH);

		this.model = model;

		getObject();
		
		// This is where they place their guesses.
		guessPanel = new JPanel();
		guessPanel.setBackground(Color.BLACK);
		
		wordIDs = new Hashtable<String,Integer>();
		WordComplete database = IOWeb.getWords(wordIDs);
		
		complete = new JCompleteBox(database);
		confirm =  new JButton("Confirm");
		confirm.addActionListener(this);
		guessPanel.add(complete);
		guessPanel.add(confirm);

		complete.setBorder(BorderFactory.createTitledBorder(complete.getBorder(),"label"));
		
		String[] directions = {"","none","unknown","left","right"};
		directionality = new JComboBox(directions);
		directionality.setBorder(BorderFactory.createTitledBorder(directionality.getBorder(),"directionality"));
		//guessPanel.add(directionality);
		
		if(iomanager.isFlagModelSet()) {
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					iomanager.flagWord(credentials.userID, credentials.password, modelID);
					getObject();
				}			
			};
			guessPanel.add(new ConfirmationSet("Flag as Inappropriate",al));
			add(guessPanel,BorderLayout.NORTH);
		}
				
		// Build the rendering window.
		add(renderWindow,BorderLayout.CENTER);
		
		container.add(this);
	}

	public BufferedReader r = null;
	
	public void getObject() {
				
		modelID = iomanager.loadModel(model, credentials.userID, gameSolveType);
		
		if(modelID < 0) {
			messager.showMessage("No model was loaded.",UserMessage.ERROR);
		} else {
			model.selected = null;
			model.updateModel();
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == confirm) {
			
			String word = (String)complete.getSelectedItem();
			Integer wordID = wordIDs.get(word);
			
			if(wordID != null) {

				System.out.println("[" + gameType + "]");
				if(iomanager.saveWordGuess(credentials.userID, credentials.password, gameType, directionality.getSelectedIndex(), modelID, wordID)) {
					getObject();
				}	
			}
		}
	}	

}