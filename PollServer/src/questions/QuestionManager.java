package questions;

import java.util.ArrayList;

public class QuestionManager {

	private ArrayList<Question> questions = null;
	
	public QuestionManager()
	{
		/*
		 * Hard-coded questions for now
		 */
		questions = new ArrayList<Question>();
		questions.add(new Question("Test question 1?", new String[]{"option A","option B"}));
		questions.add(new Question("Test question 2?", new String[]{"option A","option B"}));
		questions.add(new Question("Test question 3?", new String[]{"option A","option B","option C"}));
		questions.add(new Question("Test question 4?", new String[]{"option A","option B","option C"}));
		questions.add(new Question("Test question 5?", new String[]{"option A","option B","option C","option D"}));
		questions.add(new Question("Test question 6?", new String[]{"option A","option B","option C","option D"}));
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Question> getQuestions()
	{
		return (ArrayList<Question>) this.questions.clone();
	}
	
	public Question getQuestion(int index)
	{
		if(index >= questions.size()) return null;
		
		return questions.get(index);
	}
	
}
