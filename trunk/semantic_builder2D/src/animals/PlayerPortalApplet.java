package animals;

import java.awt.*;
import javax.swing.*;

import templates.CreatorPanel;

public class PlayerPortalApplet extends JApplet {
	private static final long serialVersionUID = -8649676540073783558L;
	
	public void start() {
		JPanel portal = buildPortal(this);
		add(portal);
	}
	
	public static void main(String[] args) {
		
		// Build JFrame.
		JFrame frame = new JFrame();		
		
		JPanel portal = buildPortal(frame);
		frame.add(portal);
		
		frame.setSize(500,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
		
	public static JPanel buildPortal(Container parent) {
		
		// Create world framework.
		AnimalWorld world = new AnimalWorld();

		try {
			String animalString = "[255,0,0,-84,100,100,4,-5[128,128,128,0,200,100,0,-31,-1,-1,1,-1[192,192,192,0,260,75,0,-46,-1,0,1,0,e[192,192,192,0,74,76,0,-31,-1,1,1,1[128,128,128,214,60,56,4,-47,0,0,0,0,t[255,0,0,0,63,155,1,-31,0,1,0,-1,s[255,0,0,563,51,100,1,-64,0,1,0,1,s][255,0,0,61,55,105,1,-64,0,1,0,1,s]]]]]]]:0,0,0";
			String controllerString = "wasd:q,0,t:e,0,f";
			
			
			Animal you = Animal.decodeString(animalString,world);
			you.x = 10;
			you.y = 10;
			world.animalList.acquire();
			world.animals.add(you);
			world.animalList.release();
			
			
			Animal them = Animal.decodeString(animalString,world);
			them.x = 250;
			them.y = 250;
			world.animalList.acquire();
			world.animals.add(them);
			world.animalList.release();
			
		
			// Create your portal to the world.
			PlayerPortal portal = new PlayerPortal(you,world,parent);
			
			// Add controller.
			portal.controller = new AnimalController(you);
			portal.controller.decodeString(controllerString);
			
			portal.addKeyListener(portal.controller);
			portal.setFocusable(true);
			portal.requestFocus();
			
			// On change, inform the portal.
			portal.world.changeListeners.add(portal);
			
			parent.addKeyListener(portal.controller);
			parent.setFocusable(true);
			parent.requestFocus();	

			JScrollPane scrollMessages = new JScrollPane(portal.messages);
			scrollMessages.setPreferredSize(new Dimension(100,50));
			world.portal = portal;
			
			JPanel message = new JPanel(new BorderLayout());
			message.add(scrollMessages,BorderLayout.CENTER);
			message.add(CreatorPanel.labeledPanel(portal.commands,"enter command:"),BorderLayout.SOUTH);
			
			JPanel masterPanel = new JPanel(new BorderLayout());
			masterPanel.add(portal,BorderLayout.CENTER);
			masterPanel.add(message,BorderLayout.SOUTH);
			return masterPanel;
		} catch(InterruptedException e) {
		}
		
		return null;
	}

	
}
