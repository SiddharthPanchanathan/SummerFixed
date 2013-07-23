package com.hm.newAge.utils;


import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ExtractAnswerUtil {
	
	
	private static String[] answers;
	
	public static String[] getAnswers(String qtiXml) throws Exception 
	{
		//qtiXml ="<questions><questestinterop type=\"qb\"><item title=\"project814_q5\" ident=\"39CBD521-9ECC-B881-1ECC-E6E07A133E61\" nhm_keywords=\"\" nhm_difficulty_level=\"Average\" nhm_v4_question_id=\"72D4E9A6-746C-4A14-8F33EE14DB00F0D0\" nhm_year=\"2007\"><nhm_title>project814_q5</nhm_title><nhm_submittable>true</nhm_submittable><nhm_non_submittable>true</nhm_non_submittable><nhm_author>HeyMath!</nhm_author><nhm_source>Padma Seshadri Bala Bhavan Senior Secondary School</nhm_source><nhm_description/><objectives><material><mattext/></material></objectives><presentation><nhm_hint><material><mattext/></material></nhm_hint><material><mattext>Study the pattern and find the missing number x.&lt;br/&gt;</mattext></material><material><matimage uri=\"http://contentserver.heymath.com/files/questions/18279/images/project814_q5.gif\" nhm_original_file_name=\"http://contentserver.heymathcom/files/questions/18279/images/project814_q5.gif\"/></material><response_lid ident=\"Response_0\" rcardinality=\"Single\" nhm_type=\"Fill_Blank_Plain\"><material><mattext>Fill in your answer in the box provided below</mattext></material><nhm_render_fill nhm_unit=\"\" nhm_unit_placement=\"after\" nhm_expected_data_type=\"number\"/></response_lid></presentation><resprocessing><respcondition title=\"Correct\" continue=\"No\"><conditionvar><varequal respident=\"Response_0\"><nhm_blank_name>x =</nhm_blank_name><nhm_blank_value><math><mn>80</mn></math></nhm_blank_value></varequal></conditionvar></respcondition></resprocessing></item></questestinterop><questestinterop type=\"qb\"><item title=\"project814_q5\" ident=\"39CBD521-9ECC-B881-1ECC-E6E07A133E61\" nhm_keywords=\"\" nhm_difficulty_level=\"Average\" nhm_v4_question_id=\"72D4E9A6-746C-4A14-8F33EE14DB00F0D0\" nhm_year=\"2007\"><nhm_title>project814_q5</nhm_title><nhm_submittable>true</nhm_submittable><nhm_non_submittable>true</nhm_non_submittable><nhm_author>HeyMath!</nhm_author><nhm_source>Padma Seshadri Bala Bhavan Senior Secondary School</nhm_source><nhm_description/><objectives><material><mattext/></material></objectives><presentation><nhm_hint><material><mattext/></material></nhm_hint><material><mattext>Study the pattern and find the missing number x.&lt;br/&gt;</mattext></material><material><matimage uri=\"http://contentserver.heymath.com/files/questions/18279/images/project814_q5.gif\" nhm_original_file_name=\"http://contentserver.heymathcom/files/questions/18279/images/project814_q5.gif\"/></material><response_lid ident=\"Response_0\" rcardinality=\"Single\" nhm_type=\"Fill_Blank_Plain\"><material><mattext>Fill in your answer in the box provided below</mattext></material><nhm_render_fill nhm_unit=\"\" nhm_unit_placement=\"after\" nhm_expected_data_type=\"number\"/></response_lid></presentation><resprocessing><respcondition title=\"Correct\" continue=\"No\"><conditionvar><varequal respident=\"Response_0\"><nhm_blank_name>x =</nhm_blank_name><nhm_blank_value><math><mn>123</mn></math></nhm_blank_value></varequal></conditionvar></respcondition></resprocessing></item></questestinterop></questions>";
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(qtiXml)));
	    NodeList answerList = doc.getElementsByTagName("nhm_blank_value");
	    answers = new String[answerList.getLength()];
	    for(int i=0;i<answerList.getLength();i++)
	    {
	    	answers[i]=nodeToString(answerList.item(i));
	    }
	    return answers;
	}
	
	private static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
		 Transformer t = TransformerFactory.newInstance().newTransformer();
		 t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		 t.setOutputProperty(OutputKeys.INDENT, "yes");
		 t.transform(new DOMSource(node.getFirstChild()), new StreamResult(sw));
		} catch (TransformerException te) {
		 System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
		}
	
}