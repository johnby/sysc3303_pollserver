import java.io.IOException;
import java.net.SocketException;

import poll.PollManager;

import listeners.PollListener;
import listeners.VoteListener;




public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		// TO-DO: take port as input parameter
		int port = 7777;
		int votePort = 7778;
		
		PollManager manager = new PollManager();
		
		PollListener pollListener = null;
		try {
			pollListener = new PollListener(port, manager);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		VoteListener voteListener = null;
		try {
			voteListener = new VoteListener(votePort, manager);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		pollListener.start();
		voteListener.start();
		
		try {
			pollListener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			voteListener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally
		{
			if(voteListener != null)
			{
				voteListener.quit();
			}
			
			if(pollListener != null)
			{
				pollListener.quit();
			}
		}
		
	}

}
