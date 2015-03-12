package tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Grapher {

	public static void graphHistogram(Graphics g, Rectangle location, int[] histogram) {
		
		int max = 0;
		for(int value : histogram) {
			max = Math.max(max, value);
		}
		
		if(max == 0) {
			g.setColor(Color.BLACK);
			g.drawRect(location.x, location.y, location.width, location.height);
			g.drawString("No values", location.x + 2, location.y + 14);
			return;
		}
		
		for(int i=0;i<histogram.length;i++) {
			double ratio = histogram[i] / max;
			int xStart = location.width * i / histogram.length;
			int xStop = location.width * (i+1) / histogram.length;
			int yStart = location.y + (int)(location.height * (1 - ratio));
			int yStop = location.y + location.height;
			
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(xStart, yStart, xStop - xStart, yStop - yStart);
			
			g.setColor(Color.BLACK);
			g.drawRect(xStart, yStart, xStop - xStart, yStop - yStart);
		}
	}
}
