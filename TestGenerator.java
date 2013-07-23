package com.hm.newAge.services;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import com.hm.newAge.VOs.QuestionResponseVO;
import com.hm.newAge.dao.implementations.QuestionPickerDAOImpl;
import com.hm.newAge.dao.interfaces.QuestionPickerDAO;
import com.hm.newAge.utils.Searcher;
import com.hm.newAge.utils.WirisTemplatizeUtil;
import com.hm.newAge.utils.XSLUtil;
import com.hm.newAge.resources.LoadProperties;


public class TestGenerator {
	
	private static Searcher searcher;
	private static List<QuestionResponseVO> _questionResponses;
	private static QuestionPickerDAO questionPicker;
	
	
	
	
	public static String getTest(String country, String level, String stream, String subject, String topics, int no_of_wiris_ques, int no_of_qb_ques) throws IOException
	{
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
		
		_questionResponses = new ArrayList<QuestionResponseVO>();
		questionPicker = new QuestionPickerDAOImpl();
		HashMap<String, ArrayList<String>> qids;
		String qstnXMLresponse = new String();
		
		
		try{
			 
			searcher = new Searcher();
			qids = searcher.doSearch(SearchWords, no_of_wiris_ques, no_of_qb_ques);
			
			
			System.out.println("Static Ids "+qids.get("QB"));
			System.out.println("Dynamic Ids "+qids.get("WI"));
			System.out.println("-----------------------------------------------------------");
			
			_questionResponses = questionPicker.getWirisQuestions((ArrayList<String>) qids.get("WI"), true);
			_questionResponses.addAll(questionPicker.getStaticQuestions((ArrayList<String>) qids.get("QB")));
			
			Collections.shuffle((List<QuestionResponseVO>)_questionResponses);
			
			qstnXMLresponse = processQuestionList();
			
			
			} catch(Exception e){
				e.printStackTrace();
			}
			
			return qstnXMLresponse.replaceAll("&nbsp;", "&#160;")
								  .replaceAll("&lt;", "&#60;")
								  .replaceAll("&gt;", "&#62;")
								  .replaceAll("&amp;", "&#38;")
								  .replaceAll("&quot;", "&#34;");
	}
	
	
	
	private static String processQuestionList() throws Exception{
		
		Iterator<QuestionResponseVO> qriter = _questionResponses.iterator();
		String qtiXml = new String();
		
		try{
			while(qriter.hasNext())
			{
				QuestionResponseVO qr = qriter.next();
				if(qr.getSourceType().equalsIgnoreCase("WI"))
				{
					
					qtiXml += WirisTemplatizeUtil.processQuestion(qr).replaceAll("<questestinterop>", "<questestinterop type='wiris'>"); // templatize dynamic question snippets before adding to response string
					
				} 
				else if(qr.getSourceType().equalsIgnoreCase("QB"))
				{

					qtiXml += XSLUtil.applyXSL(qr.getOriginalQstn().getId(),qr.getQtiXml().replaceAll("<questestinterop>", "<questestinterop type='qb'>"), qr.getqType());
				}
				
			}
		 
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return qtiXml;
	}
	}