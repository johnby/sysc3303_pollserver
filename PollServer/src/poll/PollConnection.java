package poll;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import questions.Question;

public class PollConnection extends Thread {

	private PollManager pollManager = null;
	private Socket socket = null;
	private boolean active=true;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private String pollersEmail = null;
	
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
					processMessage(new String(message));
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

		System.out.println("Debug: Msg Recieved: " + message);

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(message));
			
			Document doc = db.parse(is);
			
			String messageType = doc.getDocumentElement().getNodeName();
			
			if(messageType.equals("connect"))
			{
				processConnectMessage(doc);
			}
			
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Process a connect message
	 * get user email address
	 * send question information
	 */
	private void processConnectMessage(Document doc)
	{
		NodeList email = doc.getElementsByTagName("email");
		
		// set email based on connect message
		if(email != null && email.getLength() > 0)
		{
			this.pollersEmail = email.item(0).getTextContent();
		}
		
		sendQuestions(this.pollManager.getQuestions());
	}
		

	/*
	 * Send the client available questions.
	 */
	public void sendQuestions(ArrayList<Question> questions) {
		synchronized (this) {
			writer.println(MessageFactory.createQuestionMessage(questions));
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
