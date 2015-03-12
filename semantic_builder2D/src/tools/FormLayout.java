package tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * The form layout builds two columns where alternating columns are label, data.
 * The label column has a width that is the minimum to fit all elements and the Y locations of labels match the data.
 * @author bkievitk
 *
 */
public class FormLayout implements LayoutManager {
	
	public void addLayoutComponent(String name, Component comp) {
		// Ignore.
	}
	
	public void layoutContainer(Container parent) {

		// Must be even number.
		if(parent.getComponentCount() % 2 == 1) {
			return;
		}
		
		// Find max width.
		int width = 0;
		for(int i=0;i<parent.getComponentCount();i+=2) {
			width = Math.max(width, parent.getComponent(i).getPreferredSize().width);
		}

		// Drop down the height one by one.
		int y = 0;
		for(int i=0;i<parent.getComponentCount();i+=2) {
			int height = Math.max(parent.getComponent(i).getPreferredSize().height, parent.getComponent(i+1).getPreferredSize().height);

			// Set left element.
			parent.getComponent(i).setLocation(0,y);
			parent.getComponent(i).setSize(new Dimension(width,height));
			
			// set Right element.
			parent.getComponent(i+1).setLocation(width,y);
			parent.getComponent(i+1).setSize(new Dimension(parent.getWidth()-width,height));
			y += height;
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	public Dimension preferredLayoutSize(Container parent) {
		
		// Must be even number.
		if(parent.getComponentCount() % 2 == 1) {
			return new Dimension(0,0);
		}

		// Get the max width of both columns.
		int widthLabel = 0;
		int widthData = 0;
		for(int i=0;i<parent.getComponentCount();i+=2) {
			widthLabel = Math.max(widthLabel, parent.getComponent(i).getPreferredSize().width);
			widthData = Math.max(widthData, parent.getComponent(i+1).getPreferredSize().width);
		}

		// Sum the heights.
		int height = 0;
		for(int i=0;i<parent.getComponentCount();i+=2) {
			height += Math.max(parent.getComponent(i).getPreferredSize().height, parent.getComponent(i+1).getPreferredSize().height);
		}
		
		return new Dimension(widthLabel+widthData,height);
	}

	public void removeLayoutComponent(Component comp) {
		// Ignore
	}
	
}
