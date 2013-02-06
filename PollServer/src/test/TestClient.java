package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import listeners.PollListener;
import listeners.VoteListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import poll.MessageFactory;
import poll.PollManager;

public class TestClient {

	PollListener pollListener = null;
	PollManager manager = null;
	VoteListener voteListener = null;
	
	int port = 7775;
	int votePort = 7778;
	
	BufferedReader reader = null;
	PrintWriter writer = null;

	/*
	 * Sets up the server to handle connections.
	 */
	@Before
	public void setUp()
	{
		int port = this.port;
		
		manager = new PollManager();
		
		pollListener = null;
		try {
			pollListener = new PollListener(port,manager);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			voteListener = new VoteListener(votePort, manager);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pollListener.start();
		voteListener.start();
	}
	
	/*
	 * Closes down the server safely.
	 * Watch out for hanging threads!
	 */
	@After
	public void tearDown()
	{
		pollListener.quit();
		manager = null;
		voteListener.quit();
		
		try {
			pollListener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally
		{
			pollListener.quit();
		}
		
		try {
			voteListener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally
		{
			voteListener.quit();
		}
	}
	
	/*
	 * Establish a TCP connection with the server.
	 * MAKE SURE YOU CLOSE SOCKET AFTER
	 * inits this.read and this.writer
	 */
	private Socket connectToServer() throws IOException
	{
		Socket socket = null;
		try {
			socket = new Socket("localhost", this.port);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// setup reader
		InputStream inputstream = socket.getInputStream();
	    InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
	    this.reader = new BufferedReader(inputstreamreader);
	    
	    // setup writer
	    this.writer = new PrintWriter(socket.getOutputStream(), true);
	    
	    return socket;
	}
	
	private DatagramPacket getVoteDatagramPacket(String message)
	{
		DatagramPacket sendPacket = null;
		try {
			sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getLocalHost() , this.votePort);
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		return sendPacket;
	}
	
	@Test
	public void testAdminConnectToServer()
	{
		// Connect to server
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Check if connection is successful
		assertNotNull(socket);
		assertTrue(socket.isConnected());

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAdminGetQuestions()
	{
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));

		// wait for validation
		boolean recMessage = false;

		String input = null;
		try {
			while(!recMessage && (input=this.reader.readLine()) != null)
			{
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "questions");
					recMessage = true;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAdminCreatePoll()
	{

		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertNotNull(pollId);
	
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAdminPauseReply()
	{

		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertNotNull(pollId);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getPausePollMessage(pollId));
		
		String pollIdCheck = null;
		
		boolean continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "pausePollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAdminResumePoll()
	{
		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertNotNull(pollId);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getPausePollMessage(pollId));
		
		String pollIdCheck = null;
		
		boolean continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "pausePollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getResumePollMessage(pollId));
		
		pollIdCheck = null;
		
		continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "resumePollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);
	
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAdminStopPoll()
	{

		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertNotNull(pollId);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getStopPollMessage(pollId));
		
		String pollIdCheck = null;
		
		boolean continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "stopPollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testVoterVote()
	{
		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// poll is now created - attempt to vote on it
		DatagramSocket sendSocket = null;
		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		try {
			sendSocket.send(getVoteDatagramPacket(MessageFactory.getVoteMessage(pollId, "a")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		sendSocket.close();
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testVote()
	{
		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// poll is now created - attempt to vote on it
		DatagramSocket sendSocket = null;
		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String message = MessageFactory.getVoteMessage(pollId, "a");

		DatagramPacket sendPacket = null;
		try {
			sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getLocalHost() , this.votePort);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		sendSocket.close();
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPoll()
	{
		String pollId = null;
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
					pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		assertNotNull(pollId);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getPausePollMessage(pollId));
		
		String pollIdCheck = null;
		
		boolean continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "pausePollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getResumePollMessage(pollId));
		
		pollIdCheck = null;
		
		continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "resumePollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);
		
		// use the pollId to pause/resume/stop the poll
		this.writer.println(MessageFactory.getStopPollMessage(pollId));
		
		pollIdCheck = null;
		
		continueLoop = true;
		try {
			while(continueLoop && (input=this.reader.readLine()) != null)
			{
				Document doc = MessageFactory.stringToXmlDoc(input);
				String messageType = doc.getDocumentElement().getNodeName();
				assertEquals(messageType, "stopPollReply");
				continueLoop = false;
				pollIdCheck = doc.getElementsByTagName("pollId").item(0).getTextContent();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		assertEquals(pollId, pollIdCheck);
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRetrieveQuestions() throws IOException
	{
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));

		// wait for validation
		boolean recQuestions = false;
		String input = null;
		try {
			while(!recQuestions && (input=this.reader.readLine()) != null)
			{
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "questions");
					recQuestions = true;
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreatePoll() throws IOException
	{
		Socket socket = null;
		try {
			socket = connectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send a connection message indicating email address
		this.writer.println(MessageFactory.getConnectMessage("test@email.ca"));
		
		// send a createPoll message
		this.writer.println(MessageFactory.getCreatePollMessage(0));
		
		// wait for validation
		boolean recCreatePollReply = false;
		int messagesReceived = 0;
		String input = null;
		try {
			while(!recCreatePollReply && (input=this.reader.readLine()) != null)
			{
				messagesReceived++;
				System.out.println("Client: Received: " + input);
				
				Document doc = MessageFactory.stringToXmlDoc(input);
				
				assertNotNull(doc);
				
				if(doc != null && messagesReceived==2)
				{
					String messageType = doc.getDocumentElement().getNodeName();
					assertEquals(messageType, "createPollReply");
					recCreatePollReply = true;
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void connectToServerTest() {
		Socket socket = null;
		try {
			socket = new Socket("localhost", this.port);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(socket);
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//@Test
	public void multipleConnectToServerTest() throws UnknownHostException, IOException {
		
		ArrayList<Socket> socketList = new ArrayList<Socket>();
		for(int i=0; i<10; i++)
		{
			Socket s = new Socket("localhost", this.port);
			socketList.add(s);
		}
		
		for(Socket s: socketList)
		{
			assertNotNull(s);
		}
		
		for(Socket s: socketList)
		{
			s.close();
		}
		
	}
}
