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
			System.out.println("Server: New connection established.");
			connection = new PollConnection(this, newSocket);
			connections.add(connection);
			connection.start();
			//connection.sendQuestions(questionManager.getQuestions());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Do something with a received vote.
	 */
	public void voteRecieved(Vote vote)
	{
		System.out.println("Server: Received vote from " + vote.getUser() + " for poll " + vote.getPollId() + ", selected " + vote.getSelection());
		if(this.polls.containsKey(vote.getPollId()))
		{
			boolean result = this.polls.get(vote.getPollId()).Vote(vote.getSelection(), vote.getUser());
			System.out.println("Server: Found poll. Poll addedd successfully = " + result);
			System.out.println("Server: Poll Status\n" + this.polls.get(vote.getPollId()).getStatus());
		}
	}
	
	/*
	 * Create a poll for poller with question questionIndex (ask QuestionManager)
	 */
	public Poll createPoll(PollConnection poller, String email, int questionIndex)
	{
		String pollId = generatePollId();
		Poll poll = new Poll(pollId, email, questionManager.getQuestion(questionIndex), questionIndex);
		System.out.println("Server: Creating Poll with pollId=" + pollId);
		this.polls.put(poll.getPollId(), poll);
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
