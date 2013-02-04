package poll;

import java.util.Hashtable;

import questions.Question;

public class Poll {

	private Hashtable<String, Vote> votes = null;					// current votes
	private String pollId = null;									// poll id
	private String email = null;
	private Question question = null;
	
	/*
	 * Represents an active poll.
	 */
	public Poll(String pollId, String userEmail, Question question)
	{
		this.pollId = pollId;
		this.email = userEmail;
		this.question = question;
		votes = new Hashtable<String, Vote>();
	}
	
	/*
	 * Vote for this poll.
	 */
	public void Vote(String selection, String user)
	{
		if(votes.containsKey(user))
		{
			if(!votes.get(user).getSelection().equals(selection))
			{
				votes.get(user).setSelection(selection);
			}
		}
		else
		{
			votes.put(user, new Vote(this.pollId, selection, user));
		}
	}
	
	public String getPollId()
	{
		return this.pollId;
	}
}
