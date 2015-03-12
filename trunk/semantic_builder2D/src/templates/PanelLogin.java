package templates;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.*;

import featuregame.BackgroundPanel;

import tools.AlignmentLayout;
import tools.FormLayout;
import tools.UserCredentials;

public class PanelLogin extends BackgroundPanel {

	private static final long serialVersionUID = -1531171865040985278L;

	private static String webHost = "http://www.indiana.edu/~semantic/io/";
	
	private JTextField userName = new JTextField();
	private JTextField password = new JPasswordField();
	private JButton login = new JButton("login");
	private JLabel validationLabel = new JLabel("Name/password not recognized");
	
	public PanelLogin(final PanelLoginCallback callback, Image image) {
		super(image);
		
		// Just place in the center of the panel.
		setLayout(new AlignmentLayout());

		// All login panel.
		JPanel allLogin = new JPanel(new BorderLayout());
		allLogin.setOpaque(false);
		allLogin.setPreferredSize(new Dimension(200,80));
		add(allLogin);

		// Fields.
		JPanel loginFields = new JPanel(new FormLayout());
		loginFields.setOpaque(false);
		loginFields.add(new JLabel("name  "));
		loginFields.add(userName);		
		loginFields.add(new JLabel("password  "));
		loginFields.add(password);
		
		allLogin.add(loginFields,BorderLayout.CENTER);
		allLogin.add(login,BorderLayout.SOUTH);
		allLogin.add(validationLabel, BorderLayout.NORTH);
		validationLabel.setVisible(false);
		
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				try {
					String request = webHost + "validUser.php?name=" + userName.getText() + "&password=" + password.getText();
		            URL url = new URL(request);
		            URLConnection connection = url.openConnection();
		            InputStream stream = connection.getInputStream();
		            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		            String line = in.readLine();
		            
		            if(line != null && line.startsWith("TRUE")) {
		            	String number = line.substring(line.indexOf(':')+1);
		            	UserCredentials credentials = new UserCredentials(userName.getText(), password.getText(), Integer.parseInt(number));
						userName.setText("");
						password.setText("");
		            	callback.setPlayerInfo(credentials);
		            }
		            else
		            {
		            	validationLabel.setVisible(true);
		            }
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
			}
		};
		
		login.addActionListener(al);
		password.addActionListener(al);

	}
}
