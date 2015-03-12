package templates;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;
import modelTools.PrimitiveInstance;



public abstract class WindowPrimitiveEdit extends JPanel implements ActionListener, ChangeListener {

	private static final long serialVersionUID = -236671868437970037L;
	
	public GeonModel model;
	public boolean disableSelect = false;
	public boolean disableSetVals = false;
	public Vector<JComponent> components = new Vector<JComponent>();
	public MyColorPanel colorSelect = new MyColorPanel();
	
	public WindowPrimitiveEdit(GeonModel model) {
		this.model = model;
		components.add(colorSelect);
	}
	
	public void actionPerformed(ActionEvent arg0) {
	}

	public void setValsFrame() {
		if(!disableSetVals) {
			PrimitiveInstance object = model.getSelected();
			if(object != null) {
				disableSelect = true;
				setVals();
				disableSelect = false;
			}
		}
	}
	
	protected abstract void setVals();
	
	public void stateChanged(ChangeEvent arg0) {
		// When the selected object changes, change what is enabled.
		setEnabledState();
		setValsFrame();
	}
	
	public void setEnabledState() {
		// Check if an object is selected then turn on or off all buttons.
		PrimitiveInstance instance = model.getSelected();
		boolean setVal = instance != null;
		for(JComponent component : components) {			
			component.setEnabled(setVal);
		}
	}

	public static Hashtable<Integer, JComponent> buildSlider(int min, int max, int step, String tag) {
		Hashtable<Integer, JComponent> slider = new Hashtable<Integer, JComponent>();
		for(int i=min;i<max;i+=step) {
			slider.put(i+1, new JLabel(i + tag));
		}
		return slider;
	}
}
