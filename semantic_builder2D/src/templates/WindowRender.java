package templates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;

/**
 * Basic class to render a geon object.
 * @author bkievitk
 *
 */

public abstract class WindowRender extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 3423875690369972947L;

	public WindowRender(GeonModel model) {
		if(model != null) {
			model.addSelectListener(this);
			model.addUpdateListener(this);
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		repaint();
	}
	
	public static BufferedImage makeBackground(int width, int height) {
		BufferedImage background = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics g = background.getGraphics();
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
		
		return background;
	}
}
