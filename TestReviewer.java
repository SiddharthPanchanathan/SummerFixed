package com.hm.newAge.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hm.newAge.VOs.QuestionPOJO;
import com.hm.newAge.VOs.QuestionResponseVO;
import com.hm.newAge.dao.implementations.QuestionPickerDAOImpl;
import com.hm.newAge.dao.interfaces.QuestionPickerDAO;
import com.hm.newAge.utils.WirisTemplatizeUtil;
import com.hm.newAge.utils.XSLUtil;

/*
 * @author - Jay Ravi
 */

public class TestReviewer {
	
	private List<QuestionResponseVO> questionResponses;
	private QuestionPickerDAO questionPicker;
	private String[] QTIXML;
	
	public String[] getTestReview(String questionBunch)
	{
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		this.questionPicker = new QuestionPickerDAOImpl();
		HashMap<String, String> QuestionListInMap;
		ArrayList<String> wids = new ArrayList<String>();
		ArrayList<String> qbids = new ArrayList<String>();
		QuestionListInMap = gson.fromJson(questionBunch, new TypeToken<HashMap<String, String>>(){}.getType());
		
		int i = 1;
		while(QuestionListInMap.get("q"+i+"_id")!=null)
		{
			if(QuestionListInMap.get("q"+i+"_source").equalsIgnoreCase("WI"))
			{
				wids.add(QuestionListInMap.get("q"+i+"_id"));
			}else{
				qbids.add(QuestionListInMap.get("q"+i+"_id"));
			}
			i++;
		}
		
		//Store All the question ids
		this.questionResponses = questionPicker.getWirisQuestions(wids, false);
		questionResponses.addAll(questionPicker.getStaticQuestions(qbids));
		
		//Send the variable list and map taken from the client to the snippet
		Iterator<QuestionResponseVO> qriter = this.questionResponses.iterator();
			while(qriter.hasNext())
			{
				QuestionResponseVO qr = qriter.next();
				if(qr.getSourceType().equalsIgnoreCase("WI"))
				{
					if(QuestionListInMap.get(String.valueOf(qr.getQid())+"_WI")!=null)
					{
						String qOrder = QuestionListInMap.get(String.valueOf(qr.getQid())+"_WI");
						HashMap<String, Map<String, String>> variableMap = gson.fromJson(QuestionListInMap.get(qOrder+"_varmap"), new TypeToken<HashMap<String,Map<String, String>>>(){}.getType());
						ArrayList<String> variableList = gson.fromJson(QuestionListInMap.get(qOrder+"_varlist"), new TypeToken<ArrayList<String>>(){}.getType());
						qr.setVariableList(variableList);
						qr.setVariableMap(variableMap);
						qr.setqOrder(Integer.parseInt(QuestionListInMap.get(String.valueOf(qr.getQid())+"_WI").replaceAll("q", "")));
					} 
				}
				else{
					System.out.println("Question Bank Order--------------"+QuestionListInMap.get(String.valueOf(qr.getQid())+"_QB"));
					if(QuestionListInMap.get(String.valueOf(qr.getQid())+"_QB")!=null){
						qr.setqOrder(Integer.parseInt(QuestionListInMap.get(String.valueOf(qr.getQid())+"_QB").replaceAll("q", "")));
					}
				}
			}
		Collections.sort(this.questionResponses);
		this.QTIXML = new String[this.questionResponses.size()];
		//Create questions templates in order starting with the first question as in the client's question list
		Iterator<QuestionResponseVO> qriter2 = this.questionResponses.iterator();
		i = 0;
		try{
			while(qriter2.hasNext())
			{
				QuestionResponseVO qr = qriter2.next();
				this.QTIXML[i] = new String();
					if(qr.getSourceType().equalsIgnoreCase("WI"))
					{			
						this.QTIXML[i]=WirisTemplatizeUtil.processQuestion(qr).replaceAll("<questestinterop>", "<questestinterop type='wiris'>"); // templatize dynamic question snippets before adding to response string					
					} 
					else if(qr.getSourceType().equalsIgnoreCase("QB"))
					{
						this.QTIXML[i]=XSLUtil.applyXSL(qr.getOriginalQstn().getId(),qr.getQtiXml().replaceAll("<questestinterop>", "<questestinterop type='qb'>"), qr.getqType());
					}
					i++;
				}
				
		} catch(Exception e){
			e.printStackTrace();
		}

		return this.QTIXML;
	}
}
