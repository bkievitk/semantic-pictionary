package animals.client;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

import javax.swing.*;

import animals.Animal;

public class ClientViewer extends JPanel implements KeyListener {

	private static final long serialVersionUID = 3318910132852989716L;
	
	private Vector<Animal> animals;
	private InputStream input;
	private OutputStream output;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.add(new ClientViewer("localhost",8234));
		frame.setVisible(true);
	}
	
	public ClientViewer(String host, int port) {
		try {
			
			System.out.println("Trying to connect to [" + host + "] on port " + port);
			Socket socket = new Socket(host, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			
			// This indicates what type of listener this is.
			output.write('v');
			output.flush();
			
			System.out.println("Connection established.");
			
			Thread readThread = new Thread() {
				public void run() {
					
					System.out.println("Starting reader.");
					
					// Infinitely read frames.
					while(true) {
						try {
							// Read one frame.
							String frameData = "";
							int charRead;
							
							// Frames end on the '~' character.
							while((charRead = input.read()) != '~') {
								
								// The stream has died, bail out.
								if(charRead < 0) {
									System.out.println("Stream has died.");
									return;
								}
								
								// Add data.
								frameData += (char)charRead;
							}

							System.out.println("Frame read.");
							
							// Split into animal pieces.
							// Then parse all animals.
							String[] elements = frameData.split("&");
							Vector<Animal> animals = new Vector<Animal>();
							for(String element : elements) {
								animals.add(Animal.decodeString(element, null));
							}
							
							// Set this as the rendered world.
							setWorld(animals);
						} catch(IOException e) {
							// Something bad happened but try to read the next frame.
						}
					}
				}
			};
			readThread.start();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setWorld(Vector<Animal> animals) {
		this.animals = animals;
		repaint();
	}
	
	public synchronized void renderWorld(Graphics g) {
		if(animals != null) {
			for(Animal animal : animals) {
				animal.renderAnimal(g);
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		renderWorld(g);
	}

	public void keyPressed(KeyEvent arg0) {
		try {
			output.write('+');
			output.write(arg0.getKeyCode());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void keyReleased(KeyEvent arg0) {
		try {
			output.write('-');
			output.write(arg0.getKeyCode());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void keyTyped(KeyEvent arg0) {
	}
}
