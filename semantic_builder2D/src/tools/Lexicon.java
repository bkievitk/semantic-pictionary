/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import iomanager.IOWeb;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Scanner;
import javax.swing.*;


/**
 *
 * @author Gabriel
 */
public class Lexicon {
    
    private Random random = new Random();
    
    /*
         * DEFINITIONS
         * 
         * "concepts": the words in the first field of data/concFeats.txt. "accordion", "airplane", "alligator", etc.
         * "features": slightly cleaned-up versions of the phrases in the second field of data/concFeats.txt. "a musical instrument", "associated with polkas", etc.
         * "clue words": the individual words that occur in features. If the concept "accordion" has the feature "a musical instrument", then
         *               "a", "musical", and "instrument" are each clue words for the concept "accordion".
         * 
         * A WeightedConcept is just a key-value pair pairing a string (a concept) with a double.
         * A WeightedFeature is just a key-value pair pairing a string (a feature) with a double.
         */

        // The cluewordsToConceptCounts dictionary pairs clue words (keys) with lists of WeightedConcepts. The number in each WeightedConcept is the number of times that concept co-occurs with the key.
        // Example key-value pair: <"air", {[accordion, 1], [bagpipe, 1], [balloon, 3], [baton, 1], [clarinet, 1], [flute, 1], [harmonica, 1], [saxophone, 1], [trombone, 1], [trumpet, 1], [tuba, 1], [whistle, 1]}>
        // (In the above example, balloon has value 3 because there are 3 lines in data/concFeats.txt that pair the concept "balloon" with a feature containing the word "air": associated_with_hot_air_balloons, requires_air, and requires_hot_air.)
        private Map<String, ArrayList<WeightedConcept>> cluewordsToConceptCounts = new HashMap<String, ArrayList<WeightedConcept>>();
        
                // Key: concept. Value: Number of lines in data/concFeats.txt that begin with that concept.
        // Example key-value pair: <"accordion", 9>
        private Map<String, Integer> conceptFrequencies = new HashMap<String, Integer>();


        // Key: Clue word. Value: Number of lines in data/concFeats.txt that contain that clue word.
        // Example key-value pair: <"instrument", 19>
        private Map<String, Integer> cluewordFrequencies = new HashMap<String, Integer>();


        // Key: concept. Value: List of the features of that concept.
        // Example key-value pair: <"accordion", {"a musical instrument", "associated with polkas", "has buttons", "has keys", "produces music", "is loud", "requires air", "used by moving bellows", "worn on chest"}>
        private Map<String, ArrayList<String>> conceptsToFeatures = new HashMap<String, ArrayList<String>>();


        // Key: feature. Value: List of the concepts having that feature.
        // Example key-value pair: <"has keys", {"accordion, "clarinet", "flute", "piano", "saxophone", "typewriter"}>
        private Map<String, ArrayList<String>> featuresToConcepts = new HashMap<String, ArrayList<String>>();


        // Key: a prefix (of any length) of a word of a feature. Value: List of features containing a word that starts with that prefix.
        // Example key-value pair: <"mus", {"a musical instrument", "produces music", "used for music", "used by musicians", "is musty", "used for classical music", "used for listening to music", "a music"}>
        private Map<String, ArrayList<String>> featureTrie = new HashMap<String, ArrayList<String>>();


        private ArrayList<String> features = new ArrayList<String>();     // List of all features in data\concFeats.txt.
        private ArrayList<String> concepts = new ArrayList<String>();     // List of all concepts in data\concFeats.txt.
        Pattern whitespace = Pattern.compile("\\s+");
        
