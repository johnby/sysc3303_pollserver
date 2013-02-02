package listeners;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import poll.PollManager;

public class PollListener extends Thread {

	private ServerSocket serverSocket = null;
	private int listeningPort = 7777;
	private boolean active = true;
	private PollManager pollManager = null;
	
	public PollListener(int port, PollManager manager) throws IOException
	{
		this.pollManager = manager;
		this.listeningPort = port;
		serverSocket = new ServerSocket(this.listeningPort);
	}

	public void run()
	{
		while(active)
		{
			try {
				Socket newSocket = serverSocket.accept();
				pollManager.addNewConnection(newSocket);
			} catch (SocketException e2)
			{
				active = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void quit()
	{
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pollManager.shutdown();
		this.active = false;
	}
	
}
