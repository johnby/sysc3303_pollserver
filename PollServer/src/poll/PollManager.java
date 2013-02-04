package poll;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Hashtable;

import questions.Question;
import questions.QuestionManager;

public class PollManager {

	private ArrayList<PollConnection> connections = null;
	private QuestionManager questionManager = null;
	private Hashtable<String, Poll> polls = null;
	
	private SecureRandom random = new SecureRandom();
	
	public PollManager()
	{
		connections = new ArrayList<PollConnection>();
		questionManager = new QuestionManager();
		polls = new Hashtable<String, Poll>();
	}
	
	/*
	 * Start new pollers session (pollConnection).
	 */
	public void addNewConnection(Socket newSocket) {
		PollConnection connection;
		try {
			System.out.println("New connection established.");
			connection = new PollConnection(this, newSocket);
			connections.add(connection);
			connection.start();
			//connection.sendQuestions(questionManager.getQuestions());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void voteRecieved(Vote vote)
	{
		
	}
	
	/*
	 * Create a poll for poller with question questionIndex (ask QuestionManager)
	 */
	public Poll createPoll(PollConnection poller, String email, int questionIndex)
	{
		String pollId = generatePollId();
		Poll poll = new Poll(pollId, email, questionManager.getQuestion(questionIndex));
		System.out.println("Create poll = \n\n\n\n" + pollId);
		
		return poll;
	}

	public void shutdown() {
		for(PollConnection poller: connections)
		{
			poller.disconnect();
		}
		
	}

	public ArrayList<Question> getQuestions() {
		return questionManager.getQuestions();
	}
	

	public String generatePollId()
	{
		return new BigInteger(128, random).toString(32).substring(0, 5);
	}

}
