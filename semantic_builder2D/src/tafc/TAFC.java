package tafc;

import java.awt.*;

import javax.swing.*;

public class TAFC extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3375149684326681112L;

	/*
	private JLabel targetWord;
	private JLabel targetDomain;
	private JButton firstWord;
	private JButton secondWord;
	*/
	
	public static final void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new TAFC());
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public TAFC() {
		this.setLayout(new BorderLayout());
		
		/*
		targetWord = new JLabel();
		targetDomain = new JLabel();
		firstWord = new JButton();
		secondWord = new JButton();
		*/
	}
}
