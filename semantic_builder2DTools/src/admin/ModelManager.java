package admin;

import iomanager.IOGradPrimaryData;
import iomanager.IOWeb;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import templates.ImageViewer;
import templates.WindowRender;
import tools.ImageFrame;

//import mdsj.MDSJ;
import modelTools.GeonModel;
import comparison.tree2D.*;
import creator2DTree.Model2DTree;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class ModelManager {

	public static final int ORDER_CREATOR = 0;
	public static final int ORDER_MODEL_TYPE = 1;

	public static String adminPassword = "";
	
	public static void main(String[] args) {
		Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
		refineModels(models);
	}
	
	public static void tmp2() {
		final Vector<ModelData> models = ModelManager.getAllModels(new File("4-9-2012.dat"));
		refineModels(models);
		Hashtable<String,Vector<ModelData>> modelSort = ModelManager.getAllWordModels(models);
		
		File f = new File("../visualization_wordSimilarities/sp/w2wImgs2");
		for(File f2 : f.listFiles()) {
			String fileName = f2.getName();
			fileName = fileName.substring(0,fileName.length() - 4).trim();
			modelSort.remove(fileName);
		}

		for(String s : modelSort.keySet()) {

			final JFrame frame = new JFrame();
			
			final String sf = s;
			JTabbedPane panel = new JTabbedPane();
			Vector<ModelData> modelsIn = modelSort.get(s);
			panel.add(new JLabel(s));
			for(ModelData model : modelsIn) {
				final ModelData modelF = model;
				JPanel inner = new JPanel() {
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						BufferedImage back = new BufferedImage(400,400,BufferedImage.TYPE_INT_ARGB);
						BufferedImage image = modelF.model.thumbnail(back, 400, 400, 10);
						g.drawImage(image, 0, 0,this);
					}
				};
				inner.setPreferredSize(new Dimension(400,400));
				panel.add(inner);
				
				inner.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent arg0) {
						BufferedImage back = new BufferedImage(400,400,BufferedImage.TYPE_INT_ARGB);
						BufferedImage image = modelF.model.thumbnail(back, 400, 400, 10);
						try {
							ImageIO.write(image, "png", new File("../visualization_wordSimilarities/sp/w2wImgs2/" + sf + ".png"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						frame.dispose();
					}
					public void mouseEntered(MouseEvent arg0) {}
					public void mouseExited(MouseEvent arg0) {}
					public void mousePressed(MouseEvent arg0) {}
					public void mouseReleased(MouseEvent arg0) {}
				});
			}
			
			frame.setSize(400, 400);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(new JScrollPane(panel));
			frame.setVisible(true);
		}
	}
	
	public static void showModel(int modelID) {
		try {
			URL url = new URL("http://www.indiana.edu/~semantic/io/adminGetObject.php?adminPassword=" + adminPassword + "&modelID=" + modelID);
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			ModelData md = new ModelData(r.readLine());
			md.buildModel();
			ImageFrame.makeFrame(md.model.thumbnail(null, 400, 400, 10));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void tmp() {
		
		final JCalendar start = new JCalendar();
		final JCalendar stop = new JCalendar();
		JButton show = new JButton("show");
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(start, BorderLayout.WEST);
		panel.add(stop, BorderLayout.EAST);
		panel.add(show, BorderLayout.CENTER);
		
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(500,100));
		frame.setVisible(true);
		
		final Vector<ModelData> models = ModelManager.getAllModels((File)null);
		refineModels(models);
		
		HashSet<String> types = new HashSet<String>();
		types.add(main.MainApplet.GAME_MECHANICAL_TURK_1);
		HumanLabeler.selectModelType(models, types);
		
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");					
					ModelManager.showAll(HumanLabeler.duplicateDateRange(models,start.c.getTime(), stop.c.getTime()), ORDER_CREATOR);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
		
	}
	
	public static void refineModels(Vector<ModelData> models) {
		HashSet<String> types = new HashSet<String>();
		types.add(main.MainApplet.GAME_2D_TREE + ":build");
		types.add(main.MainApplet.GAME_SUBJECT_POOL_1 + ":build");
		types.add(main.MainApplet.GAME_SUBJECT_POOL_1);
		types.add(main.MainApplet.GAME_SUBJECT_POOL_2);
		types.add(main.MainApplet.GAME_MECHANICAL_TURK_1);
		types.add(main.MainApplet.GAME_MECHANICAL_TURK_2);
		types.add("1");
		types.add("2DTree");
				
		HumanLabeler.selectModelType(models, types);

		for(int i=0;i<models.size();i++) {
			models.get(i).buildModel();
			models.get(i).word = models.get(i).word.trim().toLowerCase();
		}
		
		HumanLabeler.removeEmptyModels(models);
	}
	
	public static void similarityMatrix(Vector<ModelData> allModels, File outFile, Comparator2DTree metric, double[] weights) {
		// Similarity ratings.
		Hashtable<Integer,Vector<ModelData>> modelsIDSorted = getAllWordIDModels(allModels);
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(outFile));
		
			w.write("'Builder2D'\n");
			
			w.write("'CONCEPT'");
			for(Vector<ModelData> modelType : modelsIDSorted.values()) {
				w.write(",'" + modelType.get(0).word + "'");
			}
			w.write("\n");
	
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for(Vector<ModelData> modelType1 : modelsIDSorted.values()) {
				w.write("'" + modelType1.get(0).word + "'");
				for(Vector<ModelData> modelType2 : modelsIDSorted.values()) {
					if(modelType1 == modelType2) {
						w.write(",'1.0'");
					} else {
						double similarity = getSimilarity(modelType1, modelType2, metric, weights);
						w.write(",'" + similarity + "'");
						min = Math.min(min, similarity);
						max = Math.max(max, similarity);
					}
				}
				w.write("\n");
			}
			
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate average similarity.
	 * @param modelType1
	 * @param modelType2
	 * @return
	 */
	public static double getSimilarity(Vector<ModelData> modelType1, Vector<ModelData> modelType2, Comparator2DTree metric, double[] weights) {
		
		double sum = 0;
		int count = 0;
				
		for(ModelData m1 : modelType1) {
			for(ModelData m2 : modelType2) {
				if(m1 != m2) {
					sum += metric.similarity((Model2DTree)m1.model, (Model2DTree)m2.model, weights);
					count++;
				}
			}
		}
		
		return sum / count;
	}

	public static void writeSampleImages(final Vector<ModelData> models, File dir, int size) {
		for(ModelData model : models) {
			
			//BufferedImage background = new BufferedImage(size,size,BufferedImage.TYPE_4BYTE_ABGR);
			BufferedImage image = model.model.thumbnail(null, size, size, 10);
			try {
				ImageIO.write(image, "png", new File(dir,model.word + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Render all models.
	 * @param models
	 */
	public static void showAll(final Vector<ModelData> models, int orderBy) {
		Hashtable<String,JPanel> verticalPanels = new Hashtable<String,JPanel>();
		final JPanel allPanels = new JPanel(new GridLayout(1,0));
		
		for(ModelData model : models) {
			
			String panelName = "";
			
			if(orderBy == ORDER_CREATOR) {
				panelName = model.playerName;
			} else if(orderBy == ORDER_MODEL_TYPE) {
				panelName = model.word;
			} else {
				System.out.println("Invalid ordering");
				return;
			}

			//System.out.println(panelName);
			//System.out.println(verticalPanels);
			
			JPanel verticalPanel = verticalPanels.get(panelName);		
			
			if(verticalPanel == null) {
				verticalPanel = new JPanel(new GridLayout(50,1));
				verticalPanel.add(new JLabel(panelName));
				verticalPanels.put(panelName, verticalPanel);
			}
			
			ImageViewer img = null;
			
			BufferedImage image = null;
			if(model.model != null) {
				image = model.model.thumbnail(null, 100, 100, 5);
	            Graphics g = image.getGraphics();
	            g.setColor(Color.BLACK);
	            g.drawRect(0,0,99,99);
			}
			
    		img = new ImageViewer(image,false);
    		img.setMinimumSize(new Dimension(105,105));
    		img.setMaximumSize(new Dimension(105,105));
    		img.setPreferredSize(new Dimension(105,105));

    		JPanel labelPanel = new JPanel(new BorderLayout());
    		labelPanel.add(img,BorderLayout.CENTER);
    		
    		
    		String modelLabel = "";
    		if(orderBy == ORDER_CREATOR) {
    			modelLabel = model.word + " " + model.time;
			} else if(orderBy == ORDER_MODEL_TYPE) {
				modelLabel = model.playerName + " " + model.time;
			}
    		
    		labelPanel.add(new JLabel(modelLabel),BorderLayout.SOUTH);
    		
			verticalPanel.add(labelPanel);
			
    		final ModelData finalModel = model;
    		final JPanel playerPanelFinal = verticalPanel;
    		final JPanel labelPanelFinal = labelPanel;
    		
    		labelPanelFinal.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent arg0) {}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {
					models.remove(finalModel);
					playerPanelFinal.remove(labelPanelFinal);
					playerPanelFinal.invalidate();
					playerPanelFinal.validate();
					playerPanelFinal.repaint();
				}
				public void mouseReleased(MouseEvent arg0) {}
    		});
		}
		
		for(JPanel panel : verticalPanels.values()) {
			allPanels.add(panel);
		}
		JScrollPane scrollPane = new JScrollPane(allPanels);
		
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400,400);
        frame.add(scrollPane);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					BufferedWriter write = new BufferedWriter(new FileWriter(new File("goodModels.dat")));
					for(ModelData model : models) {
						write.write(model.line + "\n");
					}
					write.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        });
        menu.add(save);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        
        frame.setVisible(true);
        
	}
	
	public static void getTypicality(Comparator2DTree metric, Vector<ModelData> allModels, double[] weights) {
		
		Hashtable<Integer,Vector<ModelData>> modelsGrouped = getAllWordIDModels(allModels);
		
		double[][] valuess = new double[72][];
		
		for(Integer wordID : modelsGrouped.keySet()) {
			Vector<ModelData> models = modelsGrouped.get(wordID);
			
			// Compare every model against every model.
			double sum = 0;
			double sumSqr = 0;
			int count = 0;
			for(ModelData model1 : models) {
				for(ModelData model2 : models) {
					if(model1 != model2) {
						double sim = metric.similarity((Model2DTree)model1.model, (Model2DTree)model2.model, weights);
						sum += sim;
						sumSqr += sim * sim;
						count ++;
					}
				}
			}
			
			if(count > 0) {
								
				int categoryID = wordID % 6;
				int wordInCategory = wordID / 6;
				int typicalityRating = wordInCategory / 4;
					
				double[] values = {categoryID,typicalityRating,count,sum,sumSqr};
				valuess[wordID] = values;
			}
		}
		
		Comparator<double[]> compTypicality = new Comparator<double[]>() {
			public int compare(double[] arg0, double[] arg1) {
				if(arg0[1] > arg1[1]) {
					return 1;
				} else if(arg0[1] < arg1[1]) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		
		Comparator<double[]> compCategory = new Comparator<double[]>() {
			public int compare(double[] arg0, double[] arg1) {
				if(arg0[0] > arg1[0]) {
					return 1;
				} else if(arg0[0] < arg1[0]) {
					return -1;
				} else {
					return 0;
				}
			}
		};

		Arrays.sort(valuess, compTypicality);
		Arrays.sort(valuess, compCategory);
		
		System.out.println("category,typicalityRating,count,sum,sumSquared");
		for(double[] values : valuess) {
			System.out.println(values[0] + "," + values[1] + "," + values[2] + "," + values[3] + "," + values[4]);
		}
	}
	
	public static double withinBetween(Comparator2DTree metric, Vector<ModelData> allModels, double[] weights) {
		Hashtable<Integer,Vector<ModelData>> modelsWordGrouped = getAllWordIDModels(allModels);
		Hashtable<String,Vector<ModelData>> modelsSubjectGrouped = getAllUserModels(allModels);
		return withinBetween(metric, allModels, weights, modelsWordGrouped, modelsSubjectGrouped);
	}
	
	public static double withinBetween(Comparator2DTree metric, Vector<ModelData> allModels, double[] weights, 
			Hashtable<Integer,Vector<ModelData>> modelsWordGrouped, 
			Hashtable<String,Vector<ModelData>> modelsSubjectGrouped) {

		double sum = 0;
		int count = 0;
		
		for(Integer wordID : modelsWordGrouped.keySet()) {
			Vector<ModelData> models = modelsWordGrouped.get(wordID);
			for(ModelData m1 : models) {
				for(ModelData m2 : models) {
					if(m1 != m2) {
						double sim = metric.similarity((Model2DTree)m1.model, (Model2DTree)m2.model, weights);
						sum += sim;			
						count ++;
					}
				}
			}
		}
		
		double sum2 = 0;
		double count2 = 0;
		
		for(String subject : modelsSubjectGrouped.keySet()) {
			Vector<ModelData> models = modelsSubjectGrouped.get(subject);
			for(ModelData m1 : models) {
				for(ModelData m2 : models) {
					if(m1 != m2) {
						double sim = metric.similarity((Model2DTree)m1.model, (Model2DTree)m2.model, weights);
						sum2 += sim;
						count2 ++;
					}
				}
			}
		}
		
		double avg = (sum / count);
		double avg2 = (sum2 / count2);
		
		double score = avg / (avg + avg2);
		
		// The higher the better.
		return score;
	}

	public static void mds(int type, int keepID, File outputFile, boolean drawImgs, double[] weights) {
		/*
		Vector<ModelData> allModels = getAllModels();
		
		Vector<ModelData> models = new Vector<ModelData>();
		for(ModelData model : allModels) {
			int wordID = model.wordID;
			//int categoryID = wordID % 6;
			int wordInCategory = wordID / 6;
			int typicalityRating = wordInCategory / 4;
			
			if(	(keepID == 0) ||
				(keepID == 1 && typicalityRating == 0) ||
				(keepID == 2 && wordInCategory == 0) ||
				(keepID == 3 && typicalityRating == 1) ||
				(keepID == 4 && typicalityRating == 2)) {
				models.add(model);
			}
		}

		double[][] sims = new double[models.size()][models.size()];
		for(int i=0;i<models.size();i++) {
			GeonModel m1 = models.get(i).model;
			for(int j=0;j<models.size();j++) {
				GeonModel m2 = models.get(j).model;
				double sim = Tree2DComparator.modelSimilarityMetric((Model2DTree)m1, (Model2DTree)m2, type, weights);
				sims[i][j] = 1 - sim;
			}
		}
		
		double[][] locations = MDSJ.stressMinimization(sims);
		//double[][] locations = MDSJ.classicalScaling(sims);
		
		BufferedImage img = new BufferedImage(1200,1200,BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0,0,img.getWidth(),img.getHeight());
				
		Color[] colors = new Color[IOGradPrimaryData.wordSet.length];
		//Random rand = new Random();
		
		Color[] basis = {Color.RED,Color.GREEN,Color.BLUE,Color.ORANGE,Color.CYAN,Color.GRAY};
		for(int id=0;id<colors.length;id++) {
			
			int categoryID = id % 6;
			int wordInCategory = id / 6;
			//int typicalityRating = wordInCategory / 4;
			
			Color base = basis[categoryID];
			double scale = (wordInCategory + 4) / 8.0;
			int red = (int)(base.getRed() * scale);
			int green = (int)(base.getGreen() * scale);
			int blue = (int)(base.getBlue() * scale);

			red = Math.min(255, Math.max(0,red));
			green = Math.min(255, Math.max(0,green));
			blue = Math.min(255, Math.max(0,blue));
			
			Color newColor = new Color(red,green,blue);
			colors[id] = newColor;
		}
		
		for(int i=0;i<models.size();i++) {
			int x = (int)(locations[0][i] * img.getWidth() + img.getWidth() / 2);
			int y = (int)(locations[1][i] * img.getHeight() + img.getHeight() / 2);

			if(drawImgs) {
				BufferedImage thumb = models.get(i).model.thumbnail(null, 50, 50, 5);
				g.drawImage(thumb, x-thumb.getWidth()/2, y-thumb.getHeight()/2, null);
			} else {
				g.setColor(colors[models.get(i).wordID]);
							
				g.fillOval(x-3,y-3,6,6);
				
				String str = IOGradPrimaryData.wordSet[models.get(i).wordID];
				str = str.split("-")[1].trim();
				g.drawString(str, x+5, y+5);
			}
		}
		
		try {
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	
	public static void showComparisons(Hashtable<String,Vector<ModelData>> models, int objectID, String playerID, Comparator2DTree metric, double[] weights) {
		
		Vector<ModelData> modelSet = models.get(playerID);
		double sumSimilarity = 0;
		int count = 0;
				
		System.out.print(objectID + " & ");
/*		System.out.println("Always borrow~~~~   &   96 - 42 = 34 \\\\");
		System.out.println("0 - N = N           &   70 - 47 = 37 \\\\");
		System.out.println("0 - N = 0           &   70 - 47 = 30 \\\\");*/
		
		for(int i=0;i<modelSet.size();i++) {
			if(objectID != i) {
				GeonModel m1 = modelSet.get(objectID).model;
				GeonModel m2 = modelSet.get(i).model;
				if(m1 instanceof Model2DTree && m2 instanceof Model2DTree) {
					sumSimilarity += metric.similarity((Model2DTree)m1, (Model2DTree)m2, weights);
					count ++;
				}
			}
		}
		
		System.out.print((sumSimilarity / (modelSet.size()-1)) + " & ");
		//System.out.println("Average similarity across = " + (sumSimilarity / (modelSet.size()-1)));
		
		sumSimilarity = 0;
		count = 0;
		
		for(Vector<ModelData> userModels : models.values()) {
			if(modelSet != userModels && userModels.size() > objectID) {
				GeonModel m1 = modelSet.get(objectID).model;
				GeonModel m2 = userModels.get(objectID).model;
				
				if(m1 instanceof Model2DTree && m2 instanceof Model2DTree) {
					sumSimilarity += metric.similarity((Model2DTree)m1, (Model2DTree)m2, weights);
					count ++;
				}
			}
		}
		
		//System.out.println("Average similarity within = " + (sumSimilarity / count));
		System.out.println((sumSimilarity / count) + " ////");
		//System.out.println();
	}
	
	
	public static void showGrid(Vector<ModelData> models) {
				
		Hashtable<String,JPanel> userPanels = new Hashtable<String,JPanel>();
		
		for(ModelData model : models) {
			if(model.gameType.equals(main.MainApplet.GAME_2D_TREE)) {
				String player = model.playerName;
				JPanel playerPanel = userPanels.get(player);
				if(playerPanel == null) {
					playerPanel = new JPanel(new GridLayout(73,1));
					playerPanel.add(new JLabel(player));
					userPanels.put(player, playerPanel);
				}
				
				ImageViewer img = null;
	            BufferedImage image = model.model.thumbnail(null, 100, 100, 5);
	            Graphics g = image.getGraphics();
	            g.setColor(Color.BLACK);
	            g.drawRect(0,0,99,99);
        		img = new ImageViewer(image,false);
        		img.setMinimumSize(new Dimension(105,105));
        		img.setMaximumSize(new Dimension(105,105));
        		img.setPreferredSize(new Dimension(105,105));
	            
				playerPanel.add(img);
			}
		}
		
		JPanel allUsers = new JPanel(new GridLayout(1,userPanels.size()+1));
		JPanel objects = new JPanel(new GridLayout(73,1));
		objects.add(new JLabel(""));
		for(int i=0;i<72;i++) {
			JLabel label = new JLabel(IOGradPrimaryData.wordSet[i]);
			label.setMinimumSize(new Dimension(105,105));
			label.setMaximumSize(new Dimension(105,105));
			label.setPreferredSize(new Dimension(105,105));
			objects.add(label);
		}
		allUsers.add(objects);
		
		for(JPanel panel : userPanels.values()) {
			allUsers.add(panel);
		}
		JScrollPane scrollPane = new JScrollPane(allUsers);
        
		
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400,400);
        frame.add(scrollPane);
        frame.setVisible(true);
        
	}
	

	public static Hashtable<String,Vector<ModelData>> getAllUserModels() {
		Vector<ModelData> models = getAllModels();
		Hashtable<String,Vector<ModelData>> ret = new Hashtable<String,Vector<ModelData>>();
		for(ModelData model : models) {
			Vector<ModelData> player = ret.get(model.playerName);
			if(player == null) {
				player = new Vector<ModelData>();
				ret.put(model.playerName, player);
			}
			player.add(model);
		}
		return ret;
	}
	
	public static Hashtable<String,Vector<ModelData>> getAllUserModels(Vector<ModelData> models) {
		Hashtable<String,Vector<ModelData>> ret = new Hashtable<String,Vector<ModelData>>();
		int count = 0;
		for(ModelData model : models) {

			String playerName = model.playerName;
			if(model.playerName == null) {
				playerName = "";
				count++;
			}
			
			Vector<ModelData> player = ret.get(playerName);
			if(player == null) {
				player = new Vector<ModelData>();
				ret.put(playerName, player);
			}
			player.add(model);
		}
		
		System.out.println(count + " models out of " + models.size() + " do not have a player name.");
		return ret;
	}
	
	public static Hashtable<Integer,Vector<ModelData>> getAllWordIDModels(Vector<ModelData> models) {
		Hashtable<Integer,Vector<ModelData>> ret = new Hashtable<Integer,Vector<ModelData>>();
		for(ModelData model : models) {
			Vector<ModelData> wordID = ret.get(model.wordID);
			if(wordID == null) {
				wordID = new Vector<ModelData>();
				ret.put(model.wordID, wordID);
			}
			wordID.add(model);
		}
		return ret;
	}
	
	public static Hashtable<String,Vector<ModelData>> getAllWordModels(Vector<ModelData> models) {
		Hashtable<String,Vector<ModelData>> ret = new Hashtable<String,Vector<ModelData>>();
		for(ModelData model : models) {
			Vector<ModelData> wordID = ret.get(model.word);
			if(wordID == null) {
				wordID = new Vector<ModelData>();
				ret.put(model.word, wordID);
			}
			wordID.add(model);
		}
		return ret;
	}
	
	public static Vector<ModelData> getAllModels() {
		return getAllModels(new File("models.dat"));
	}
		
	/**
	 * Attempt to load all models, first from file, then from the web.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Vector<ModelData> getAllModels(File file) {
		
		// Read from file.
		try {
			if(file != null && file.exists()) {
				
				FileInputStream fis = new FileInputStream(file);
				boolean isSerialized = fis.read() == 172 && fis.read() == 237;
				fis.close();

				if(isSerialized) {
					System.out.println("Reading model data from file as serialized data " + file);
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
					Vector<ModelData> models = (Vector<ModelData>)ois.readObject();
					ois.close();
					return models;
				} else {
					System.out.println("Reading model data from file as ASCII line data " + file);
					return getAllModels(new BufferedReader(new FileReader(file)));
				}
				
			}
		} catch(IOException e) {
			System.out.println("Failed to read model data from file.");
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to read model data from file due to file not being serialized correctly.");
		}

		// Read from web.
		try {
			
			System.out.println("Reading model data from web.");
			
			// Connect to website.
            URL url = new URL(IOWeb.webHostIO + "adminGetObjects.php?adminPassword=" + adminPassword);
            URLConnection connection = url.openConnection();

            // Read data.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            
            Vector<ModelData> models = getAllModels(in);
            try {
            	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            	out.writeObject(models);
            	out.close();
            } catch(IOException e) {
            	e.printStackTrace();
            }
            
            return models;
		} catch(IOException e) {
			System.out.println("Failed to read model data from web.");
			e.printStackTrace();
			return null;
		}
	}
    
    public static Vector<ModelData> getAllModels(BufferedReader in) throws IOException {
    	Vector<ModelData> models = new Vector<ModelData>();
        
        String line;
        
        while((line = in.readLine()) != null) {
        	models.add(new ModelData(line));
        }  
        
        return models;
	}
	
	public static void showList() {
		try {
			// Connect to website.
            URL url = new URL(IOWeb.webHostIO + "adminGetObjects.php?adminPassword=" + adminPassword);
            URLConnection connection = url.openConnection();

            // Read data.
            InputStream stream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line;
            

            Box list = Box.createVerticalBox();
            JScrollPane scrollPane = new JScrollPane(list);
            
            while((line = in.readLine()) != null) {
            	Box row = Box.createHorizontalBox();
            	row.setAlignmentX(Box.LEFT_ALIGNMENT);
            	row.setPreferredSize(new Dimension(800,200));
            	row.setBackground(Color.RED);
            	           
            	ModelData model = new ModelData(line);
            	
            	/*
            	String[] parts = line.split("~");
            	int modelID = Integer.parseInt(parts[0]);
            	int word = Integer.parseInt(parts[1]);
            	String player = parts[2];
            	String representation = parts[3];
            	int timesSolved = Integer.parseInt(parts[4]);
            	int gameType = Integer.parseInt(parts[5]);
            	*/
            	
            	JButton select = new JButton("select");
            	
            	JLabel spacer = new JLabel("");
	            	spacer.setMinimumSize(new Dimension(10,20));
	            	spacer.setMaximumSize(new Dimension(10,20));            	
            	
            	JLabel modelLabel = new JLabel(model.modelID + "");
            		modelLabel.setMinimumSize(new Dimension(25,20));
            		modelLabel.setMaximumSize(new Dimension(25,20));
            	                	
                JLabel wordLabel;
                
                if(model.gameType.equals(main.MainApplet.GAME_SUBJECT_POOL_1)) {
                	wordLabel = new JLabel(IOGradPrimaryData.wordSet[model.wordID]);
                } else {
                	wordLabel = new JLabel(model.wordID + "");
                }
    			wordLabel.setMinimumSize(new Dimension(150,20));
    			wordLabel.setMaximumSize(new Dimension(150,20));
            	
            	JLabel playerLabel = new JLabel(model.playerName);
	            	playerLabel.setMinimumSize(new Dimension(100,20));
	            	playerLabel.setMaximumSize(new Dimension(100,20));
    			
            	JLabel representationLabel = new JLabel(model.representation + "");
	            	representationLabel.setMinimumSize(new Dimension(100,20));
	            	representationLabel.setMaximumSize(new Dimension(100,20));
            	
            	JLabel timesSolvedLabel = new JLabel(model.timesSolved + "");
	            	timesSolvedLabel.setMinimumSize(new Dimension(25,20));
	            	timesSolvedLabel.setMaximumSize(new Dimension(25,20));
            	
            	JLabel gameLabel = new JLabel(model.gameType + "");
	            	gameLabel.setMinimumSize(new Dimension(25,20));
	            	gameLabel.setMaximumSize(new Dimension(25,20));

	            ImageViewer img = null;
	            BufferedImage image;
	            
	            if(model.model != null) {
	            	image = model.model.thumbnail(null, 100, 100, 5);
	            } else {
	            	image = WindowRender.makeBackground(100,100);
	            }
	            
	            Graphics g = image.getGraphics();
	            
        		/*
	            switch(gameType) {
	            	case CreatorPanel.GAME_2D_NO_TREE:
	            		Model2DNoTree model2DNoTree = new Model2DNoTree();
	            		model2DNoTree.fromReduced(representation);
	            		for(PrimitiveInstance2DNoTree object : model2DNoTree.objects) {
	            			object.render(g, model2DNoTree.getSelected());
	            		}
	            	break;
	            	case 5:
	            	case CreatorPanel.GAME_2D_TREE:

	            		Model2DTree model2DTree = new Model2DTree();
	            		model2DTree.fromReduced(representation);
	            		
	            		
	            		// Create root transform to be centered and scaled by 20.
	            		AffineTransform rootTransform = new AffineTransform();
	            		rootTransform.translate(image.getWidth()/2,image.getHeight()/2);
	            		rootTransform.scale(10, 10);
            			rootTransform.rotate(model2DTree.root.rotation[0]/100.0);
	            			
            			// Render.
            			model2DTree.root.render(g, rootTransform,model2DTree.getSelected(),false);
            		break;
	            }
				*/
        		
        		
	            g.setColor(Color.BLACK);
	            g.drawRect(0,0,99,99);
	            
        		img = new ImageViewer(image,true);
        		img.setMinimumSize(new Dimension(105,105));
        		img.setMaximumSize(new Dimension(105,105));
        		
	            row.add(select);
            	row.add(spacer);
            	row.add(modelLabel);
            	row.add(wordLabel);
            	row.add(playerLabel);
            	row.add(representationLabel);
            	row.add(spacer);
            	row.add(timesSolvedLabel);
            	row.add(gameLabel);
            	row.add(img);
            	list.add(row);

            }
            
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(400,400);
            frame.add(scrollPane);
            frame.setVisible(true);

        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
}
