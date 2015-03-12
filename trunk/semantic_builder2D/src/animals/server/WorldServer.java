package animals.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class WorldServer {

	public LinkedList<IndividualConnection> availableThreads = new LinkedList<IndividualConnection>();
	public Semaphore threadLock = new Semaphore(1);
	
	public static void main(String[] args) {
		WorldServer server = new WorldServer();
		server.start(9748);
	}
	
	public WorldServer() {
		
	}
		
	public void start(int port) {
	
		
		try {
			// Create server.
			ServerSocket server = new ServerSocket(port);
			while(true) {
				
				// Connect to a new user.
				Socket s = server.accept();
				
				try {
					
					// Get access to the thread queue.
					threadLock.acquire();
					
					if(availableThreads.size() > 0) {
						// Use available thread.
						IndividualConnection connection = availableThreads.removeLast();
						connection.initialize(s);
					} else {
						// No available threads.
						IndividualConnection connection = new IndividualConnection(this);
						connection.initialize(s);
						connection.start();
					}
					
					threadLock.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
