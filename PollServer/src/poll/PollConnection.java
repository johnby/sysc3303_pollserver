package poll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import questions.Question;

public class PollConnection extends Thread {

	private PollManager pollManager = null;
	private Socket socket = null;
	private boolean active=true;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	
	public PollConnection(PollManager pollManager, Socket newSocket) throws IOException {
		this.pollManager = pollManager;
		this.socket = newSocket;
		
		// setup reader
		InputStream inputstream = this.socket.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        reader = new BufferedReader(inputstreamreader);
        
        // setup writer
        OutputStream outputStream = this.socket.getOutputStream();
        OutputStreamWriter osr = new OutputStreamWriter(outputStream);
        writer = new PrintWriter(socket.getOutputStream(), true);
	}
	
	/*
	 * Recieve client requests here.
	 */
	public void run()
	{
		while(active)
		{
			String message = null;
			try {
				if((message = reader.readLine())!=null)
				{
					processMessage(message);
				}
			} catch (IOException e) {
				
				if(this.socket.isClosed())
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

	/*
	 * Determine what message means
	 */
	private void processMessage(String message) {
		
		System.out.println("Msg Recieved:" + message);
		
		if(message.equals("get questions"))		
		{

		}
	}

	/*
	 * Send the client available questions.
	 */
	public void sendQuestions(ArrayList<Question> questions) {
		synchronized (this) {
			writer.println("some questions\n");
		}
	}

	public void disconnect() {
		this.active = false;
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
