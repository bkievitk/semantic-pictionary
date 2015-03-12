package creator3DTree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.WordPair;


public class WindowOptions extends JPanel implements ActionListener, ChangeListener {

	private static final long serialVersionUID = -7373930384988747460L;

	public static int OBJECT_LIMIT = 50;
	
	private JButton undo = new JButton("undo");
	private JButton redo = new JButton("redo");
	private JButton complete = new JButton("complete");
	private JLabel confirm = new JLabel("Confirm Complete?");
	private JLabel error = new JLabel("");
	private JButton yes = new JButton("yes");
	private JButton no = new JButton("no");
	private JButton copy = new JButton("copy");
	private JButton paste = new JButton("paste");
	private Model3DTree model;
	private JLabel count = new JLabel();
	private JTextArea longLabel = new JTextArea();
	
	private WordPair word;
	
	private Vector<CompletionListener> completionListeners = new Vector<CompletionListener>();
	
	public void setConfirm(boolean set) {
		confirm.setVisible(set);
		yes.setVisible(set);
		no.setVisible(set);		
		undo.setVisible(!set);
		redo.setVisible(!set);
		complete.setVisible(!set);		
		count.setVisible(!set);
		error.setVisible(false);
		this.invalidate();
	}
	
	public WindowOptions(Model3DTree model, WordPair word) {
		this.model = model;
		this.word = word;
		
		setLayout(new FlowLayout());
		
		error.setVisible(false);
		error.setForeground(Color.RED);
		add(error);
		
		confirm.setVisible(false);
		yes.setVisible(false);
		no.setVisible(false);
		add(confirm);
		add(yes);
		yes.addActionListener(this);
		add(no);
		no.addActionListener(this);		
		
		add(complete);
		complete.addActionListener(this);
		add(undo);
		undo.addActionListener(this);
		add(redo);
		redo.addActionListener(this);
		add(count);
		
		model.addUpdateListener(this);
		updateCount();
	}

	public void addCompletionListener(CompletionListener l) {
		completionListeners.add(l);
	}
	
	public void removeCompletionListener(CompletionListener l) {
		completionListeners.remove(l);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == undo) {
			model.undo();
		}
		else if(arg0.getSource() == redo) {
			model.redo();
		} else if(arg0.getSource() == copy) {
			StringSelection stringSelection = new StringSelection( model.toReduced() );
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents( stringSelection, null );
		} else if(arg0.getSource() == paste) {
			
			// Load from clipboard.
			String result = "";
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable contents = clipboard.getContents(null);
			boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if ( hasTransferableText ) {
				try {
					// Try to load the model from an XML file.
					result = (String)contents.getTransferData(DataFlavor.stringFlavor);
					
					model.fromReduced(result);
					//model.fromXML(result);
				}
				catch (UnsupportedFlavorException ex){
					//highly unlikely since we are using a standard DataFlavor
					System.out.println(ex);
					ex.printStackTrace();
				}
				catch (IOException ex) {
					System.out.println(ex);
					ex.printStackTrace();
				}
			}
		} else if(arg0.getSource() == complete) {
			setConfirm(true);
		} else if(arg0.getSource() == yes) {
			setConfirm(false);
			
			if(word != null) {
				if(completionListeners.get(0).isComplete()) {
					for(CompletionListener l : completionListeners) {
						l.actionComplete();
					}
				} else {
					error.setText("[Invalid]");
					error.setForeground(Color.RED);
					error.setVisible(true);
					this.invalidate();
				}
				
			} else {
				error.setText("No word was found on initialization.");
				error.setForeground(Color.RED);
				error.setVisible(true);
				this.invalidate();
			}
		} else if(arg0.getSource() == no) {
			setConfirm(false);
		}
	}

	public void stateChanged(ChangeEvent arg0) {
		updateCount();
	}
	
	public void updateCount() {
		
		longLabel.setText(model.toXML());
		longLabel.setPreferredSize(new Dimension(50,20));
		
		int numObjects = model.countObjects();
		if(numObjects > OBJECT_LIMIT) {
			count.setForeground(Color.RED);
		} else {
			count.setForeground(Color.WHITE);
		}
		count.setText("Number of objects: " + numObjects);
	}
}
