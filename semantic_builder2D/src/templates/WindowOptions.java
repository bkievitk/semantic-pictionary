package templates;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;

import tools.CompletionListener;

public class WindowOptions extends JPanel implements ActionListener, ChangeListener {
	
	private static final long serialVersionUID = -3563323983438293710L;

	public static int OBJECT_LIMIT = 50;
	
	private JButton undo = new JButton("undo");
	private JButton redo = new JButton("redo");
	private JButton complete = new JButton("complete");
	private JLabel confirm = new JLabel("Confirm Complete?");
	private JLabel error = new JLabel("");
	private JButton yes = new JButton("yes");
	private JButton no = new JButton("no");
	private JLabel count = new JLabel();
	private JTextArea longLabel = new JTextArea();
	
	private GeonModel model;
	private CreatorPanel cp;
	
	private Vector<CompletionListener> completionListeners = new Vector<CompletionListener>();
	
	public void setCreatorPanel(CreatorPanel cp) {
		this.cp = cp;
	}
	
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
	
	public WindowOptions(GeonModel model) {
		this.model = model;
		
		setLayout(new BorderLayout());
		JPanel options = new JPanel(new FlowLayout());
		add(options,BorderLayout.CENTER);
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		add(infoPanel,BorderLayout.SOUTH);
				
		error.setVisible(false);
		error.setForeground(Color.RED);
		options.add(error);
		
		confirm.setVisible(false);
		yes.setVisible(false);
		no.setVisible(false);
		options.add(confirm);
		options.add(yes);
		yes.addActionListener(this);
		options.add(no);
		no.addActionListener(this);		
		
		options.add(complete);
		complete.addActionListener(this);
		options.add(undo);
		undo.addActionListener(this);
		options.add(redo);
		redo.addActionListener(this);
		options.add(count);
		
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
		} else if(arg0.getSource() == complete) {
			setConfirm(true);
		} else if(arg0.getSource() == yes) {
			setConfirm(false);
			
			if(cp.word != null) {
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