        /// Lexicon constructor. All this does is initialize the dictionaries in the above member variables with data from data\concFeats.txt.
        public Lexicon() throws IOException
        {
            // Initialize dictionaries.
            String lastConcept = "";
            String currentConcept = "";
            
            URL url = new URL(IOWeb.webHost + "data/concFeats.txt");
            ArrayList<String> concFeatsLines = readAllLines(url.openStream());
            
            for (int j = 0; j < concFeatsLines.size(); j++)
            {
                String line = concFeatsLines.get(j);

                if ("".equals(line)) continue;
                String[] tokens = line.split("\t");
                currentConcept = tokens[0];
                if (currentConcept.contains("_")) continue;             // Ignore all multi-word concepts.
                
                if (!currentConcept.equals(lastConcept))
                {
                    conceptFrequencies.put(currentConcept, 0);
                    conceptsToFeatures.put(currentConcept, new ArrayList<String>());
                }
                conceptFrequencies.put(currentConcept, 1 + conceptFrequencies.get(currentConcept));
                
                //int numSubjects = Integer.parseInt(tokens[2]);
                String feature = tokens[1];
                int dashAt = feature.indexOf("_-_");
                if (dashAt != -1) feature = feature.substring(dashAt + 3);  // If substring "_-_" appears in a feature, delete everything before it.
                                feature = feature.replace("-", "").replace("__", "_");      // Delete dashes, and replace double-underscores with underscores.
                feature = feature.replace("_", " ").toLowerCase();              // Replace underscores with spaces.
                if (feature.length() < 3) continue;                           // Ignore features shorter than 3 characters.
                
                if (!features.contains(feature)) features.add(feature);
                conceptsToFeatures.get(currentConcept).add(feature);
                
                String[] cluewords = feature.split(" ");
                for (int i = 0; i < cluewords.length; i++)
                {
                    String clueword = cluewords[i];
                    if (!cluewordFrequencies.containsKey(clueword)) cluewordFrequencies.put(clueword, 0);
                    if (!cluewordsToConceptCounts.containsKey(clueword)) cluewordsToConceptCounts.put(clueword, new ArrayList<WeightedConcept>());
                    
                    ArrayList<WeightedConcept> wcList = cluewordsToConceptCounts.get(clueword);
                    WeightedConcept wc = getWeightedConcept(currentConcept, wcList);
                    if (wc == null)
                    {
                        wc = new WeightedConcept(currentConcept, new Double(0));
                        wcList.add(wc);
                    }
                    wc.weight++;

                    cluewordFrequencies.put(clueword, cluewordFrequencies.get(clueword) + 1);
                }
                lastConcept = currentConcept;
                
           } // end for j
            
            for (String key : conceptFrequencies.keySet())
            {
                concepts.add(key);
            }
            initFeatureTrieDictionary(features);
            initFeaturesToConceptsDictionary(conceptsToFeatures);
        }   // end fn
        
        
        /// Populate the featureTrie dictionary (see member variables at top of class for definition).
        /// Assumes "conceptsToFeatures" is already populated.
        private void initFeaturesToConceptsDictionary(Map<String, ArrayList<String>> conceptsToFeatures)
        {
            for (String concept : conceptsToFeatures.keySet())
            {
                for (String feature : conceptsToFeatures.get(concept))
                {
                    if (!featuresToConcepts.containsKey(feature)) featuresToConcepts.put(feature, new ArrayList<String>());
                    ArrayList<String> list = featuresToConcepts.get(feature);
                    if (!list.contains(concept)) list.add(concept);
                }
            }
        }
        
        
        /// Populate the featuresToConcepts dictionary (see member variables at top of class for definition).
        /// Assumes "features" is already populated.
        private void initFeatureTrieDictionary(ArrayList<String> features)
        {
            for (String f : features)
            {
                String[] words = f.split(" ");
                for (int w = 0; w < words.length; w++)
                {
                    String word = words[w] + " ";
                    for (int i = 1; i <= word.length(); i++)
                    {
                        String prefix = word.substring(0, i);
                        if (!featureTrie.containsKey(prefix)) featureTrie.put(prefix, new ArrayList<String>());
                        
                        ArrayList<String> list = featureTrie.get(prefix);
                        if (!list.contains(f)) list.add(f);
                    }
                }
            }
        }
        
        /// Returns a random concept.
        public String getRandomConcept()
        {
            return concepts.get(random.nextInt(concepts.size()));
        }

        /// Returns a random feature.
        public String getRandomFeature()
        {
            return features.get(random.nextInt(features.size()));
        }

