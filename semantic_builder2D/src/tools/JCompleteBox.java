package tools;

import java.awt.Color;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListDataEvent;

/**
 * A combo box with auto-complete
 * READY
 * @author brentkk
 */

public class JCompleteBox extends JComboBox implements KeyListener, InputMethodListener {
	
	private static final long serialVersionUID = 1341118610057853879L;
	private boolean keyTyped = false;
	
	// All words in dictionary.
	public static WordComplete BASIC_DATABASE = WordList.makeList();
	public WordComplete database;
	
	/**
	 * Initialize by loading word set.
	 */
	public JCompleteBox() {
		init(BASIC_DATABASE);
	}
	
	public JCompleteBox(WordComplete database) {
		init(database);
	}
	
	public void init(WordComplete database) {
		this.database = database;
		this.getEditor().addActionListener(this);
		this.getEditor().getEditorComponent().addInputMethodListener(this);
		
		// They can type in it.
		this.setEditable(true);
		
		// Listen for keys.
		this.getEditor().getEditorComponent().addKeyListener(this);
		this.setFocusable(true);
	}

	/**
	 * Get the text in the main area.
	 * @return Text.
	 */
	public String getText() {
		return ((JTextField)this.getEditor().getEditorComponent()).getText();
	}
	
	/**
	 * Catch when you have typed a letter.
	 */
	public void keyReleased(KeyEvent arg0) {
		keyTyped = true;
		changed();
	}
	
	/**
	 * State changed. Update options.
	 */
	public void changed() {
		String text = getText();
		
		// Clear current items.
		removeAllItems();
		this.addItem(text);

		// Only add options if you have 3 or more letters.
		if(text.length() > 1) {

			// Add all options.
			Vector<String> others = database.complete(text);
			boolean match = false;
			if(others != null && others.size() > 0) {
				for(String str : others) {
					if(!str.equals(text)) {
						this.addItem(str);
					} else {
						match = true;
					}
				}
			}
			validWord(match);
		} else {
			validWord(false);
		}

		// Refresh popup list.
		this.getUI().setPopupVisible(this, false);		
		this.getUI().setPopupVisible(this, true);		
	}
	
	/**
	 * Check if this is a word in the list.
	 * @param isValid
	 */
	public void validWord(boolean isValid) {
		if(isValid) {
			this.getEditor().getEditorComponent().setForeground(Color.BLACK);
		} else {
			this.getEditor().getEditorComponent().setForeground(Color.RED);
		}
	}

	/**
	 * Don't update when automatically updating to avoid infinite loop.
	 */
	public void contentsChanged(ListDataEvent e) {
		super.contentsChanged(e);
		
		if(e.getIndex0() == -1) {
			if(keyTyped) {
				keyTyped = false;
			} else {
				keyTyped = true;
				changed();
			}
		}
	}
	
	public void keyPressed(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	public void caretPositionChanged(InputMethodEvent arg0) {}
	public void inputMethodTextChanged(InputMethodEvent arg0) {}

	
}
