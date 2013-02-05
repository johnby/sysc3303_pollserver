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
import java.util.Hashtable;

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
	private Hashtable<String, Poll> activePolls = null;
	
	public PollConnection(PollManager pollManager, Socket newSocket) throws IOException {
		this.pollManager = pollManager;
		this.socket = newSocket;
		this.activePolls = new Hashtable<String, Poll>();
		
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

		System.out.println("Server: Msg Recieved: " + message);

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(message));
			
			Document doc = db.parse(is);
			
			String messageType = doc.getDocumentElement().getNodeName();
			
			if(messageType.equals("connect"))
			{
				processConnectMessage(doc);
			} else if(messageType.equals("createPoll"))
			{
				processCreatePollMessage(doc);
			} else if(messageType.equals("pausePoll"))
			{
				processPausePollMessage(doc);
			} else if(messageType.equals("resumePoll"))
			{
				processResumePollMessage(doc);
			} else if(messageType.equals("stopPoll"))
			{
				processStopPollMessage(doc);
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
	 ***************************** RECEIVING MESSAGES BELOW ****************************
	 */
	
	/*
	 * Received Stop Poll message.
	 */
	private void processStopPollMessage(Document doc) {
		String pollId = null;
		pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
		if(this.activePolls.containsKey(pollId))
		{
			this.activePolls.get(pollId).stop();
			sendStopPollConfirmation(pollId);
		}
	}

	/*
	 * Received Resume Poll message.
	 */
	private void processResumePollMessage(Document doc) {
		String pollId = null;
		pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
		if(this.activePolls.containsKey(pollId))
		{
			this.activePolls.get(pollId).resume();
			sendResumePollConfirmation(pollId);
		}
	}

	/*
	 * Received Pause Poll message.
	 */
	private void processPausePollMessage(Document doc) {
		String pollId = null;
		pollId = doc.getElementsByTagName("pollId").item(0).getTextContent();
		if(this.activePolls.containsKey(pollId))
		{
			this.activePolls.get(pollId).pause();
			sendPausePollConfirmation(pollId);
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
	 * Process create poll message
	 */
	private void processCreatePollMessage(Document doc)
	{
		NodeList questionId = doc.getElementsByTagName("questionId");
		if(questionId != null)
		{
			int questionIndex = Integer.parseInt(questionId.item(0).getTextContent());		
			Poll p = pollManager.createPoll(this, this.pollersEmail, questionIndex);
			this.activePolls.put(p.getPollId(), p);
			sendCreatePollConfirmation(p);
		}
	}

	
	/*
	 ***************************** SENDING MESSAGES BELOW ****************************
	 */
	
	private void sendCreatePollConfirmation(Poll p) {
		synchronized (this) {
			writer.println(MessageFactory.getCreatePollMessageReply(p.getPollId(), p.getQuestionId()));
		}
	}

	public void sendQuestions(ArrayList<Question> questions) {
		synchronized (this) {
			writer.println(MessageFactory.getQuestionMessage(questions));
		}
	}
	
	private void sendPausePollConfirmation(String pollId) {
		synchronized (this) {
			writer.println(MessageFactory.getPausePollReplyMessage(pollId));
		}
	}
	
	private void sendResumePollConfirmation(String pollId) {
		synchronized (this) {
			writer.println(MessageFactory.getResumePollReplyMessage(pollId));
		}
	}
	
	private void sendStopPollConfirmation(String pollId) {
		synchronized (this) {
			writer.println(MessageFactory.getStopPollReplyMessage(pollId));
		}
	}

	// safely kill thread
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
