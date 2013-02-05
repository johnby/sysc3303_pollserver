package listeners;

import poll.PollManager;

public class VoteListener extends Thread {

	private int port = 7778;
	private PollManager pollManager = null;
	
	public VoteListener(int port, PollManager manager) {
		this.port = port;
		this.pollManager = manager;
	}
	
	public void quit()
	{
		
	}
	
}
