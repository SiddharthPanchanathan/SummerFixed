package com.hm.newage.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.hm.newage.services.Mapper1Stub.GetQuestion;
import com.hm.newage.services.Mapper1Stub.GetQuestionResponse;
import com.hm.newage.util.XSL;

public class newFile1 {
	
	private static String[] questions;

	
	public static String getHTMLString() throws RemoteException, Mapper1IOExceptionException {
		
	String HTMLQuestions = new String();
	Mapper1Stub stub = new Mapper1Stub();
	GetQuestion newQuestion = new GetQuestion();
	GetQuestionResponse res = new GetQuestionResponse();
	res = stub.getQuestion(newQuestion);
	System.out.println("----------------Client Starts here=------------------------");
	String XMLQuestions = res.get_return();
	List<String> questionsXML = test2questions(XMLQuestions);
	
	if (!questionsXML.isEmpty())
	{
	Iterator<String> iterator = questionsXML.iterator();
	questions = new String[questionsXML.size()];
	int questionNumber=1;
	int optionNumber = 0;
	int optionNum = 0;
	while (iterator.hasNext()) {
		String questionXML=iterator.next();
		
		questions[questionNumber-1]="<div><b>Question: "+questionNumber+"</b></div>";
		questions[questionNumber-1]+=new XSL().apply(questionXML, questionNumber-1, optionNum)+"</br></br>";
		optionNum += optionNumber;
		HTMLQuestions+=questions[questionNumber-1];
		questionNumber+=1;
	}
	
	
	}
	return HTMLQuestions;

	}
	
	public static List<String> test2questions(String testXML)
	{
		List<String> strlist = new ArrayList<String>();

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			System.out.println(testXML);
			Document doc = dBuilder.parse(new InputSource(new StringReader(new XSL().encoding(testXML))));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("questestinterop");
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			//serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			for (int i = 0; i < nList.getLength(); i++)
			{
				StringWriter sw = new StringWriter(); 
				serializer.transform(new DOMSource(nList.item(i)), new StreamResult(sw));	
				strlist.add(sw.toString()); 
			}
			
		} catch (Exception e) {e.printStackTrace();}
		return strlist;
	}

}
