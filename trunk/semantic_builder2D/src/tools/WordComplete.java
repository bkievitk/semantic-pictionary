package tools;

import java.util.*;

/**
 * This will suggest words to auto-complete.
 * READY
 * @author bkievitk
 */

public class WordComplete {
	
	// List of words as a tree with each letter a new depth.
	public Hashtable<Character,WordComplete> children = new Hashtable<Character,WordComplete>();
	public boolean isWord;
		
	/**
	 * Show results on terminal.
	 * @param str
	 */
	public void showComplete(String str) {
		Vector<String> result = complete(str);
		System.out.println("----- Results -----");
		if(result == null) {
			System.out.println("No results found.");
		} else {
			for(String s : result) {
				System.out.println("Complete with " + s);
			}
		}
	}
	
	/**
	 * Add a word to the word list.
	 * @param word
	 */
	public void addWord(String word) {
		addWord(word,0);
	}

	/**
	 * Add a word to the word list given that you are already at the deapth specified by the index.
	 * @param word
	 * @param index
	 */
	private void addWord(String word, int index) {
		if(index == word.length()) {
			// This is the last node of the word.
			isWord = true;
		}
		else {
			WordComplete next = children.get(word.charAt(index));
			
			if(next == null) {
				next = new WordComplete();
				children.put(word.charAt(index), next);
			}
			
			next.addWord(word,index+1);
		}
	}
	
	/**
	 * Complete this word with all possible options.
	 * @param word
	 * @return
	 */
	public Vector<String> complete(String word) {
		WordComplete last = getLastComplete(word);
		if(last == null) {
			return null;
		}
		return last.getRemaining(word);
	}
	
	/**
	 * Get all words at this node or any of its children.
	 * @param word
	 * @return
	 */
	private Vector<String> getRemaining(String word) {
		Vector<String> ret = new Vector<String>();
		if(isWord) {
			ret.add(word);
		}
		for(Character c : children.keySet()) {
			Vector<String> more = children.get(c).getRemaining(word + c);
			ret.addAll(more);
		}
		
		return ret;
	}
	
	/**
	 * Get the last node that is in the partial word given.
	 * @param word
	 * @return
	 */
	private WordComplete getLastComplete(String word) {
		WordComplete current = this;
		for(int i=0;i<word.length();i++) {
			current = current.children.get(word.charAt(i));
			if(current == null) {
				return null;
			}
		}
		return current;
	}
}
