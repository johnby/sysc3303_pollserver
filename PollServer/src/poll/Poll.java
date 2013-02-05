package poll;

import java.util.Hashtable;

import questions.Question;

public class Poll {

	private Hashtable<String, Vote> votes = null;					// current votes
	private String pollId = null;									// poll id
	private String email = null;
	private Question question = null;
	public enum STATE {OPEN,STOPPED,PAUSED};
	
	private Hashtable<String, Integer> talliedVotes = null;
	private Poll.STATE state = Poll.STATE.OPEN;
	private int questionId = 0;
	
	/*
	 * Represents an active poll.
	 */
	public Poll(String pollId, String userEmail, Question question, int questionId)
	{
		this.questionId = questionId;
		this.pollId = pollId;
		this.email = userEmail;
		this.question = question;
		votes = new Hashtable<String, Vote>();
		talliedVotes = new Hashtable<String, Integer>();
		
		String[] answers = this.question.getAnswers();
		for(int i=0; i<this.question.getAnswerCount();i++)
		{
			String selection = MessageFactory.getLetter(i);
			talliedVotes.put(selection,0);
		}
	}
	
	/*
	 * Vote for this poll.
	 */
	public boolean Vote(String selection, String user)
	{
		if(this.state == STATE.OPEN)
		{
			if(votes.containsKey(user))
			{
				if(!votes.get(user).getSelection().equals(selection))
				{
					int oldSelectionVotes = talliedVotes.get(votes.get(user).getSelection());
					talliedVotes.put(votes.get(user).getSelection(), oldSelectionVotes-1);
					int old = talliedVotes.get(selection);
					talliedVotes.put(selection, old+1);
					votes.get(user).setSelection(selection);
				}
			}
			else
			{
				// new user
				talliedVotes.put(selection, 1);
				votes.put(user, new Vote(this.pollId, selection, user));
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getPollId()
	{
		return this.pollId;
	}
	
	public void pause()
	{
		this.state = Poll.STATE.PAUSED;
	}
	
	public void resume()
	{
		if(this.state == STATE.OPEN)
		{
			this.state = STATE.PAUSED; 
		}
	}
	
	public void stop()
	{
		this.state = STATE.STOPPED;
	}
	
	public String getStatus()
	{
		String status = "";
		status += "PollId=" + this.pollId;
		status += "; email=" + this.email;
		status += "; status=" + this.state;
		status += "\n Question=" + this.question.getQuestion();
		
		String[] answers = this.question.getAnswers();
		for(int i=0; i<this.question.getAnswerCount();i++)
		{
			String selection = MessageFactory.getLetter(i);
			status += "\n  " + selection + "." + answers[i] + " = " + talliedVotes.get(selection);
		}

		return status;
	}
	
	public int getQuestionId()
	{
		return this.questionId;
	}
}
