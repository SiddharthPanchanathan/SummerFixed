package com.hm.newAge.utils;

import java.io.StringReader;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;

import javax.xml.xpath.XPathFactory;



import com.hm.newAge.utils.preferences.PropsKeys;
import com.hm.newAge.resources.LoadProperties;

public class XSLUtil {
	
	private static String templatePath;
	
	public static String applyXSL(Integer id,String xml, String qtype){
		StringWriter resultXML = new StringWriter();
		templatePath = XSLUtil.class.getResource(LoadProperties.getQuery("qb_template_dir")).getPath();
	
		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_image_xsl"));
			StringReader xmlDoc = new StringReader(xml);
			Transformer transformer = tFactory.newTransformer(xslDoc);
        	transformer.setParameter("qid", id.toString());
        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			e.getStackTrace();
		}
		
		return convertToMathML(resultXML.toString(), qtype).replaceAll("&nbsp;", "&#160;")
		  .replaceAll("&lt;", "&#60;")
		  .replaceAll("&gt;", "&#62;")
		  .replaceAll("&amp;", "&#38;")
		  .replaceAll("&quot;", "&#34;");
	}
	
	private static String convertToMathML(String xml, String qtype)
	{
		StringWriter resultXML = new StringWriter();
		List<String> mathML = new ArrayList<String>();
		String processedXML = new String();
		
		//used only for fib many xslt
		XPathFactory factory = XPathFactory.newInstance();
	    XPath xPath = factory.newXPath();
	    
		try {
			mathML = QTIXmlUtils.convertXml(xml, qtype);
		} catch (Exception e1) {
			System.out.println("Conversion to MathML failed");
			e1.printStackTrace();
		}
		try
	    {
	        TransformerFactory tFactory = TransformerFactory.newInstance();
	        StringReader xmlDoc = new StringReader(xml);
	        if(qtype.equalsIgnoreCase(PropsKeys.FRACTION))
	        {
	        	Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_fibfrac_xsl"));
	        	Transformer transformer = tFactory.newTransformer(xslDoc);
	        	transformer.setParameter("mathml", "<nhm_blank_value>"+mathML.get(0)+"</nhm_blank_value>");
	        	transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
	        	processedXML = resultXML.toString();
	        }
	        else if(qtype.equalsIgnoreCase(PropsKeys.MIXFRACTION))
	        {
	        	Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_fibmixfrac_xsl"));
	        	Transformer transformer = tFactory.newTransformer(xslDoc);
	        	transformer.setParameter("mathml", "<nhm_blank_value>"+mathML.get(0)+"</nhm_blank_value>");
	        	transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
	        	processedXML = resultXML.toString();
	        }
	        else if(qtype.equalsIgnoreCase(PropsKeys.FILLBLANK))
	        {
	        	Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_fibplain_xsl"));
	        	Transformer transformer = tFactory.newTransformer(xslDoc);
	        	transformer.setParameter("mathml", "<nhm_blank_value>"+mathML.get(0)+"</nhm_blank_value>");
	        	transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
	        	processedXML = resultXML.toString();
	        }
	        else if(qtype.equalsIgnoreCase(PropsKeys.RATIO))
	        {
	        	Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_ratio_xsl"));
	        	Transformer transformer = tFactory.newTransformer(xslDoc);
	        	transformer.setParameter("mathml", "<nhm_blank_value>"+mathML.get(0)+"</nhm_blank_value>");
	        	transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
	        	processedXML = resultXML.toString();
	        }
	        
	        else if(qtype.equalsIgnoreCase(PropsKeys.DECIMAL))
	        {
	        	Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_decimal_xsl"));
	        	Transformer transformer = tFactory.newTransformer(xslDoc);
	        	transformer.setParameter("mathml", "<nhm_blank_value>"+mathML.get(0)+"</nhm_blank_value>");
	        	transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
	        	processedXML = resultXML.toString();
	        }
	        else if(qtype.equalsIgnoreCase(PropsKeys.FILLBLANKMANY))
	        {
	        	String MathMl = new String();
	        	MathMl = "<resprocessing>\n";
	        	for(int i=0; i<mathML.size();i++)
	        	{
	        		MathMl += "<respcondition title=\"Correct_Blank_"+i+"\" continue=\"No\"><conditionvar>\n";
	        		MathMl += "<varequal respident=\"Response_"+i+"\">\n";
	        		MathMl += "<nhm_blank_value>"+mathML.get(i)+"</nhm_blank_value>\n";
	        		MathMl += "</varequal>\n</conditionvar>\n</respcondition>\n";
	        	}
	        	MathMl += "</resprocessing>";
	        	System.out.println(MathMl);
	        	Source xslDoc = new StreamSource(templatePath+LoadProperties.getQuery("get_qb_fibmany_xsl"));
	        	Transformer transformer = tFactory.newTransformer(xslDoc);        	
	        	transformer.setParameter("mathml", MathMl);
	        	transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	        	transformer.transform(new StreamSource(xmlDoc), new StreamResult(resultXML));
	        	processedXML = resultXML.toString();
	        }
	        else if(qtype.equalsIgnoreCase(PropsKeys.MCQ)||qtype.equalsIgnoreCase(PropsKeys.TRUEORFALSE))
	        {
	        	processedXML = xml;
	        }
	        
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	    return processedXML;
	}
}