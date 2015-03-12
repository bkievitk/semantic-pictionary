package imageScrapper;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;

import admin.ModelData;
import admin.ModelManager;
import tools.image.ImageDisk;
import tools.image.ImageNormalizer;
import tools.json.JSONArray;
import tools.json.JSONException;
import tools.json.JSONObject;

public class GoogleImage {
		
	public static int WIDTH = 100;
	public static int HEIGHT = 100;
		
	public static final void main(String[] args) {
		
		Vector<ModelData> models = ModelManager.getAllModels(new File("3-29-2012.dat"));
		ModelManager.refineModels(models);
		Hashtable<String,Vector<ModelData>> words = ModelManager.getAllWordModels(models);
		
		int i = 0;
		for(String word : words.keySet()) {
			System.out.println(word + " " + i + "/" + words.keySet().size());
			retrieveImages(word, new File("google"), 10, false);
			i++;
		}
	}
	
	/**
	 * Retrieve images from a Google search and place them in the given directory.
	 * Will create sub-directory to root named "name" and will fill with images named "name"x.jpg where x is the image number.
	 * @param name		Search item. Must be properly formatted for url.
	 * @param root		Directory to use as root to save to.
	 * @param number	Number of items to look up.
	 * @param overwrite	Overwrite the directory if it already exists.
	 */
	public static boolean retrieveImages(String name, File root, int number, boolean overwrite) {

		// Check if directory exists.
		File saveDir = new File(root.getAbsolutePath() + "/" + name);
		if(!saveDir.exists()) {
			saveDir.mkdir();
		} else if(!overwrite) {
			return false;
		}
		
		// Number of items successfully loaded.
		int myItem = 0;
		
		// Item on Google paging system.
		int theirItem = 0;
		
		// Loop until you have enough items and are told to return.
		while(true) {
			
			try {
				
				// Extended search parameters.
				// http://code.google.com/apis/ajaxsearch/documentation/reference.html#_class_GSearch
				
				// Connect to URL.
				// Search for "name".
				// Start on item "item"
				URL url = new URL("http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + name + "&start=" + theirItem);
				URLConnection connection = url.openConnection();

				// Set your referer name.
				connection.addRequestProperty("Referer", "http://www.my-ajax-site.com");

				// Read results.
				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}

				// Use JSON to connect.
				JSONObject json = new JSONObject(builder.toString());
				JSONObject responseData = json.getJSONObject("responseData");
				JSONArray responses = responseData.getJSONArray("results");
				
				for(int i=0;i<responses.length();i++) {
					JSONObject result = responses.getJSONObject(i);
					
					// Possibly use the image size information?
					//int width = result.getInt("width");
					//int height = result.getInt("height");
					Image img = ImageDisk.loadImageWeb(result.getString("unescapedUrl"));
					
					// Increment the count of their items.
					theirItem++;
					
					// If you were able to retrieve image.
					if(img != null) {
												
						// Set path name for new image and delete if it already exists.
						File imgName = new File(saveDir.getAbsolutePath() + "/" + name + "" + myItem + ".jpg");
						if(imgName.exists()) {
							imgName.delete();
						}
						
						// Place the image in the file.
						ImageIO.write(ImageNormalizer.normalizeSimple(img,WIDTH,HEIGHT), "jpg", imgName);

						// Increment image counter.
						myItem++;
						
						// End if you have enough images.
						if(myItem >= number) {
							return true;
						}
					}
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}		
}


