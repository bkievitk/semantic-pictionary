package tools;

import java.awt.*;

/**
 * Use the items default size. Then place it aligned in the X and Y axis.
 * @author bkievitk
 */

public class AlignmentLayout implements LayoutManager {

	// Y axis allignment.
	public static final int TOP = -1;
	public static final int CENTER = 0;
	public static final int BOTTOM = 1;

	// X axis allignment.
	public static final int LEFT = -1;
	//public static final int CENTER = 0;
	public static final int RIGHT = 1;

	// Assume center aligned.
	public int xLayout = CENTER;
	public int yLayout = CENTER;

	public void layoutContainer(Container parent) {		
		
		// Get the top component if available.
		if(getComponent(parent) != null) {
			
			// Get size.
			int width = getComponent(parent).getPreferredSize().width;
			int height = getComponent(parent).getPreferredSize().height;
			int x,y;
			
			// Layout x axis.
			if(xLayout == LEFT) {
				x = 0;
			} else if(xLayout == CENTER) {
				x = (parent.getWidth()-width)/2;
			} else {
				x = parent.getWidth()-width;
			}

			// Layout y axis.
			if(yLayout == TOP) {
				y = 0;
			} else if(yLayout == CENTER) {
				y = (parent.getHeight()-height)/2;
			} else {
				y = parent.getHeight()-height;
			}
			
			// Set.
			getComponent(parent).setLocation(x, y);
			getComponent(parent).setSize(width, height);
		}
	}

	/**
	 * The minimum size is the minimum of the child component.
	 */
	public Dimension minimumLayoutSize(Container parent) {
		if(getComponent(parent) != null) {
			return getComponent(parent).getMinimumSize();
		}
		return null;
	}
	
	/**
	 * The preferred size is the preferred of the child component.
	 */
	public Dimension preferredLayoutSize(Container parent) {
		if(getComponent(parent) != null) {
			return getComponent(parent).getPreferredSize();
		}
		return null;
	}
	
	/**
	 * Get the top component.
	 * @param parent
	 * @return
	 */
	private Component getComponent(Container parent) {
		
		// Get the top if it exists.
		if(parent.getComponentCount() > 0) {
			return parent.getComponent(0);
		}
		return null;
	}

	public void removeLayoutComponent(Component parent) {
		// Do nothing.
	}

	public void addLayoutComponent(String name, Component comp) {
		// Do nothing.
	}
	
}
