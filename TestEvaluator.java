package com.hm.newAge.services;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wiris.quizzes.api.QuestionInstance;
import com.wiris.quizzes.api.QuestionRequest;
import com.wiris.quizzes.api.QuestionResponse;
import com.wiris.quizzes.api.QuizzesBuilder;
import com.wiris.quizzes.api.QuizzesService;

public class TestEvaluator {
	
	public String evaluateAnswers(String correctAnswerInJson, String userAnswerInJson){
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		ArrayList<ArrayList<String>> correctAnswers;
		ArrayList<ArrayList<String>> userAnswers;
		ArrayList<ArrayList<Boolean>> results = new ArrayList<ArrayList<Boolean>>();
		correctAnswers = gson.fromJson(correctAnswerInJson, new TypeToken<ArrayList<ArrayList<String>>>(){}.getType());
		userAnswers = gson.fromJson(userAnswerInJson, new TypeToken<ArrayList<ArrayList<String>>>(){}.getType());
		for(int i = 0 ; i<userAnswers.size() ; i++)
		{
			ArrayList<String> answerBlanks = userAnswers.get(i);
			ArrayList<Boolean> tempResults = new ArrayList<Boolean>();
			for(int j = 0 ; j<answerBlanks.size() ; j++)
			{
				
				if(answerBlanks.get(j).length()>1)
				{
					//Use Wiris Evaluator
					QuizzesBuilder builder = QuizzesBuilder.getInstance();
				    QuestionRequest request = builder.newEvalRequest(correctAnswers.get(i).get(j), answerBlanks.get(j), null, null);
				    QuizzesService service = builder.getQuizzesService();
				    QuestionResponse response = service.execute(request);
				    QuestionInstance instance = builder.newQuestionInstance();
				    instance.update(response);
				    tempResults.add(instance.isAnswerCorrect(0));
				    
				}
				else
				{
					if(answerBlanks.get(j).equalsIgnoreCase(correctAnswers.get(i).get(j)))
					{
						tempResults.add(true);
					}
					else
					{
						tempResults.add(false);
					}
				}
			}
			results.add(tempResults);
		}
			
		return gson.toJson(results);
	}
}
