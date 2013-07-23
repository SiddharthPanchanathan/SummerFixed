package com.hm.newAge.utils;

import com.hm.newAge.utils.preferences.PropsKeys;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class QTIXmlUtils {
	enum OTQuestionType { FILLBLANKMANY,FILLBLANK,MCQ,TRUEORFALSE,FRACTION,MIXFRACTION,RATIO,DECIMAL }
	
	//not supported for qtype MCQ and TRUEORFALSE
	public static List<String> convertXml(String qtiXml,String qType) throws Exception {
		List<String> mathmlList = new ArrayList<String>();
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(qtiXml)));
	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xPath = factory.newXPath();

	    List<String> params = new ArrayList<String>();
	    OTQuestionType inputQuestionType = OTQuestionType.MCQ;
	    List<String> responseType = new ArrayList<String>();

	    if(qType.equalsIgnoreCase(PropsKeys.FILLBLANK) || qType.equalsIgnoreCase(PropsKeys.FILLBLANKMANY)) {
	        NodeList blankList = (NodeList) xPath.evaluate("/questestinterop/item/presentation/response_lid", doc, XPathConstants.NODESET);
	        for(int i=0;i<blankList.getLength();i++) {
	            String typeXpath = "/questestinterop/item/presentation/response_lid[$pos]/nhm_render_fill/@nhm_expected_data_type";
	            typeXpath = typeXpath.replaceAll("\\$pos", String.valueOf(i+1));
	            
	            Node nhm_expected_type = (Node) xPath.evaluate(typeXpath, doc, XPathConstants.NODE);
	            String valueXpath = "/questestinterop/item/resprocessing/respcondition[$pos]/conditionvar/varequal/nhm_blank_value/text()";
	            valueXpath = valueXpath.replaceAll("\\$pos", String.valueOf(i+1));
	            
	            Node nhm_value = (Node) xPath.evaluate(valueXpath, doc, XPathConstants.NODE);
	            responseType.add(nhm_expected_type.getNodeValue());
	            params.add(nhm_value.getNodeValue());
	        }
	        if(qType.equalsIgnoreCase(PropsKeys.FILLBLANK)) inputQuestionType = OTQuestionType.FILLBLANK;
	        if(qType.equalsIgnoreCase(PropsKeys.FILLBLANKMANY)) inputQuestionType = OTQuestionType.FILLBLANKMANY;
	    }
	    else if(qType.equalsIgnoreCase(PropsKeys.FRACTION)) {
	        Node nhm_numerator = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_numerator/text()", doc, XPathConstants.NODE);
	        Node nhm_denominator = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_denominator/text()", doc, XPathConstants.NODE);
	        params.add(nhm_numerator.getNodeValue());
	        params.add(nhm_denominator.getNodeValue());
	        inputQuestionType = OTQuestionType.FRACTION;
	    }
	    else if(qType.equalsIgnoreCase(PropsKeys.MIXFRACTION)) {
	        Node nhm_whole = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_whole/text()", doc, XPathConstants.NODE);
	        Node nhm_numerator = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_numerator/text()", doc, XPathConstants.NODE);
	        Node nhm_denominator = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_denominator/text()", doc, XPathConstants.NODE);
	        params.add(nhm_whole.getNodeValue());
	        params.add(nhm_numerator.getNodeValue());
	        params.add(nhm_denominator.getNodeValue());
	        inputQuestionType = OTQuestionType.MIXFRACTION;
	    }
	    else if(qType.equalsIgnoreCase(PropsKeys.RATIO)) {
	        Node nhm_ratio_first = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_ratio_first/text()", doc, XPathConstants.NODE);
	        Node nhm_ratio_second = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_ratio_second/text()", doc, XPathConstants.NODE);
	        params.add(nhm_ratio_first.getNodeValue());
	        params.add(nhm_ratio_second.getNodeValue());
	        inputQuestionType = OTQuestionType.RATIO;
	    }
	    else if(qType.equalsIgnoreCase(PropsKeys.DECIMAL)) {
	        Node nhm_whole = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_whole/text()", doc, XPathConstants.NODE);
	        Node nhm_separator = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_separator/text()", doc, XPathConstants.NODE);
	        Node nhm_decimal = (Node) xPath.evaluate("/questestinterop/item/resprocessing/respcondition/conditionvar/varequal/nhm_decimal/text()", doc, XPathConstants.NODE);
	        params.add(nhm_whole.getNodeValue());
	        params.add(nhm_separator.getNodeValue());
	        params.add(nhm_decimal.getNodeValue());
	        inputQuestionType = OTQuestionType.DECIMAL;
	    }

	    String mathml="";
	    if(qType.equalsIgnoreCase(PropsKeys.FILLBLANK) || qType.equalsIgnoreCase(PropsKeys.FILLBLANKMANY)) {
	    	mathmlList = getMathMl(inputQuestionType, params, responseType);
	    }
	    else {
	    	mathml = getMathMl(inputQuestionType, params);
	        mathmlList.add(mathml);
	    }
		
		return mathmlList;	
	}
	
	public static List<String> getMathMl(OTQuestionType qType,List<String> params,List<String> type) throws Exception {
	    List<String> mathmls = new ArrayList<String>();
	    if(qType.equals(OTQuestionType.FILLBLANKMANY) || qType.equals(OTQuestionType.FILLBLANK)) {
	        for(int i=0;i<params.size();i++) {
	            Document mathmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	            

	            Element root = mathmlDoc.createElement("math");
	            //root.setAttribute("xmlns", "http://www.w3.org/1998/Math/MathML");
	            
	            if(type.get(i).equalsIgnoreCase("number")) {
	                Element nhm_value = mathmlDoc.createElement("mn");
	                Text valueText = mathmlDoc.createTextNode(params.get(i));
	                nhm_value.appendChild(valueText);
	                root.appendChild(nhm_value);
	            }
	            else {
	                String a = params.get(0);
	                String[] arr = a.split("[0-9]+");
	                List<String> temp = new ArrayList<String>();

	                for(int j=0;j<arr.length-1;j++) {
	                    String str1 = arr[j];
	                    String str2 = arr[j+1];
	                    int s = a.indexOf(str1)+str1.length();
	                    int e = a.indexOf(str2);
	                    temp.add(str1);
	                    temp.add(a.substring(s, e));
	                }
	                temp.add(arr[arr.length-1]);

	                for(String mn_text : temp) {
	                    Element nhm_value = mathmlDoc.createElement("mn");
	                    Text valueText = mathmlDoc.createTextNode(mn_text);
	                    nhm_value.appendChild(valueText);
	                    root.appendChild(nhm_value);
	                }
	            }
	            
	            mathmlDoc.appendChild(root);

	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

	            StreamResult result = new StreamResult(new StringWriter());
	            DOMSource source = new DOMSource(mathmlDoc);
	            transformer.transform(source, result);

	            String xmlString = result.getWriter().toString();
	            mathmls.add(xmlString);
	        }
	    }

	    return mathmls;
	}

	public static String getMathMl(OTQuestionType qType,List<String> params) throws Exception {
	    Document mathmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	    

	    Element root = mathmlDoc.createElement("math");
	    //root.setAttribute("xmlns", "http://www.w3.org/1998/Math/MathML");

	    if(qType.equals(OTQuestionType.MIXFRACTION)) {
	        Element nhm_whole = mathmlDoc.createElement("mn");
	        Text wholeText = mathmlDoc.createTextNode(params.get(0));
	        nhm_whole.appendChild(wholeText);
	        root.appendChild(nhm_whole);

	        Element mfrac = mathmlDoc.createElement("mfrac");

	        Element num = mathmlDoc.createElement("mn");
	        Text numText = mathmlDoc.createTextNode(params.get(1));
	        num.appendChild(numText);
	        mfrac.appendChild(num);

	        Element den = mathmlDoc.createElement("mn");
	        Text denText = mathmlDoc.createTextNode(params.get(2));
	        den.appendChild(denText);
	        mfrac.appendChild(den);

	        root.appendChild(mfrac);
	    }
	    else if(qType.equals(OTQuestionType.FRACTION)) {
	        Element mfrac = mathmlDoc.createElement("mfrac");

	        Element num = mathmlDoc.createElement("mn");
	        Text numText = mathmlDoc.createTextNode(params.get(0));
	        num.appendChild(numText);
	        mfrac.appendChild(num);

	        Element den = mathmlDoc.createElement("mn");
	        Text denText = mathmlDoc.createTextNode(params.get(1));
	        den.appendChild(denText);
	        mfrac.appendChild(den);

	        root.appendChild(mfrac);
	    }
	    else if(qType.equals(OTQuestionType.RATIO)) {
	        Element nhm_ratio_first = mathmlDoc.createElement("mn");
	        Text numText = mathmlDoc.createTextNode(params.get(0));
	        nhm_ratio_first.appendChild(numText);

	        Element nhm_sep = mathmlDoc.createElement("mo");
	        Text sepText = mathmlDoc.createTextNode(":");
	        nhm_sep.appendChild(sepText);

	        Element nhm_ratio_second = mathmlDoc.createElement("mn");
	        Text denText = mathmlDoc.createTextNode(params.get(1));
	        nhm_ratio_second.appendChild(denText);

	        root.appendChild(nhm_ratio_first);
	        root.appendChild(nhm_sep);
	        root.appendChild(nhm_ratio_second);
	    }
	    else if(qType.equals(OTQuestionType.DECIMAL)) {
	        Element nhm_whole = mathmlDoc.createElement("mn");
	        Text numText = mathmlDoc.createTextNode(params.get(0));
	        nhm_whole.appendChild(numText);

	        Element nhm_sep = mathmlDoc.createElement("mo");
	        Text sepText = mathmlDoc.createTextNode(params.get(1));
	        nhm_sep.appendChild(sepText);

	        Element nhm_decimal = mathmlDoc.createElement("mn");
	        Text denText = mathmlDoc.createTextNode(params.get(2));
	        nhm_decimal.appendChild(denText);

	        root.appendChild(nhm_whole);
	        root.appendChild(nhm_sep);
	        root.appendChild(nhm_decimal);
	    }

	    mathmlDoc.appendChild(root);
	    
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

	    StreamResult result = new StreamResult(new StringWriter());
	    DOMSource source = new DOMSource(mathmlDoc);
	    transformer.transform(source, result);

	    String xmlString = result.getWriter().toString();

	    return xmlString;
	}
	
}
