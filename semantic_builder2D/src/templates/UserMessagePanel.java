package templates;

import java.awt.Color;

import javax.swing.*;

public class UserMessagePanel extends JPanel implements UserMessage {

	private static final long serialVersionUID = -1338696968661800360L;

	private JLabel label = new JLabel();
	
	public UserMessagePanel() {
		add(label);
		this.setBackground(Color.BLACK);
		label.setForeground(Color.BLACK);
		label.setBackground(Color.BLACK);
		label.setOpaque(true);
	}
	
	public void showMessage(String message, int level) {
		switch(level) {
			case UserMessage.ERROR: 	label.setForeground(Color.WHITE); label.setBackground(Color.RED); break;
			case UserMessage.WARN: 		label.setForeground(Color.YELLOW); label.setBackground(Color.BLACK); break;
			case UserMessage.INFORM: 	label.setForeground(Color.WHITE); label.setBackground(Color.BLACK); break;
			default:
				label.setForeground(Color.WHITE);
				label.setBackground(Color.RED);
				label.setText("INVALID MESSAGE LEVEL");
				return;
		}	
		
		label.setText(message);
	}

}
