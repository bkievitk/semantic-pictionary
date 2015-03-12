package animals;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

public class PlayerPortal extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 2886532438462054648L;
	public Animal you;
	public AnimalWorld world;
	public AnimalController controller;
	public JTextArea messages = new JTextArea();
	public JTextField commands = new JTextField();
	public Container parent;
	
	public PlayerPortal(Animal newYou, AnimalWorld newWorld, final Container parent) {
		you = newYou;
		world = newWorld;
		this.parent = parent;
		
		messages.setEditable(false);
		setFocusable(true);
		requestFocus();
		
		commands.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String command = commands.getText();
				
				try {
					String[] cmdPts = command.split("~");
					
					if(command.startsWith("SET_YOU")) {
						Animal newYou = Animal.decodeString(cmdPts[1],world);
						newYou.x = 10;
						newYou.y = 10;
						world.animalList.acquire();
						world.animals.remove(you);
						world.animals.add(newYou);
						world.animalList.release();
						you = newYou;
						
						AnimalController newController = new AnimalController(you);
						newController.decodeString(cmdPts[2]);
						parent.removeKeyListener(controller);
						parent.addKeyListener(newController);
						controller = newController;
						
						messages.append("Created new creature for you.");
						
					} else if(command.startsWith("SET_NEW")) {
						Animal them = Animal.decodeString(cmdPts[1],world);
						them.x = Integer.parseInt(cmdPts[2]);
						them.y = Integer.parseInt(cmdPts[3]);

						world.animalList.acquire();
						world.animals.add(them);
						world.animalList.release();						

						messages.append("Created new creature for them.");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				parent.requestFocus();
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		try {
			world.animalList.acquire();
			for(Animal animal : world.animals) {
				animal.renderAnimal(g);
			}
			world.animalList.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}


	public void stateChanged(ChangeEvent e) {
		if(controller != null) {
			controller.tick();
		}
		repaint();		
	}
}
