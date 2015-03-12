package admin;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.swing.*;

import modelTools.GeonModel;
import modelTools.Primitive2D;
import creator2DTree.*;


public class HumanLabeler {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Vector<ModelData> models = ModelManager.getAllModels(new File("3-26-2012.dat"));
		
		HashSet<String> types = new HashSet<String>();
		types.add(main.MainApplet.GAME_2D_TREE + ":build");
		types.add(main.MainApplet.GAME_SUBJECT_POOL_1 + ":build");
		types.add(main.MainApplet.GAME_MECHANICAL_TURK_1);
		types.add("1");
		types.add("2DTree");
		
		//types.add(main.MainApplet.GAME_SUBJECT_POOL_1); // BAD WORD RECAL
		
		selectModelType(models, types);
		

		for(int i=0;i<models.size();i++) {
			models.get(i).buildModel();
		}
		
		removeEmptyModels(models);
		
		Vector<ModelData>[] labelData = new Vector[2];
		labelData[0] = loadSerializedModels(new File("handedness/allModelsBrent"));
		labelData[1] = loadSerializedModels(new File("handedness/allModelsGabe"));
		labelData[1] = loadSerializedModels(new File("handedness/allModelsMegan"));
		applyCommonLabel(models, labelData);
		
		runIdentifier(models);
	}
	
	public static Vector<ModelData> duplicateDateRange(Vector<ModelData> models, Date start, Date stop) {
		Vector<ModelData> ret = new Vector<ModelData>();
		
		for(int i=0;i<models.size();i++) {
			if(models.get(i).time.after(start) && models.get(i).time.before(stop)) {
				ret.add(models.get(i));
			}
		}

		return ret;
	}
	
	public static void keepDateRange(Vector<ModelData> models, Date start, Date stop) {
		int count = 0;
		
		for(int i=0;i<models.size();i++) {
			if(models.get(i).time.after(stop) || models.get(i).time.before(start)) {
				models.remove(i);
				i--;
				count++;
			}
		}
		
		System.out.println(count + " models removed by date.");
	}
	
	public static void removeEmptyModels(Vector<ModelData> models) {
		
		int count = 0;
		
		for(int i=0;i<models.size();i++) {
			GeonModel model = models.get(i).model;
			if(model instanceof Model2DTree) {
				Model2DTree modelTree = (Model2DTree)model;
				
				if(	modelTree.root.color.equals(Color.GREEN) && 
					modelTree.root.shape == Primitive2D.shapes.get("square") &&
					modelTree.root.children.size() == 0) {

					models.remove(i);
					i--;
					count++;
				}
			} else if(model == null){
				System.out.println("Model is not of type Model2DTree [null]");
			} else {
				System.out.println("Model is not of type Model2DTree [" + model.getClass() + "]");
			}
		}
		
		System.out.println(count + " empty models removed.");
		System.out.println(models.size() + " models left.");
	}
	
	public static void keepModelsGeonRange(Vector<ModelData> models, int min, int max) {
		
		int count = 0;
		
		for(int i=0;i<models.size();i++) {
			GeonModel model = models.get(i).model;
			if(model instanceof Model2DTree) {
				Model2DTree modelTree = (Model2DTree)model;
				
				int geonCount = modelTree.countObjects();
				if(geonCount < min || geonCount > max) {
					models.remove(i);
					i--;
					count++;
				}
			}
		}
		
		System.out.println(count + " wrongly sized models removed.");
	}
	
	public static void selectModelType(Vector<ModelData> models, HashSet<String> types) {
		Hashtable<String,Integer> otherTypes = new Hashtable<String,Integer>();
		for(int i=0;i<models.size();i++) {
			
			String gameType = models.get(i).gameType;
			
			if(!types.contains(gameType)) {
				
				if(gameType != null) {
					Integer count = otherTypes.remove(gameType);
					if(count == null) {
						count = 0;
					}
					otherTypes.put(gameType, count + 1);
				}
				
				models.remove(i);
				i--;
			}
		}
		
		for(String key : otherTypes.keySet()) {
			Integer count = otherTypes.get(key);
			System.out.println("Ignoring (" + count + ")\t " + key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<ModelData> loadSerializedModels(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			return (Vector<ModelData>)ois.readObject();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Hashtable<Integer,ModelData> convertToIDList(Vector<ModelData> models) {
		Hashtable<Integer,ModelData> modelsByID = new Hashtable<Integer,ModelData>();
		for(ModelData model : models) {
			modelsByID.put(model.modelID, model);
		}
		return modelsByID;
	}
	
	@SuppressWarnings("unchecked")
	public static void applyCommonLabel(Vector<ModelData> models, Vector<ModelData>[] labelData) {
		if(labelData == null || labelData.length < 1) {
			return;
		}
		
		Hashtable<Integer,ModelData>[] labelDataByID = new Hashtable[labelData.length];
		for(int i=0;i<labelData.length;i++) {
			labelDataByID[i] = convertToIDList(labelData[i]);
		}
		
		for(int i=0;i<models.size();i++) {
			ModelData mData = labelDataByID[0].get(models.get(i).modelID);
			if(mData != null) {
				int direction = mData.direction;
				boolean allMatch = true;
				for(int j=1;j<labelDataByID.length;j++) {
					mData = labelDataByID[j].get(models.get(i).modelID);
					if(mData == null || mData.direction != direction) {
						allMatch = false;
						break;
					}
				}
				
				if(allMatch) {
					models.get(i).direction = direction;
				}				
			}
		}
	}
	
	public static void paintCheckers(Graphics g, int width, int height) {
		
		Color light = new Color(220,220,220);
		Color dark = new Color(200,200,200);		
		for(int x=0;x<width;x+=20) {
			for(int y=0;y<height;y+=20) {
				if((x+y)/20%2==0) {
					g.setColor(light);
				} else {
					g.setColor(dark);
				}
				g.fillRect(x,y,20,20);
			}
		}
	}

	public static void runIdentifier(final Vector<ModelData> models) {
			
		JPanel panel = new JPanel(new BorderLayout());
		
		final int[] modelID = new int[1];
		modelID[0] = 0;
		
		final JPanel imagePanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(models.get(modelID[0]).buildModel()) {
					int width = 400;
					int height = 400;
					
					paintCheckers(g, width, height);
					
					g.setColor(Color.BLACK);
					g.drawString("word: " + models.get(modelID[0]).word + " [" + modelID[0] + "/" + models.size() + "]", 10, 30);
					
					BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					models.get(modelID[0]).model.thumbnail(image, image.getWidth(), image.getHeight(), 10.0);
					g.drawImage(image, 0, 0, this);
				}

				
				if(models.get(modelID[0]).direction == ModelData.DIRECTION_LEFT) {
					
					g.setColor(Color.GREEN);
					g.drawLine(10, 10 + 5, 20, 10 + 5);
					g.drawLine(10, 10 + 5, 10 + 5, 10 + 5 - 5);
					g.drawLine(10, 10 + 5, 10 + 5, 10 + 5 + 5);
					
				} else if(models.get(modelID[0]).direction == ModelData.DIRECTION_RIGHT) {
					
					g.setColor(Color.BLUE);
					g.drawLine(10, 10 + 5, 20, 10 + 5);
					g.drawLine(20, 10 + 5, 20 - 5, 10 + 5 - 5);
					g.drawLine(20, 10 + 5, 20 - 5, 10 + 5 + 5);
					
				} else if(models.get(modelID[0]).direction == ModelData.DIRECTION_BAD) {
					
					g.setColor(Color.RED);
					g.drawLine(10, 10, 20, 20);
					g.drawLine(10, 20, 20, 10);
					
				} else if(models.get(modelID[0]).direction == ModelData.DIRECTION_UNKNOWN) {
					
					g.setColor(Color.ORANGE);
					g.drawOval(10, 10, 10, 10);
				}
			}
		};
		panel.add(imagePanel, BorderLayout.CENTER);
		
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("file");
		
		JMenuItem save = new JMenuItem("save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("allModels")));
					oos.writeObject(models);
					oos.close();
					System.out.println("Saved");
				} catch(IOException e) {
					e.printStackTrace();
				}
			}				
		});
		file.add(save);

		final String showOnlyUnknown = "Show Only Unknown";
		final String showAll = "Show All";		
		final JMenuItem show = new JMenuItem(showOnlyUnknown);
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(show.getText() == showOnlyUnknown) {
					show.setText(showAll);
				} else {
					show.setText(showOnlyUnknown);					
				}
			}
		});
		file.add(show);
				
		menubar.add(file);
		
		JPanel buttons = new JPanel(new GridLayout(1,0));
			
			JButton previous = new JButton("previous");
				previous.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						if(show.getText() == showAll) {
							for(int i=modelID[0]-1;i>=0;i--) {
								if(models.get(i).direction == ModelData.DIRECTION_UNKNOWN) {
									modelID[0] = i;
									break;
								}
							}
						} else {
							modelID[0] = Math.max(0, modelID[0] - 1);
						}
						
						imagePanel.repaint();
					}
				});
			buttons.add(previous);
			
			JButton left = new JButton("left");
				left.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						models.get(modelID[0]).direction = ModelData.DIRECTION_LEFT;
						imagePanel.repaint();
					}
				});
			buttons.add(left);
			
			JButton unknown = new JButton("unknown");
				unknown.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						models.get(modelID[0]).direction = ModelData.DIRECTION_UNKNOWN;
						imagePanel.repaint();
					}
				});
			buttons.add(unknown);

			JButton bad = new JButton("bad");
				bad.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						models.get(modelID[0]).direction = ModelData.DIRECTION_BAD;
						imagePanel.repaint();
					}
				});
			buttons.add(bad);
			
			JButton right = new JButton("right");
				right.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						models.get(modelID[0]).direction = ModelData.DIRECTION_RIGHT;
						imagePanel.repaint();
					}
				});
			buttons.add(right);
			
			JButton next = new JButton("next");
			next.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
												
						if(show.getText() == showAll) {
							for(int i=modelID[0]+1;i<models.size();i++) {
								if(models.get(i).direction == ModelData.DIRECTION_UNKNOWN) {
									modelID[0] = i;
									break;
								}
							}
						} else {
							modelID[0] = Math.min(models.size() - 1, modelID[0] + 1);
						}
						
						imagePanel.repaint();
					}
				});
			buttons.add(next);
		panel.add(buttons, BorderLayout.SOUTH);
		
		JFrame frame = new JFrame();
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setJMenuBar(menubar);
		frame.setVisible(true);
		
	}
}
