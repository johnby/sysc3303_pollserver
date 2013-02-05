package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.w3c.dom.Document;

import poll.MessageFactory;
import poll.PollManager;
import poll.Vote;

public class VoteListener extends Thread {

	private int port = 7778;
	private PollManager pollManager = null;
	private boolean active = true;
	
	private DatagramSocket recSocket = null;
	
	public VoteListener(int port, PollManager manager) throws SocketException {
		this.port = port;
		this.pollManager = manager;
		recSocket = new DatagramSocket(this.port);
	}
	
	public void run()
	{
		while(this.active){
			byte data[] = new byte[200];
		    DatagramPacket recPacket = new DatagramPacket(data, data.length);
			try {
				recSocket.receive(recPacket);
				
				System.out.println("Server: VoteListener: message received.");
				
				String ip = recPacket.getAddress().toString();
				String contents = new String(recPacket.getData(),0,recPacket.getLength());
				
				Document doc = MessageFactory.stringToXmlDoc(contents);
				
				/*
				 * Vote message
				 */
				if(doc.getDocumentElement().getNodeName().equals("vote"))
				{
					String pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
					String selection = doc.getElementsByTagName("selection").item(0).getTextContent();
					
					System.out.println("Server: VoteListener: vote received.");
					
					this.pollManager.voteRecieved(new Vote(pollId, selection, ip));
				}
				
			} catch (IOException e) {
				
				if(this.recSocket.isClosed())
				{
					this.active = false;
				}
				else
				{
					e.printStackTrace();
				}
			}
		}
	}
		
	public void quit()
	{
		this.active = false;
		recSocket.close();
	}
}
