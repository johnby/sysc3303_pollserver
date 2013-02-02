package poll;

public class Vote {

	private String pollId = null;
	private String selection = null;
	private String user = null;
	
	public Vote(String pollId, String selection, String user)
	{
		this.pollId = pollId;
		this.selection = selection;
		this.user = user;
	}

	public String getPollId() {
		return pollId;
	}

	public void setPollId(String pollId) {
		this.pollId = pollId;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