        /// Returns the first #numToGet features of the provided concept.
        @SuppressWarnings("unchecked")
		public ArrayList<String> getFeatures(String concept, int numToGet)
        {
            ArrayList<String> someFeatures = new ArrayList<String>();
                        
            ArrayList<String> featureList = conceptsToFeatures.get(concept);
            if(featureList != null) {            
	            ArrayList<String> allFeatures = (ArrayList<String>)featureList.clone();
	            for (int i = 0; i < numToGet; i++)
	            {
	                if (allFeatures.isEmpty()) break;
	                int index = random.nextInt(allFeatures.size());
	                someFeatures.add(allFeatures.get(index));
	                allFeatures.remove(index);
	            }
            }
            return someFeatures;    
        }

        /// Given a prefix string, returns a list of the top #numToGet features that match that prefix, using a formula that rewards short features
        /// and features with words that match the prefix.
        /// For example, if the user types "col", this function will be called with stringToMatch="col", numToGet=6,
        /// and might return {"is cold", "a colour", "is colourful", "has a collar", "used in the cold", "lives in a colony"}.
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public ArrayList<String> getMatchingFeatures(String stringToMatch, int numToGet)
        {
            if ("".equals(stringToMatch)) return new ArrayList<String>();

            Map<String, Double> weightedFeatures = new HashMap<String, Double>();
            for (String f : features)
            {
                // Assign a small value to every feature, such that short features have higher values
                weightedFeatures.put(f, (100 - f.length())/100.0 + (random.nextDouble() / 10000));
            }

            Matcher matcher = whitespace.matcher(stringToMatch);
            stringToMatch = matcher.replaceAll(" ").trim();
            String[] wordsToMatch = stringToMatch.split(" ");

            for (int i = 0; i < wordsToMatch.length; i++)
            {
                String toMatch = wordsToMatch[i] + " ";

                // If you are a feature, you get 1 point for every letter of each prefix that you match

                for (int j = 1; j < toMatch.length(); j++)
                {
                    String prefix = toMatch.substring(0, j);
                    if (featureTrie.containsKey(prefix))
                    {
                        ArrayList<String> featureTrieList = featureTrie.get(prefix);
                        for (String f : featureTrieList)
                        {
                            weightedFeatures.put(f, weightedFeatures.get(f) + 1);
                        }
                    }
                }
            }

            // Now that we have a dictionary with each feature and its score, sort and return the top #numToGet features
            ArrayList<WeightedFeature> list = new ArrayList<WeightedFeature>();
            for (Map.Entry<String, Double> wf : weightedFeatures.entrySet())
            { 
                list.add(new WeightedFeature(wf.getKey(), wf.getValue()));
            }
            Collections.sort(list, new Comparator(){
            //@Override
                public int compare(Object o1, Object o2) {
                    WeightedFeature wf1 = (WeightedFeature) o1;
                    WeightedFeature wf2 = (WeightedFeature) o2;
                   return wf2.weight.compareTo(wf1.weight);
                }
            });

            ArrayList<String> topFeatures = new ArrayList<String>();    //Sort went forwards or backwards?
            int max = Math.min(weightedFeatures.size(), numToGet);
            for (int i = 0; i < max; i++)
            {
                topFeatures.add(list.get(i).feature);
            }
            return topFeatures;
        }

        public String makeGuess(JFormattedTextField[] features, String realAnswer) {
        	String[] strFeatures = new String[features.length];
        	for(int i=0;i<features.length;i++) {
        		strFeatures[i] = features[i].getText();
        	}
        	return makeGuess(strFeatures,realAnswer);
        }
        
        /// Given the textboxes that the player typed clues into, and the actual correct answer, return the computer's guess.
        /// The computer cheats a little: If the actual correct answer is similar enough to the clues the player gave, the computer
        /// will just guess the actual correct answer.
        public String makeGuess(String[] features, String realAnswer)
        {
            ArrayList<String> clues = new ArrayList<String>();
            for (int i = 0; i < features.length; i++)
            {
                String clue = features[i].trim().toLowerCase().replace("-", " ");
                Matcher matcher = whitespace.matcher(clue);
                clue = matcher.replaceAll(" ");
                clues.add(clue);
            }

            ArrayList<WeightedConcept> guesses = convertCluesToRankedGuesses(clues);
            
            int threshold = 3;

            // Make a guess!
            for (int i = 0; i < threshold; i++)
            {
                String guess = guesses.get(i).concept;
                if (guess.equals(realAnswer)) return guess;
            }

            return guesses.get(0).concept;
        }

