package admin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import modelManager.Guess;

public class Video {

	public static void main(String[] args) {
		Vector<ModelData> models = ModelManager.getAllModels(new File("9-25-13_models.dat"));
		ModelManager.refineModels(models);	
		
		Vector<Guess> guesses = Guess.getAllGuesses(new File("9-25-13_guesses.dat"));
		Guess.linkModels(models, guesses);
		
		int count = 0;
		for(ModelData model : models) {
			double ratio = model.correctGuesses / (double)(model.correctGuesses + model.incorrectGuesses);

			if(ratio > .8 && model.correctGuesses > 3) {
				count++;
				
				BufferedImage img = model.model.thumbnail(null, 800, 600, 30);
				try {
					ImageIO.write(img, "png", new FileImageOutputStream(new File("C:/Users/bkiev_000/Desktop/spVideo/frames/" + count + ".png")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(count);
	}
}
