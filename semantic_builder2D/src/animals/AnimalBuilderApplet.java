package animals;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AnimalBuilderApplet extends JApplet {

	private static final long serialVersionUID = 6177638882013108446L;
	
	public static void main(String[] args) {
		
		
		Object[] options = {"Limited (player)",
        					"Full (admin)"};
		
		int n = JOptionPane.showOptionDialog(null,
			"What type of editor would you like?",
			"Select editor type",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,     //do not use a custom Icon
			options,  //the titles of buttons
			options[0]); //default button title

		JFrame frame = new JFrame();
		frame.setSize(800,500);
		frame.add(new AnimalBuilder(n == 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void start() {
		add(new AnimalBuilder(false));
	}
}
