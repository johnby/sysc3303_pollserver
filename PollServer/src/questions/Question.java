package questions;

public class Question {

	private String question = null;
	private String[] answers = null;
	
	public Question(String question, String[] answers)
	{
		this.question = question;
		this.answers = answers;
	}
	
	public int getAnswerCount()
	{
		return answers.length;
	}
	
	public String getQuestion()
	{
		return question;
	}
	
	public String[] getAnswers()
	{
		return answers;
	}
	
	public Question clone()
	{
		Question newQuestion = new Question(this.question, answers);
		return newQuestion;
	}
}
