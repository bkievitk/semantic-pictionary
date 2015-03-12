package templates;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Tool to view BufferedImage.
 * @author bkievitk
 */

public class ImageViewer extends JPanel {
	
	private static final long serialVersionUID = 4718162972596679109L;
	private BufferedImage img;
	
	public ImageViewer(BufferedImage img, boolean buildFrame) {
		this.img = img;
		if(buildFrame) {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(img.getWidth(), img.getHeight());
			frame.add(this);
			frame.setVisible(true);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this);
	}
	
}
