package creator2DTree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import modelTools.GeonModel;
import modelTools.Primitive2D;

import templates.WindowRender;

/**
 * This is a model using the 2D tree framework.
 * @author bkievitk
 */

public class Model2DTree extends GeonModel {

	private static final long serialVersionUID = 1933819379455637969L;
	
	// A tree needs a root.
	public PrimitiveInstance2DTree root;

	public Model2DTree() {
		// Create the default scene.
		root = new PrimitiveInstance2DTree(0, 200, 100, Primitive2D.shapes.get("square"));
		root.color = Color.GREEN;
	}
		
	public String toReduced() { 
		return root.toReduced();
	}
	
	public void fromReduced(String reduced) {
		root = PrimitiveInstance2DTree.fromReduced(reduced,null);
	}
	
	public String toXML() {
		// TODO: Need to define xml format for the Tree2D model.
		return "";
	}
	
	public int countObjects() {
		// Need to ask the root to count how many objects there are.
		return root.countObjects();
	}	
	
	public PrimitiveInstance2DTree getSelected() {
		// Force the getSelected into the correct instance type.
		return (PrimitiveInstance2DTree)selected;
	}
	
	public void clear() {
		super.clear();
		root = new PrimitiveInstance2DTree(0, 200, 100, Primitive2D.shapes.get("square"));
		root.color = Color.GREEN;
	}
	
	public static BufferedImage simpleBackground(int width, int height, Color color) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		return img;
	}
	
	public BufferedImage thumbnail(int width, int height, double scale, int range, int step, PrimitiveInstance2DTree root, Color backgroundColor) {
		
		int tmp = root.rotation[0];
		
		BufferedImage[] images = new BufferedImage[range * 2 / step + 1];

		int j = 0;
		for(int val = -range;val<=range;val+=step) {
			root.rotation[0] = tmp + val;
			images[j] = thumbnail(simpleBackground(width, height, backgroundColor), width, height, scale);
			j++;
		}
		
		BufferedImage imgAvg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<imgAvg.getWidth();x++) {
			for(int y=0;y<imgAvg.getHeight();y++) {
				long sumRed = 0;
				long sumGreen = 0;
				long sumBlue = 0;
				int count = 0;
				
				for(int i=0;i<images.length;i++) {
					Color c = new Color(images[i].getRGB(x, y));
					if(!c.equals(backgroundColor)) {
						count++;
						sumRed += c.getRed();
						sumGreen += c.getGreen();
						sumBlue += c.getBlue();
					}
				}
				
				if(count == 0) {
					imgAvg.setRGB(x, y, backgroundColor.getRGB());
				} else {
					int red = (int)(sumRed / count);
					int green = (int)(sumGreen / count);
					int blue = (int)(sumBlue / count);
										
					Color c = new Color(red, green, blue);
					imgAvg.setRGB(x, y, c.getRGB());
				}
			}
		}
		
		return imgAvg;
	}

	public BufferedImage thumbnail(Color backgroundColor, int width, int height, double scale) {
		return thumbnail(simpleBackground(width,height,backgroundColor),width,height,scale);
	}
	
	public BufferedImage thumbnail(BufferedImage background, int width, int height, double scale) {
		BufferedImage img;
		if(background == null) {
			img = WindowRender.makeBackground(width,height);
		} else {
			img = background;
		}
		
		Graphics g = img.getGraphics();
		
		// Create root transform to be centered and scaled by 20.
		AffineTransform rootTransform = new AffineTransform();
		rootTransform.translate(img.getWidth()/2,img.getHeight()/2);
		rootTransform.scale(scale, scale);
		rootTransform.rotate(root.rotation[0]/100.0);
			
		// Render.
		root.render(g, rootTransform,getSelected(),false);
		
		return img;
	}
}
