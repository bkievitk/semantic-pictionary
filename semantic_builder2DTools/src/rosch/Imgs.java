package rosch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;

import modelManager.Guess;
import admin.ModelData;
import admin.ModelManager;

public class Imgs {
	public static void main(String[] args) {
		Vector<Guess> guesses = Guess.getAllGuesses(new File("8-21-2014_guesses.dat"));
		Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
		ModelManager.refineModels(models);	
		Guess.linkModels(models, guesses);

		for(int i=0;i<models.size();i++) {
			if(models.get(i).correctGuesses < 1) {
				models.remove(i);
				i--;
			}
		}
		
		Hashtable<String,Vector<ModelData>> modelSort = ModelManager.getAllWordModels(models);
		
		for(String word : modelSort.keySet()) {
			ModelData md = modelSort.get(word).get(0);
			if(md != null) {
				BufferedImage back = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
				BufferedImage img = md.model.thumbnail(back, 200, 200, 15);
				try {
					ImageIO.write(img, "png", new File("spImgs/" + word + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
