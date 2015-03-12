package animals.server;

import java.io.*;
import java.net.*;
import java.util.Vector;

import animals.Animal;

public class Server {

	public static Vector<Animal> world = new Vector<Animal>();
	
	public static void main(String[] args) {
		try {
			int port = 8234;
			
			// Create server.
			ServerSocket server = new ServerSocket(port);
			while(true) {
				try {
					// Connect to a new user.
					Socket s = server.accept();
					InputStream input = s.getInputStream();
					OutputStream output = s.getOutputStream();
					switch(input.read()) {
						case 'v': 
							// This is a standard client viewer.
							(new ClientViewerServer(input,output,world)).start();
						break;
						default: break;
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
