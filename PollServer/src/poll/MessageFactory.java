package poll;

import java.util.ArrayList;

import questions.Question;

public class MessageFactory {

	public static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	/*
	 * Generate a Connect message.
	 */
	public static String getConnectMessage(String email)
	{
		return "<?xml version=\"1.0\"?><connect><email>" + email + "</email></connect>";
	}
	
	/*
	 * Generate a list of Questions message.
	 */
	public static String getQuestionMessage(ArrayList<Question> questions)
	{
		String message = "<?xml version=\"1.0\"?>";
		
		if(questions == null || questions.size() == 0)
		{
			message += "<questions count=\"0\"></questions>";
		}
		else
		{
			message += "<questions count=\"" + questions.size() + "\">";

			int index = 0;
			for(Question q: questions)
			{
				message += "<question index=\"" + index + "\"><value>" + q.getQuestion() + "</value>";
				
				String[] answers = q.getAnswers();
				for(int i=0; i<answers.length; i++)
				{
					message += "<answer id=\"" + getLetter(i) + "\">" + answers[i] + "</answer>";
				}
				
				message += "</question>";
				index++;
			}
			
			message += "</questions>";
		}
		
		return message;
	}
	
	public static String getCreatePollMessage(int index) {
		String message = "<?xml version=\"1.0\"?>";
		message += "<createPoll><question>" + index + "</question></createPoll>";
		return message;
	}
	
	public static String getCreatePollMessageConfirmation(String pollId)
	{
		String message = "<?xml version=\"1.0\"?>";
		message += "<createPollReply><pollId>" + pollId + "</pollId></createPollReply>";
		return message;
	}
	
	/*
	 * Convert integer index to alpha char (a,b,c,d,etc)
	 */
	public static String getLetter(int index)
	{
		if(index < 0 || index >= MessageFactory.alphabet.length())
		{
			return "";
		}
		else
		{
			return MessageFactory.alphabet.substring(index, index+1);
		}
	}


}
