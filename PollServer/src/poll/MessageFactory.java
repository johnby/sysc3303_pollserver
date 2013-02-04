package poll;

import java.util.ArrayList;

import questions.Question;

public class MessageFactory {

	public static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	public static String createConnectMessage(String email)
	{
		return "<?xml version=\"1.0\"?><connect><email>" + email + "</email></connect>";
	}
	
	public static String createQuestionMessage(ArrayList<Question> questions)
	{
		String message = "<?xml version=\"1.0\"?>";
		
		if(questions.size() == 0)
		{
			message += "<questions count=\"0\"></questions>";
		}
		else
		{
			message += "<questions count=\"" + questions.size() + "\">";

			for(Question q: questions)
			{
				message += "<question><value>" + q.getQuestion() + "</value>";
				
				String[] answers = q.getAnswers();
				for(int i=0; i<answers.length; i++)
				{
					message += "<answer id=\"" + getLetter(i) + "\">" + answers[i] + "</answer>";
				}
				
				message += "</question>";
			}
			
			message += "</questions>";
		}
		
		return message;
	}
	
	
	public static String getLetter(int index)
	{
		if(index < 0 || index >= MessageFactory.alphabet.length())
		{
			return "";
		}
		else
		{
			return MessageFactory.alphabet.substring(index);
		}
	}
}
