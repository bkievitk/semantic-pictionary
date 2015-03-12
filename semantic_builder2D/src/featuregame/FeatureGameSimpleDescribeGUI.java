package featuregame;

import iomanager.IOWeb;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import templates.PanelLoginCallback;
import tools.Lexicon;
import tools.UserCredentials;

public class FeatureGameSimpleDescribeGUI extends JPanel implements PanelLoginCallback {

	private static final long serialVersionUID = 7040988303352069711L;
	
	private UserCredentials credentials;
	private Lexicon lexicon;

	private JLabel conceptLabel;
	private String concept;
	private JTextField[] features;
	private long[] featureTimes;
	private long lastTime;
	private JList selectionBox = new JList();
	private DefaultListModel listModel = new DefaultListModel();
	private int featureOn = 0;
	private int roundID = 1;
	
	public static void main(String args[]) {
		FeatureGameSimpleDescribeGUI gui = new FeatureGameSimpleDescribeGUI(null);		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800,600);
		frame.add(gui);
	    frame.setVisible(true);	     
	}
	 
	public FeatureGameSimpleDescribeGUI(UserCredentials credentials) {
		this.credentials = credentials;
		
		try {
			lexicon = new Lexicon();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout());
				
		concept = lexicon.getRandomConcept();
		conceptLabel = new JLabel("describe round " + roundID + "/20: " + concept);
		add(conceptLabel, BorderLayout.NORTH);

		JPanel featurePanel = new JPanel(new GridLayout(0,1,5,5));
		featurePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(featurePanel, BorderLayout.CENTER);
		
		features = new JTextField[10];
		featureTimes = new long[10];
		for(int i=0;i<features.length;i++) {
			final JTextField feature = new JTextField();
			feature.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {}
				public void keyReleased(KeyEvent arg0) {
					
					// They hit the enter key.
					if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						featureEntered();
					} else if(arg0.getKeyCode() == KeyEvent.VK_DOWN) {
						selectionBox.setSelectedIndex(selectionBox.getSelectedIndex()+1);
					} else if(arg0.getKeyCode() == KeyEvent.VK_UP) {
						selectionBox.setSelectedIndex(selectionBox.getSelectedIndex()-1);
					} else {
						// They hit a different key.
						String text = feature.getText();
			            if (!text.equals("")) {
			            	updateListModel(text);
			            	if (selectionBox.getSelectedIndex() == -1) {
		            	        selectionBox.setSelectedIndex(0);
		            	    }
				        }
					}
					
					
				}
				public void keyTyped(KeyEvent arg0) {}
			});
			feature.setEditable(false);
			features[i] = feature;
			featurePanel.add(feature);
		}
		features[0].setEditable(true);
		
		selectionBox = new JList();
		selectionBox.setFixedCellHeight(14);
		selectionBox.setModel(listModel);
		selectionBox.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				featureEntered();
			}
		});
		add(selectionBox, BorderLayout.SOUTH);
		
		lastTime = System.currentTimeMillis();
	}
	
	public void featureEntered() {
		
		String selected = selectionBox.getSelectedValue().toString();
		
		// Make sure this has not already been entered.
		for(int i=0;i<featureOn;i++) {
			if(features[i].getText().equals(selected)) {
				return;
			}
		}		
		
		featureTimes[featureOn] = System.currentTimeMillis() - lastTime;
		features[featureOn].setEditable(false);
		features[featureOn].setText(selected);
		featureOn++;
		
		if(featureOn >= features.length) {
			// Finished.
			Vector<String> features = new Vector<String>();
			for(JTextField feature : this.features) {
				features.add(feature.getText());
			}
			
			Vector<Long> times = new Vector<Long>();
			for(long time : this.featureTimes) {
				times.add(time);
			}
			
			// Save
			IOWeb.saveFeatureDescribeRound(credentials.userID, credentials.password, concept, features, times);
			
		    // New concept			
			concept = lexicon.getRandomConcept();
			roundID++;
			conceptLabel.setText("describe round " + roundID + "/20: " + concept);
		    for(int i=0;i<this.features.length;i++) {
		    	this.features[i].setEditable(false);
		    	this.features[i].setText("");
		    }
		    featureOn = 0;
	    	this.features[featureOn].setEditable(true);
	    	this.features[featureOn].requestFocus();
			lastTime = System.currentTimeMillis();
			
		} else {
			features[featureOn].setEditable(true);
			features[featureOn].requestFocus();
		}
		listModel.clear();
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
