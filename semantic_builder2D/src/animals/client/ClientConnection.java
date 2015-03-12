package animals.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientConnection extends JApplet {

	private static final long serialVersionUID = -8206240049422191562L;
	
	public String name;
	public String password;
	public CardLayout layout = new CardLayout();

	public static final String CARD_LOGIN = "CARD_LOGIN";
	public static final String CARD_OPTIONS = "CARD_OPTIONS";
	
	public ClientConnection() {
		getContentPane().setLayout(layout);
		
		// Login
		LoginPanel login = new LoginPanel(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loggedIn((String)e.getSource(),e.getActionCommand());
			}
		});
		add(login,CARD_LOGIN);

		// Options
		JPanel options = new JPanel(new CenterLayout());
		JPanel buttons = new JPanel(new GridLayout(3,1));
		options.add(buttons);
		buttons.setPreferredSize(new Dimension(150,150));
		
		JButton viewCreatures = new JButton("View Creatures");		
		buttons.add(viewCreatures);

		JButton help = new JButton("Help");		
		buttons.add(help);
		
		JButton about = new JButton("About");		
		buttons.add(about);

		add(options,CARD_OPTIONS);
	}
	
	public void init() {
		layout.show(getContentPane(), CARD_LOGIN);
	}
	
	public void loggedIn(String name, String password) {
		this.name = name;
		this.password = password;
		layout.show(getContentPane(), CARD_OPTIONS);
	}
}
