package animals.client;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.*;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = -8900368792587060210L;

	// The panel to login.
	public JPanel login;
	
	// Panel to create new user.
	public JPanel newUser;
	
	// This fires when logged in.
	public ActionListener listener;

	// Password and name.
	private JTextField name = new JTextField();
	private JPasswordField password = new JPasswordField();

	// Build panels.
	public LoginPanel(ActionListener listener) {
		this.listener = listener;
		
		setLayout(new CenterLayout());
		buildLoginPanel();
		buildNewUserPanel();
		add(login);
	}

	public String getName() {
		return name.getText();
	}
	
	public String getPassword() {
		return new String(password.getPassword());
	}
		
	public void buildNewUserPanel() {
		// To do. Create new users.
		newUser = new JPanel();
		newUser.setPreferredSize(new Dimension(300,500));
		newUser.setBackground(Color.WHITE);
		
		SpringLayout layout = new SpringLayout();
		newUser.setLayout(layout);
	}
	
	public void buildLoginPanel() {
		
		login = new JPanel();
		login.setPreferredSize(new Dimension(250,112));
		login.setBackground(Color.WHITE);

		SpringLayout layout = new SpringLayout();
		login.setLayout(layout);

		name = new JTextField();
		name.setColumns(13);
		
		password = new JPasswordField();
		password.setColumns(13);
		JLabel nameLabel = new JLabel("name");
		JLabel passwordLabel = new JLabel("password");
		JButton confirm = new JButton("enter");

		confirm.setPreferredSize(new Dimension(65,25));
		JLabel newUserLink = new JLabel("new user");
		login.add(name);
		login.add(password);
		login.add(nameLabel);
		login.add(passwordLabel);
		login.add(confirm);
		//login.add(newUserLink);

		final JLabel error = new JLabel();
		error.setVisible(false);
		error.setForeground(Color.RED);
		login.add(error);
				
		layout.putConstraint(SpringLayout.WEST, nameLabel, 35, SpringLayout.WEST, login);
		layout.putConstraint(SpringLayout.NORTH, nameLabel, 20, SpringLayout.NORTH, login);
		
		layout.putConstraint(SpringLayout.WEST, name, 5, SpringLayout.EAST, nameLabel);
		layout.putConstraint(SpringLayout.NORTH, name, 0, SpringLayout.NORTH, nameLabel);

		layout.putConstraint(SpringLayout.EAST, passwordLabel, 0, SpringLayout.EAST, nameLabel);
		layout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.SOUTH, nameLabel);
		
		layout.putConstraint(SpringLayout.WEST, password, 5, SpringLayout.EAST, passwordLabel);
		layout.putConstraint(SpringLayout.NORTH, password, 0, SpringLayout.NORTH, passwordLabel);

		layout.putConstraint(SpringLayout.WEST, confirm, 0, SpringLayout.WEST, password);
		layout.putConstraint(SpringLayout.NORTH, confirm, 5, SpringLayout.SOUTH, password);

		layout.putConstraint(SpringLayout.WEST, newUserLink, 5, SpringLayout.EAST, confirm);
		layout.putConstraint(SpringLayout.NORTH, newUserLink, 5, SpringLayout.NORTH, confirm);
		
		layout.putConstraint(SpringLayout.WEST, error, 2, SpringLayout.WEST, login);
		layout.putConstraint(SpringLayout.NORTH, error,2, SpringLayout.NORTH, login);
		
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String nameString = name.getText();
					String passwordString = new String(password.getPassword());

					// Connect to website.
		            URL url = new URL(IOWeb.webHostIO + "validUser.php?name=" + nameString + "&password=" + passwordString);
		            URLConnection connection = url.openConnection();
		            
		            // Read data.
		            InputStream stream = connection.getInputStream();
		            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		            
		            // First line must be word and second line, wordID.
		            String line = in.readLine();
		            		            
		            // Close stream and return.
		            in.close();
		            
		            if(line.equals("TRUE")) {		            				            
		            	error.setVisible(false);		            	
		            	listener.actionPerformed(new ActionEvent(getName(),0,getPassword()));
		            } else {		            				            
		            	error.setText(line);
		            	error.setVisible(true);
		            	invalidate();
		            	repaint();
		            }
		        }
		        catch (MalformedURLException e) {
		        	e.printStackTrace();
		        }
		        catch (IOException e) {
		        	e.printStackTrace();
		        }
			}			
		};
		
		confirm.addActionListener(al);
		password.addActionListener(al);
		
		newUserLink.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				removeAll();
				add(newUser);
				validate();
				repaint();
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}
}
