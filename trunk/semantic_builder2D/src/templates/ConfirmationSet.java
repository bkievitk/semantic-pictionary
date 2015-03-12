package templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ask the user to confirm an action.
 * READY
 * @author bkievitk
 */

public class ConfirmationSet extends JPanel {
		
	private static final long serialVersionUID = -8728904682450373364L;

	/**
	 * Set message and what to do when confirmed.
	 * @param message
	 * @param al
	 */
	public ConfirmationSet(String message, ActionListener al) {
		this.setLayout(new FlowLayout());

		// Buttons.
		final JButton yes = new JButton("yes");
		final JButton no = new JButton("no");
		final JButton originalMessage = new JButton(message);
		final JLabel confirm = new JLabel("confirm");
		
		// Lay out.
		yes.setVisible(false);
		no.setVisible(false);
		confirm.setVisible(false);
		originalMessage.setVisible(true);
		setBackground(Color.BLACK);

		// On click.
		ActionListener revert = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				yes.setVisible(false);
				no.setVisible(false);
				confirm.setVisible(false);
				originalMessage.setVisible(true);
				invalidate();
			}			
		};

		// Listeners.
		yes.addActionListener(al);
		yes.addActionListener(revert);
		no.addActionListener(revert);

		// The primary button.
		originalMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				yes.setVisible(true);
				no.setVisible(true);
				confirm.setVisible(true);
				originalMessage.setVisible(false);
				invalidate();
			}			
		});
		
		// Add to panel.
		add(confirm);
		add(yes);
		add(no);
		add(originalMessage);
	}
	
}
