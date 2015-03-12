package admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;

import creator2DNoTree.Model2DNoTree;
import creator2DNoTree.WindowRender2DNoTree;
import creator2DTree.Model2DTree;
import creator2DTree.WindowRender2DTree;
import creator3DTree.Model3DTree;
import creator3DTree.WindowRender3DTree;

import templates.CreatorPanel;

/**
 * Visualize results.
 * @author bkievitk
 */

public class SearchGeonModels extends JPanel {
	
	private static final long serialVersionUID = -4499911398672502656L;
	
	// Data for all models found.
	private Vector<ModelData> allModels;
	
	private JComboBox models = new JComboBox();
	private Container container;
	
	private JTextField player = new JTextField();
	private JTextField wordID = new JTextField();
	private JTextField modelID = new JTextField();
	private JTextField gameType = new JTextField();
	private boolean inhibit = false;
	private JPanel search;
		
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new SearchGeonModels());
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static Vector<String> splitCSVLine(String line) {
		Vector<String> ret = new Vector<String>();
		int index = 0;
		while(true) {
			int c1 = line.indexOf(',',index);
			int c2 = line.indexOf('\'',index);
			
			if(c1 < 0) {
				ret.add(line.substring(index));
				return ret;
			}
			
			if(c2 >= 0 && c2 < c1) {
				int c3 = line.indexOf('\'',c2+1);
				ret.add(line.substring(index,c3+1));
				index = c3+2;
			} else {
				ret.add(line.substring(index,c1));
				index = c1+1;
			}
		}
	}
	
	public void rebuildList() {
		if(models != null) {
			inhibit = true;
			models.removeAllItems();
			for(ModelData model : allModels) {
				
				if(	(player.getText().length() == 0 || player.getText().equals(model.playerName)) &&
					(wordID.getText().length() == 0 || wordID.getText().equals(model.wordID+"")) &&
					(modelID.getText().length() == 0 || modelID.getText().equals(model.modelID+"")) &&
					(gameType.getText().length() == 0 || gameType.getText().equals(model.gameType+""))
					) {
					models.addItem(model);
				}
			}
			inhibit = false;
		}
	}
	
	public SearchGeonModels() {
		

		System.out.println("Retrieving all models.");
		allModels = ModelManager.getAllModels();
		for(ModelData model : allModels) {
			System.out.println(model);
			models.addItem(model);
		}
		
		container = this;//.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(models,BorderLayout.NORTH);
		
		search = new JPanel(new GridLayout(4,1));
		search.add(CreatorPanel.labeledPanel(player, "player"));
		search.add(CreatorPanel.labeledPanel(wordID, "wordID"));
		search.add(CreatorPanel.labeledPanel(modelID, "modelID"));
		search.add(CreatorPanel.labeledPanel(gameType, "gameType"));

		player.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rebuildList();
			}
		});
		
		wordID.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rebuildList();
			}
		});

		modelID.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rebuildList();
			}
		});
		
		gameType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rebuildList();
			}
		});
		
		container.add(search,BorderLayout.SOUTH);
		
		models.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!inhibit) {
					ModelData model = (ModelData)models.getSelectedItem();
					container.removeAll();
					
					if(model.gameType.equals(main.MainApplet.GAME_2D_NO_TREE)) {
						container.add(new WindowRender2DNoTree((Model2DNoTree)model.model),BorderLayout.CENTER);
					} else if(model.gameType.equals(main.MainApplet.GAME_2D_TREE)) {
						container.add(new WindowRender2DTree((Model2DTree)model.model),BorderLayout.CENTER);
					} else if(model.gameType.equals(main.MainApplet.GAME_3D_TREE)) {
						container.add(new WindowRender3DTree((Model3DTree)model.model),BorderLayout.CENTER);
					} else if(model.gameType.equals(main.MainApplet.GAME_FEATURE)) {
						container.add(null,BorderLayout.CENTER);
					}
					
					container.add(models,BorderLayout.NORTH);
					container.add(search,BorderLayout.SOUTH);
					invalidate();
					validate();
				}
			}
		});
		
	}
}
