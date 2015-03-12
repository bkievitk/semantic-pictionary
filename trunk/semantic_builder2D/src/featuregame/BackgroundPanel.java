package featuregame;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Simply draw image scaled to the size of the screen.
 * @author Gabriel
 */
public class BackgroundPanel extends JPanel {

	private static final long serialVersionUID = -5623954460092948476L;

	// Background image.
	private Image img;

	/**
	 * Initialize with image.
	 * @param img
	 */
	public BackgroundPanel(Image img) {
		this.img = img;
	}

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);

    	// Render image.
    	if(img != null) {
    		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    	}
    }
}
