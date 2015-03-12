package animals.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class CenterLayout implements LayoutManager {

	public Component c = null;
	
	public void addLayoutComponent(String arg0, Component arg1) {
		c = arg1;
	}

	public void layoutContainer(Container arg0) {
		c = arg0.getComponent(0);
		if(c != null) {
			c.setSize(c.getPreferredSize());
			c.setLocation(arg0.getWidth()/2-c.getWidth()/2, arg0.getHeight()/2-c.getHeight()/2);
		}
	}

	public Dimension minimumLayoutSize(Container arg0) {
		return null;
	}

	public Dimension preferredLayoutSize(Container arg0) {
		return null;
	}

	public void removeLayoutComponent(Component arg0) {
		c = null;
	}

}
