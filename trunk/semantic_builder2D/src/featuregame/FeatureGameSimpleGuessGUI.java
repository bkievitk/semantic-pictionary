package featuregame;

import iomanager.IOWeb;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import templates.PanelLoginCallback;
import tools.Lexicon;
import tools.UserCredentials;

public class FeatureGameSimpleGuessGUI extends JPanel implements PanelLoginCallback {

	private static final long serialVersionUID = -3522685811575131110L;
	private UserCredentials credentials;
	private Lexicon lexicon;

	private JTextField conceptLabel;
	private JLabel roundLabel;
	private String concept;
	private JLabel[] features;
	private JList selectionBox = new JList();
	private DefaultListModel listModel = new DefaultListModel();
	private int roundID = 1;
	
	public static void main(String args[]) {
		FeatureGameSimpleGuessGUI gui = new FeatureGameSimpleGuessGUI(new UserCredentials("test", "", -1));		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800,600);
		frame.add(gui);
	    frame.setVisible(true);	     
	}
	
	public void getGameSet() {
		try {
			String urlString = IOWeb.webHostIO + "getRndFeatures.php?playerID=" + credentials.userID;
			URL url = new URL(urlString);
	        URLConnection connection = url.openConnection();
	
	        // Read results.
	        InputStream stream = connection.getInputStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
	        String line = in.readLine();
	        String[] parts = line.split(",");
	        concept = parts[0];
	        for(int i=1;i<features.length;i++) {
	        	features[i].setText("");
	        }
	        
	        for(int i=1;i<parts.length;i++) {
	        	features[i-1].setText(parts[i]);
	        }
	        conceptLabel.setText("");
	        roundLabel = new JLabel("guess round " + roundID + "/20");
			roundID++;
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	 
	public FeatureGameSimpleGuessGUI(UserCredentials newCredentials) {
		this.credentials = newCredentials;
		
		try {
			lexicon = new Lexicon();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout());
				
		concept = lexicon.getRandomConcept();
		conceptLabel = new JTextField();
		conceptLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Vector<String> featureList = new Vector<String>();
				for(JLabel feature : features) {
					featureList.add(feature.getText());
				}
				IOWeb.saveFeatureGuessRound(credentials.userID, credentials.password, concept, conceptLabel.getText(), featureList);
				getGameSet();
			}
		});
		
		roundLabel = new JLabel("guess round " + roundID + "/20");
		add(roundLabel, BorderLayout.NORTH);
		
		JPanel featurePanel = new JPanel(new GridLayout(0,1,5,5));
		featurePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(featurePanel, BorderLayout.CENTER);
		
		featurePanel.add(conceptLabel);
		features = new JLabel[10];
		for(int i=0;i<features.length;i++) {
			features[i] = new JLabel("");
			featurePanel.add(features[i]);
		}
		getGameSet();
		
		selectionBox = new JList();
		selectionBox.setFixedCellHeight(14);
		selectionBox.setModel(listModel);
		selectionBox.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				
			}
		});
		add(selectionBox, BorderLayout.SOUTH);
	}
		
	public void updateListModel(String text) {
    	
    	// Clear old.
	    listModel.clear();
	    
	    if(text != null) {
		    // Add found features.
		    ArrayList<String> choices = lexicon.getMatchingFeatures(text, GameState.MAX_BOX_ITEMS);
		    for (String s : choices) {
		        listModel.addElement(s);
		    }
		    
		    // Add as new feature if no matches.
		    if (!choices.contains(text)) {
		        listModel.addElement(GameState.NEW_FEATURE_PREFIX + text + "]");
		    }
	    }
    }
	
	public void setPlayerInfo(UserCredentials credentials) {
		this.credentials = credentials;
	}

}