        /// Given a list of clues, returns a list of WeightedConcepts (concept/double pairs), sorted with the "best" guesses on top.
        /// For example, if the clues were "has big teeth", "is a green animal", "is scary", "lives in florida", then CluesToRankedGuesses
        /// would return a list of WeightedConcepts with the concept "alligator" near the top, because this list includes features of "alligator",
        /// and because the features of "alligator" contain many of the same words as appear in the list of clues.
        @SuppressWarnings({ "unchecked", "rawtypes" })
		private ArrayList<WeightedConcept> convertCluesToRankedGuesses(ArrayList<String> clues)
        {

            // Initialize a list of all concepts
            Map<String, Double> conceptScores = new HashMap<String, Double>();
            for (String concept : conceptFrequencies.keySet()) 
            { 
                conceptScores.put(concept, new Double(0)); 
            }

            // Look at clues
            for (String clue : clues)
            {
                if (featuresToConcepts.containsKey(clue))
                {
                    // If the clue is identical to one of our known features, every concept sharing that feature gets a point

                    for (String concept : featuresToConcepts.get(clue))
                    {
                        conceptScores.put(concept, conceptScores.get(concept) + 1);
                    }
                }
                else
                {
                    // If the clue is not a feature we know, we can still give partial credit to concepts that have similar clues

                    String[] clueTokens = clue.split(" ");
                    updateConceptScoresOnAKeywordBasis(clueTokens, conceptScores);  // Make sure it updates conceptScores as if by reference.
                }
            }

            ArrayList<WeightedConcept> list = new ArrayList<WeightedConcept>();
            for (Map.Entry<String, Double> wc : conceptScores.entrySet())
            { 
                list.add(new WeightedConcept(wc.getKey(), wc.getValue())); 
            }
            Collections.sort(list, new Comparator(){
            //@Override
                public int compare(Object o1, Object o2) {
                    WeightedConcept wc1 = (WeightedConcept) o1;
                    WeightedConcept wc2 = (WeightedConcept) o2;
                   return wc2.weight.compareTo(wc1.weight);
                }
            });
            
            return list;
        }

        /// Inner method called only by CluesToRankedGuesses.
        /// Given a list of all of the words appearing in a clue, uses a formula to give "partial credit" to concepts that have features containing some of the same words.
        private void updateConceptScoresOnAKeywordBasis(String[] clueTokens, Map<String, Double> conceptScores)
        {
            //double log2 = Math.log(2.0);
            
            for (int i = 0; i < clueTokens.length; i++)
            {
                String clueword = clueTokens[i];

                // Calculate the pointwise mutual information of clueword and each concept
                if (cluewordsToConceptCounts.containsKey(clueword))
                {
                    ArrayList<WeightedConcept> conceptCounts = cluewordsToConceptCounts.get(clueword);

                    if (conceptCounts.size() < 300)
                    {
                        for (WeightedConcept wc : conceptCounts)
                        {
                            Double counts = wc.weight;
                            int cluewordFreq = cluewordFrequencies.get(clueword);
                            int conceptFreq = conceptFrequencies.get(wc.concept);
                            double pmi = counts / (cluewordFreq * conceptFreq);
                            conceptScores.put(wc.concept, conceptScores.get(wc.concept) + pmi * (3.0 - (i * .27)));
                        }
                    }
                }
            }
        }
        
        
        private WeightedConcept getWeightedConcept(String concept, ArrayList<WeightedConcept> list)
        {
            for (WeightedConcept wc : list) {
                if (wc.concept.equals(concept)) return wc;
            }
            return null;
        }
        
        private ArrayList<String> readAllLines(InputStream stream) throws IOException
        {
            ArrayList<String> lines = new ArrayList<String>();
            Scanner scanner = new Scanner(stream);
            try {
              while (scanner.hasNextLine()){
                lines.add(scanner.nextLine());
              }
            }
            finally{
              scanner.close();
            }
            return lines;
        }
}
