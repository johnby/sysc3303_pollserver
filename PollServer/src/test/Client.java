package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import listeners.PollListener;
import listeners.VoteListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import poll.PollManager;

public class Client {

	PollListener pollListener = null;
	PollManager manager = null;
	VoteListener voteListener = null;
	
	int port = 7775;
	
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
		voteListener = new VoteListener(manager);

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
		}
		
		try {
			voteListener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		
		boolean recInput = false;
		String input = null;
		try {
			while(!recInput && (input=this.reader.readLine()) != null)
			{
				assertTrue(input.equals("some questions"));
				recInput = true;
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
	
	@Test
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
