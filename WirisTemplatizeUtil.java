package com.hm.newAge.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.hm.newAge.VOs.QuestionResponseVO;
import com.hm.wiris.VOs.MultiplechoiceBean;
import com.hm.wiris.util.WirisHandler;
import com.hm.newAge.resources.LoadProperties;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

public class WirisTemplatizeUtil {
	private static Template templateMCQ;
	private static Template templateFIB;
	
public static String processQuestion(QuestionResponseVO qr) throws IOException{
	Configuration config = new Configuration();
	URL templateURL = WirisTemplatizeUtil.class.getResource(LoadProperties.getQuery("wiris_template_dir"));
	config.setDirectoryForTemplateLoading(new File(templateURL.getPath()));

	//load Templates
	try {
		templateMCQ = config.getTemplate(LoadProperties.getQuery("get_wir_mcq_template"));
	} catch (Exception e) {

		System.out.println("MCQ.xml not found");
		e.printStackTrace();
	}
	try {
		templateFIB = config.getTemplate(LoadProperties.getQuery("get_wir_fib_template"));
	} catch (Exception e) {

		System.out.println("fib.xml not found");
		e.printStackTrace();
	}
	
		try {
			qr = WirisHandler.processQuestion(qr);
			qr = templatizeQuestion(qr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qr.getQtiXml().replaceAll("&nbsp;", "&#160;")
		  .replaceAll("&lt;", "&#60;")
		  .replaceAll("&gt;", "&#62;")
		  .replaceAll("&amp;", "&#38;")
		  .replaceAll("&quot;", "&#34;");
		
	}
	
	private static QuestionResponseVO templatizeQuestion(QuestionResponseVO qr) throws Exception
    {

    	SimpleHash context = new SimpleHash();
    	StringWriter out = new StringWriter();
    	 
    	Template _template = null;
    	if(qr.getOriginalQstn().getResponseType().equalsIgnoreCase("fib"))
    	{    	
    		_template = templateFIB;
    		context.put("questionResponse", qr);
    	} 
    	else if(qr.getOriginalQstn().getResponseType().equalsIgnoreCase("mcq"))
    	{
    		_template = templateMCQ;
    		
    		int correctIndex = 0;
    		ArrayList<String> options =((MultiplechoiceBean)qr.getProcessedQstn()).getOptions();
    		Collections.shuffle(options);
    		String correctOption = ((MultiplechoiceBean)qr.getProcessedQstn()).getCorrectOption();
    		for(int i = 0; i<options.size();i++)
    		{
    			if(options.get(i).equalsIgnoreCase(correctOption))
    			{
    				correctIndex = i;
    			}
    		}
    		context.put("questionResponse", qr);
    		context.put("correctOption", correctIndex);
    		context.put("mcqoptions", options);
    	}
 
    	_template.process(context, out);
 
    	qr.setQtiXml(out.toString());
    	
    	return qr;
    	
    }


}
