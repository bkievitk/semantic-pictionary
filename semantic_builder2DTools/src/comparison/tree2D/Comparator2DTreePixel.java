package comparison.tree2D;

import java.awt.*;
import java.awt.image.BufferedImage;

import creator2DTree.Model2DTree;

public class Comparator2DTreePixel implements Comparator2DTree {

	private int size = 100;
	private double scale = 5;
	private static Color background = new Color(250,250,250);
	
	public String description() {
		return "Pixel";
	}
	
	public BufferedImage toImage(Model2DTree m) {		
		BufferedImage backgroundImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = backgroundImage.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, size, size);
		return m.thumbnail(backgroundImage, size, size, scale);
	}
	
	@Override
	public double similarity(Model2DTree m1, Model2DTree m2, double[] weights) {

		BufferedImage image1 = toImage(m1);
		BufferedImage image2 = toImage(m2);
		
		long sumSqr = 0;
		int count = 0;
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				Color c1 = new Color(image1.getRGB(x, y));
				Color c2 = new Color(image2.getRGB(x, y));
				
				if(!c1.equals(background) && !c2.equals(background)) {
					count++;

					int dRed = c1.getRed() - c2.getRed();
					int dGreen = c1.getGreen() - c2.getGreen();
					int dBlue = c1.getBlue() - c2.getBlue();
					int sum = dRed * dRed + dBlue * dBlue + dGreen * dGreen;
					sumSqr += sum;
				}
			}	
		}
		
		double similarity = sumSqr / (double)(count);		
		
		return 255 * 3 - similarity;
	}

}
