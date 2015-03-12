package templates;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;


public class WindowAddPrimitive extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 6834283799187836902L;
	
	protected Vector<JButton> buttons = new Vector<JButton>();
	protected GeonModel model;

	public WindowAddPrimitive(GeonModel model) {
		this.model = model;
	}
	
	public void stateChanged(ChangeEvent arg0) {
		// When the selected object changes, change what is enabled.
		setEnabledState();
	}
	
	public void setEnabledState() {
		// Check if an object is selected then turn on or off all buttons.
		boolean setVal = model.getSelected() != null;
		for(JButton b : buttons) {
			b.setEnabled(setVal);
		}
	}
}
