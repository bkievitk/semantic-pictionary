package admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.swing.*;

public class HandednessDB {

	public static void main(String[] args) {
		
		//runIdentifier(new File("modelData.dat"));
		
		
		File[] files = {new File("handedness/allModelsBrent"),
						new File("handedness/allModelsGabe"),
						new File("handedness/allModelsMegan"),
						new File("handedness/allModelsMelody")};
		
		//addUserID(brent);
		//borrowUserIDs(gabe, brent);
		//show0IDs(gabe);
				
		runCSVOutputter(getPlayers(), files, new File("handedness.csv"));
		
	}
	
	@SuppressWarnings("unchecked")
	public static void show0IDs(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Vector<ModelData> models = (Vector<ModelData>)ois.readObject();
			ois.close();
			
			for(ModelData model : models) {
				if(model.playerID <= 0) {
					System.out.println("ERROR model " + model.modelID + " " + model.direction + " " + model.playerID);
					
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void borrowUserIDs(File noUsers, File users) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(users));
			Vector<ModelData> modelsWithUsers = (Vector<ModelData>)ois.readObject();
			ois.close();
			
			Hashtable<Integer,Integer> modelIDToUserID = new Hashtable<Integer,Integer>();
			for(ModelData model : modelsWithUsers) {
				modelIDToUserID.put(model.modelID, model.playerID);
			}
			
			
			ois = new ObjectInputStream(new FileInputStream(noUsers));
			Vector<ModelData> modelsWithoutUsers = (Vector<ModelData>)ois.readObject();
			ois.close();
			
			for(ModelData model : modelsWithoutUsers) {
				model.playerID = modelIDToUserID.get(model.modelID);
			}
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(noUsers));
			oos.writeObject(modelsWithoutUsers);
			oos.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Hashtable<Integer,Player> getPlayers() {
		Hashtable<Integer,Player> ret = new Hashtable<Integer,Player>();
		
		try {
			URL url = new URL("http://www.indiana.edu/~semantic/io/adminGetUsers.php?adminPassword=" + ModelManager.adminPassword);
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while((line = r.readLine()) != null) {
				Player player = new Player(line);
				ret.put(player.playerID, player);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static void addUserID(File file) {

		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Vector<ModelData> models = (Vector<ModelData>)ois.readObject();
			ois.close();
						
			int i = 0;
			for(ModelData model : models) {
				String line = "";
				try {
					System.out.println((i++) + "/" + models.size());
					URL url = new URL("http://www.indiana.edu/~semantic/io/admingetUserFromObject.php?adminPassword=" + ModelManager.adminPassword + "=" + model.modelID);
					BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
					line = r.readLine();
					String[] parts = line.split("~");
					int playerID = Integer.parseInt(parts[0].trim());
					int handedness = Integer.parseInt(parts[1].trim());
					int gender = Integer.parseInt(parts[2].trim());
					int ethnicity = Integer.parseInt(parts[3].trim());
					int race = Integer.parseInt(parts[4].trim());
					
					model.playerID = playerID;
					model.playerData.handedness = handedness;
					model.playerData.gender = gender;
					model.playerData.ethnicity = ethnicity;
					model.playerData.race = race;
					
				} catch(NumberFormatException e) {
					System.out.println(line);
					model.playerID = -1;
				}
			}
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(models);
			oos.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static void runCSVOutputter(Hashtable<Integer,Player> players, File[] files, File output) {
		
		try {
			Hashtable<Integer,int[]> results = new Hashtable<Integer, int[]>();
			Hashtable<Integer,ModelData> allModels = new Hashtable<Integer, ModelData>();
			int judgeID = 0;
			for(File file : files) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				Vector<ModelData> models = (Vector<ModelData>)ois.readObject();
				ois.close();
				
				for(ModelData model : models) {
					int[] resultSet = results.get(model.modelID);
					if(resultSet == null) {
						resultSet = new int[files.length];
						results.put(model.modelID, resultSet);
					}
					resultSet[judgeID] = model.direction;
					allModels.put(model.modelID, model);
				}
				judgeID++;
			}		
			
			BufferedWriter w = new BufferedWriter(new FileWriter(output));
			w.write("objectID,objectType,playerID,handedness,gender,ethnicity,race");
			for(File file : files) {
				w.write("," + file.getName());
			}
			w.write("\n");
						
			for(Integer modelID : allModels.keySet()) {
				
				int[] result = results.get(modelID);
				ModelData model = allModels.get(modelID);
				
				if(model.playerID >= 0) {
					Player player = players.get(model.playerID);
					w.write(model.modelID + "," + model.word + "," + player.playerID + "," + player.handedness + "," + player.gender + "," + player.ethnicity + "," + player.race);
					
					for(int r : result) {
						w.write("," + r);
					}
					w.write("\n");
				}
			}
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static void runIdentifier(File modelFile) {
				
		try {
			
			if(!modelFile.exists()) {
				
				System.out.println("Loading models from web ...");
				
				Vector<ModelData> modelsIn = ModelManager.getAllModels();
				ModelManager.refineModels(modelsIn);
				
				System.out.println("Randomizing models ...");
				
				Random rand = new Random();
				for(int i=0;i<modelsIn.size();i++) {
					int j = rand.nextInt(modelsIn.size()-i) + i;
					ModelData tmp = modelsIn.get(j);
					modelsIn.set(j, modelsIn.get(i));
					modelsIn.set(i, tmp);
				}
				
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFile));
				oos.writeObject(modelsIn);
				oos.close();
			}
			

			System.out.println("Loading models from file ...");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile));
			final Vector<ModelData> models = (Vector<ModelData>)ois.readObject();
			ois.close();
			
			final int[] modelID = new int[1];
			modelID[0] = 0;
			
			JPanel panel = new JPanel(new BorderLayout());			
			
			final JPanel imagePanel = new JPanel() {
				private static final long serialVersionUID = 1L;
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					if(models.get(modelID[0]).buildModel()) {
						int width = 400;
						int height = 400;
						
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
						
						g.setColor(Color.BLACK);
						g.drawString("word: " + models.get(modelID[0]).word + " [" + modelID[0] + "/" + models.size() + "]", 10, 30);
						
						BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
						models.get(modelID[0]).model.thumbnail(image, image.getWidth(), image.getHeight(), 10.0);
						g.drawImage(image, 0, 0, this);
					}


					g.setColor(Color.GREEN);
					g.fillOval(10, getHeight() - 20, 10, 10);
					
					g.setColor(Color.RED);
					g.fillOval(getWidth() - 20, getHeight() - 20, 10, 10);
					
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
						
			KeyListener listener = new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
					if(arg0.getKeyChar() == 'a') {
						models.get(modelID[0]).direction = ModelData.DIRECTION_UNKNOWN;
						modelID[0] = Math.min(models.size() - 1, modelID[0] + 1);
						imagePanel.repaint();
					} else if(arg0.getKeyChar() == '\'') {
						models.get(modelID[0]).direction = ModelData.DIRECTION_BAD;
						modelID[0] = Math.min(models.size() - 1, modelID[0] + 1);
						imagePanel.repaint();
					}
					
				}
				public void keyReleased(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			};

			imagePanel.addKeyListener(listener);
			panel.addKeyListener(listener);
			
			imagePanel.setFocusable(true);
			imagePanel.requestFocus();
			
			imagePanel.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent arg0) {
					imagePanel.requestFocus();
				}

				public void mousePressed(MouseEvent arg0) {
					imagePanel.requestFocus();
				}

				public void mouseReleased(MouseEvent arg0) {}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
			});
			
			JPanel buttons = new JPanel(new GridLayout(1,0));
				
				JButton previous = new JButton("previous");
					previous.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							modelID[0] = Math.max(0, modelID[0] - 1);
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
							modelID[0] = Math.min(models.size() - 1, modelID[0] + 1);
							imagePanel.repaint();
						}
					});
				buttons.add(next);
			panel.add(buttons, BorderLayout.SOUTH);
			
			JMenuBar menubar = new JMenuBar();
			JMenu file = new JMenu("file");
			JMenuItem save = new JMenuItem("save");
			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("modelData.dat")));
						oos.writeObject(models);
						oos.close();
						System.out.println("Saved");
					} catch(IOException e) {
						e.printStackTrace();
					}
				}				
			});
			file.add(save);
			menubar.add(file);
			
			JFrame frame = new JFrame();
			frame.setSize(600, 600);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(panel);
			frame.setJMenuBar(menubar);
			frame.setVisible(true);
			
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
