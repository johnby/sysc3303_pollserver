package poll;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import questions.Question;
import questions.QuestionManager;

public class PollManager {

	private ArrayList<PollConnection> connections = null;
	private QuestionManager questionManager = null;
	
	public PollManager()
	{
		connections = new ArrayList<PollConnection>();
		questionManager = new QuestionManager();
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
		return null;
		
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
	


}
