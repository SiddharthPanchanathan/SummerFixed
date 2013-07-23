package com.hm.newAge.services;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hm.newAge.VOs.QuestionPOJO;
import com.hm.newAge.VOs.QuestionResponseVO;
import com.hm.newAge.dao.implementations.QuestionPickerDAOImpl;
import com.hm.newAge.dao.interfaces.QuestionPickerDAO;
import com.hm.newAge.utils.Searcher;
import com.hm.newAge.utils.WirisTemplatizeUtil;
import com.hm.newAge.utils.XSLUtil;
import com.hm.newAge.resources.LoadProperties;


public class TestMaker {
	
	private Searcher searcher;
	private List<QuestionResponseVO> _questionResponses;
	private QuestionPickerDAO questionPicker;
	private QuestionPOJO[] questionList;
	private Gson gson;
	
	
	public QuestionPOJO[] getTest(String country, String level, String stream, String subject, String topics) throws IOException
	{
		this.gson = new GsonBuilder().disableHtmlEscaping().create();
		if(stream.equalsIgnoreCase(""))
		{
			stream =" ";
		}
		ArrayList<String> SearchWords = new ArrayList<String>();
		SearchWords.add(country.toLowerCase());
		SearchWords.add(level.toLowerCase());
		SearchWords.add(stream.toLowerCase());
		SearchWords.add(subject.toLowerCase());
		SearchWords.add(topics.toLowerCase().trim());
		
		this._questionResponses = new ArrayList<QuestionResponseVO>();
		this.questionPicker = new QuestionPickerDAOImpl();
		HashMap<String, ArrayList<String>> qids;
		int no_of_questions = Integer.parseInt(LoadProperties.getQuery("get_no_of_questions"));
		
		this.questionList = new QuestionPOJO[no_of_questions];
		try{
			 
			searcher = new Searcher();
			qids = searcher.doSearch(SearchWords, no_of_questions/2, no_of_questions/2);
			
			
			System.out.println("Static Ids "+qids.get("QB"));
			System.out.println("Dynamic Ids "+qids.get("WI"));
			System.out.println("-----------------------------------------------------------");
			
			this._questionResponses = questionPicker.getWirisQuestions((ArrayList<String>) qids.get("WI"), true);
			this._questionResponses.addAll(questionPicker.getStaticQuestions((ArrayList<String>) qids.get("QB")));
			
			Collections.shuffle((List<QuestionResponseVO>)_questionResponses);
			
			processQuestionList();
			
			
			} catch(Exception e){
				e.printStackTrace();
			}
			
			
			return this.questionList;
	}
	
	
	
	private void processQuestionList() throws Exception{
		
		Iterator<QuestionResponseVO> qriter = _questionResponses.iterator();
		int i = 0;
		try{
			while(qriter.hasNext())
			{
				QuestionResponseVO qr = qriter.next();
				this.questionList[i] = new QuestionPOJO();
				this.questionList[i].setId(qr.getQid());
				if(qr.getSourceType().equalsIgnoreCase("WI"))
				{
					this.questionList[i].setSource("WI");
					this.questionList[i].setQtiXml(WirisTemplatizeUtil.processQuestion(qr).replaceAll("<questestinterop>", "<questestinterop type='wiris'>")); // templatize dynamic question snippets before adding to response string
					this.questionList[i].setVariableMap(gson.toJson(qr.getVariableMap()));
					this.questionList[i].setVariableList(gson.toJson(qr.getVariableList()));
					
				} 
				else if(qr.getSourceType().equalsIgnoreCase("QB"))
				{
					this.questionList[i].setSource("QB");
					this.questionList[i].setQtiXml(XSLUtil.applyXSL(qr.getOriginalQstn().getId(),qr.getQtiXml().replaceAll("<questestinterop>", "<questestinterop type='qb'>"), qr.getqType()));
				}
				i++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	}
