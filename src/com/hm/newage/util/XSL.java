package com.hm.newage.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.xml.transform.*;
import java.io.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
/**
 *
 * @author senthil
 */
public class XSL{
    private String generateHTML(StreamSource xmlSource, StreamSource xslSource, int qNo, int oNo) {
        String result = null;
        Transformer transformer;
        TransformerFactory tFactory;
        try {
            tFactory = TransformerFactory.newInstance();
            StringWriter sw = new StringWriter();
            StreamResult resultSource = new StreamResult(sw);
            transformer = tFactory.newTransformer(xslSource);
            transformer.setParameter("qID", qNo);
            transformer.setParameter("oID", oNo);
            transformer.transform(xmlSource, resultSource);
            result = sw.toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    public String encoding(String xml)
    {
    	//return "<?xml version='1.0' encoding='utf-8'?><!DOCTYPE html [<!ENTITY nbsp '&#160;'>]>" + xml;
    	//return "<?xml version='1.0' encoding='utf-8'?>" + xml;
    	return xml;
    }
    public String apply(String xml, int qNo, int oNo) {
       String genHTML="";
        try 
        {
            String xsl= "/home/intern/Documents/InternWorkspace/RandomName/templates/question.xslt";
            StreamSource xslSource = new StreamSource(xsl);
            StreamSource xmlSource = new StreamSource(new BOMStripperInputStream(IOUtils.toInputStream(xml)));
            genHTML = generateHTML(xmlSource, xslSource, qNo, oNo);
            genHTML = genHTML.replace("<math>", "<math xmlns='http://www.w3.org/1998/Math/MathML'>");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genHTML;
    }

}
