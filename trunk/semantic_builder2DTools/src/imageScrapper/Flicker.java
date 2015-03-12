package imageScrapper;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import admin.ModelData;
import admin.ModelManager;

import com.aetrion.flickr.*;
import com.aetrion.flickr.photos.*;

public class Flicker {

	public static final void main(String[] args) {
		try {
			Vector<ModelData> models = ModelManager.getAllModels(new File("3-29-2012.dat"));
			ModelManager.refineModels(models);
			Hashtable<String,Vector<ModelData>> words = ModelManager.getAllWordModels(models);
			
			Transport t = new REST();
			Flickr f = new Flickr("c1942994694d39d889488fed4ae149b4", "99871fed57f624ee", t);
			
			PhotosInterface psi = f.getPhotosInterface();
			
			File dirFlickr = new File("flickr");
			if(!dirFlickr.exists()) {
				dirFlickr.mkdir();
			}
			
			for(String word : words.keySet()) {
				SearchParameters search = new SearchParameters();
				search.setText(word);
				PhotoList list = psi.search(search, 10, 1);
				
				System.out.println("Retrieving word " + word);
				
				File dirImage = new File(dirFlickr + "/" + word);
				if(!dirImage.exists()) {
					dirImage.mkdir();
				}
				
				int i=0;
				for (Iterator<Photo> iterator = list.iterator(); iterator.hasNext();) {
				    try {
					    Photo photo = iterator.next();
					    String imageURL = photo.getMediumUrl();
					    URL url = new URL(imageURL);
					    final BufferedImage image = ImageIO.read(url.openStream());
					    
					    /*
					    JPanel comp = new JPanel() {
					    	public void paintComponent(Graphics g) {
					    		super.paintComponent(g);
					    		g.drawImage(image, 0, 0, this);
					    	}
					    };				    
					    JFrame frame = new JFrame();
					    frame.setSize(400, 400);
					    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					    frame.add(comp);
					    frame.setVisible(true);
					    */
					    
					    ImageIO.write(image, "png", new File(dirImage + "/" + i + ".png"));
					    i++;
				    } catch(Exception e) {
				    	e.printStackTrace();
				    }
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
