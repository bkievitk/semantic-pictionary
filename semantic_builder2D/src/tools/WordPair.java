package tools;

/**
 * This is just a quick association of a word with an id.
 * It is used for database management purposes.
 * READY
 * @author bkievitk
 */

public class WordPair {
	
	// Store word and id.
	public String description;
	public int id;
	
	/**
	 * Create with description and id.
	 * @param description
	 * @param id
	 */
	public WordPair(String description, int id) {
		this.description = description;
		this.id = id;
	}

	/**
	 * Print parts.
	 */
	public String toString() {
		return description + " (" + id + ")";
	}
}
