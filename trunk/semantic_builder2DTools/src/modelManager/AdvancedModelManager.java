package modelManager;

import javax.swing.*;

import templates.ImageViewer;

import main.MainApplet;

import admin.JCalendar;
import admin.ModelData;
import admin.ModelManager;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.*;

public class AdvancedModelManager extends JPanel {
	private static final long serialVersionUID = -2695161900026433524L;
	
	private Vector<ModelData> models;
	private Vector<Guess> guesses;
	private Vector<ModelData> selectedModels;
	
	private JPanel selectPanel;
	private JTabbedPane displayPanelHolder;
	private JPanel displayPanelGrid;
	private JPanel displayPanelIndividual;
	private int modelID = 0;
	
	public static void main(String[] args) {
		new AdvancedModelManager();
	}
	
	public void buildSelectPanel() {

		// Load words.
		Vector<String> wordData = new Vector<String>();
		try {
			URL url = new URL("http://www.indiana.edu/~semantic/io/getAllWords.php");
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while((line = r.readLine()) != null) {
				wordData.add(line.split(",")[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Create input for words.
		final JList<String> words = new JList<String>(wordData);
		JScrollPane wordsScroller = new JScrollPane(words);
		wordsScroller.setPreferredSize(new Dimension(250, 80));
		wordsScroller.setBorder(BorderFactory.createTitledBorder("words"));

		// Start date.
		final JCalendar startDate = new JCalendar();
		startDate.c.set(2000, 1, 1);
		startDate.update();
		startDate.setBorder(BorderFactory.createTitledBorder("start date"));

		// End date.
		final JCalendar endDate = new JCalendar();
		endDate.setBorder(BorderFactory.createTitledBorder("end date"));
		
		// Search button.
		JButton search = new JButton("search");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				HashSet<String> wordSet = new HashSet<String>();
				for(String word : words.getSelectedValuesList()) {
					wordSet.add(word);					
				}

				selectedModels.clear();
				for(ModelData model : models) {
					if(wordSet.contains(model.word) && model.time.after(startDate.c.getTime()) && model.time.before(endDate.c.getTime())) {
						selectedModels.add(model);
					}
				}
				
				modelID = 0;
				updateGridDisplay();
				updateIndividualDisplay();
			}
		});

		selectPanel.add(wordsScroller, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new GridLayout(0,1));
		bottom.add(startDate);
		bottom.add(endDate);		
		bottom.add(search);		
		selectPanel.add(bottom, BorderLayout.SOUTH);
	}
	
	public void updateGridDisplay() {
		displayPanelGrid.removeAll();
		for(ModelData model : selectedModels) {
									
			ImageViewer img = null;
			
			BufferedImage image = null;
			if(model.model != null) {
				image = model.model.thumbnail(null, 100, 100, 5);
	            Graphics g = image.getGraphics();
	            g.setColor(Color.BLACK);
	            g.drawRect(0,0,99,99);

	            if(model.correctGuesses == 0 && model.incorrectGuesses == 0) {
	            	g.setColor(Color.BLACK);
	            } else if(model.correctGuesses > model.incorrectGuesses) {
	            	g.setColor(Color.GREEN);
	            } else {
	            	g.setColor(Color.RED);
	            }
	            g.drawString(model.correctGuesses + "/" + (model.correctGuesses + model.incorrectGuesses), 10, 22);
			}
			
    		img = new ImageViewer(image,false);
    		img.setMinimumSize(new Dimension(105,105));
    		img.setMaximumSize(new Dimension(105,105));
    		img.setPreferredSize(new Dimension(105,105));

    		displayPanelGrid.add(img);
		}

		displayPanelHolder.validate();
	}
	
	public void updateIndividualDisplay() {
				
		if(modelID >= 0 && modelID < selectedModels.size()) {
			ModelData model = selectedModels.get(modelID);
												
			ImageViewer img = null;
			
			BufferedImage image = null;
			if(model.model != null) {
				image = model.model.thumbnail(null, 500, 500, 25);
	            Graphics g = image.getGraphics();
	            g.setColor(Color.BLACK);
	            g.drawRect(0,0,499,499);
			}
			
			img = new ImageViewer(image,false);
			img.setMinimumSize(new Dimension(505,505));
			img.setMaximumSize(new Dimension(505,505));
			img.setPreferredSize(new Dimension(505,505));
	
			displayPanelIndividual.add(img, BorderLayout.CENTER);
			
			displayPanelHolder.validate();
		}
	}
	
	public AdvancedModelManager() {
		
		JTextArea text = new JTextArea();
		System.setOut(new PrintStream(new TextAreaOutputStream(text,"message")));

		JFrame frameMsg = new JFrame();
		frameMsg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frameMsg.add(new JScrollPane(text));
		frameMsg.setSize(400, 150);
		frameMsg.setVisible(true);
		
		// Load models.
		models = ModelManager.getAllModels(new File("9-25-13_models.dat"));
		ModelManager.refineModels(models);	
		
		guesses = Guess.getAllGuesses(new File("9-25-13_guesses.dat"));
		Guess.linkModels(models, guesses);
				
		selectedModels = new Vector<ModelData>();
		
		this.setLayout(new BorderLayout());
		
		selectPanel = new JPanel(new BorderLayout());
		displayPanelHolder = new JTabbedPane();		
		displayPanelGrid = new JPanel(new GridLayout(0,5));
		displayPanelIndividual = new JPanel(new BorderLayout());
		
		displayPanelHolder.addTab("grid", new JScrollPane(displayPanelGrid));
		displayPanelHolder.addTab("individual", displayPanelIndividual);
		
		buildSelectPanel();
		
		JButton left = new JButton("<");
		JButton right = new JButton(">");
		JPanel flipPanel = new JPanel(new BorderLayout());
		flipPanel.add(left, BorderLayout.WEST);
		flipPanel.add(right, BorderLayout.EAST);
		displayPanelIndividual.add(flipPanel, BorderLayout.SOUTH);
		
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelID = Math.max(0, modelID - 1);
				updateIndividualDisplay();
			}			
		});
		
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelID = Math.min(selectedModels.size() - 1, modelID + 1);
				updateIndividualDisplay();
			}			
		});
		
		add(selectPanel, BorderLayout.WEST);
		add(displayPanelHolder, BorderLayout.CENTER);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
