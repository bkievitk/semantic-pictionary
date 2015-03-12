package creator3DTree;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JPanel;

import tools.MyGrayFilter;

/**
 * A common place to handle loading images.
 * This means it should be easy to change from loading from a file to loading from the web.
 * @author brentkk
 *
 */
public class ImageLoader {

	public static ImageLoader imgs = null;
	
	public JApplet loader;
	public JPanel component = new JPanel();
	
	// Primitive images.
	public ImageIcon deleteImg;
	public ImageIcon deleteImgDisable;

	// Action images.
	public ImageIcon moveImg;
	public ImageIcon removeImg;
	public ImageIcon selectImg;
	public ImageIcon cloneBindImg;
	public ImageIcon cloneUnboundImg;

	/**
	 * Create image set.
	 * @param loader
	 */
	public ImageLoader(JApplet loader) {
		
		// Use the applet to load images if applicable.
		this.loader = loader;

		deleteImg = getImg("delete");
		deleteImgDisable = MyGrayFilter.getDisabledIcon(component, deleteImg);
		moveImg = getImg("move");
		removeImg = getImg("remove");
		selectImg = getImg("select");
		cloneBindImg = getImg("cloneBind");
		cloneUnboundImg = getImg("cloneUnbound");
	}
	
	/**
	 * Load images from web.
	 * @param img Image name.
	 * @return
	 */
	public ImageIcon getImg(String img) {

		URL url = null;
		
		// First try to get resource from your JAR file.
		url = this.getClass().getResource("/" + img + ".jpg");

		if(url == null) {			
			// If we can't get image from our JAR, then connect to the web to get images.
			try {
				String strURL = "http://www.indiana.edu/~clcl/object3D/imgs/" + img + ".jpg";
				url = new URL(strURL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
						
		// Load through Applet if possible.
		// Otherwise load directly through the url.
		if(loader == null) { return new ImageIcon(url); }
		else { return new ImageIcon(loader.getImage(url)); }
		
	}
}
