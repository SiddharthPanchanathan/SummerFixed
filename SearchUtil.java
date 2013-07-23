package com.hm.newAge.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

public class SearchUtil {
	
	private static String country; 
	private static String level; 
	private static String stream; 
	private static String subject; 
	private static StringTokenizer topicTokens;

	public static HashMap<String, ArrayList<String>> search(ArrayList<String> queryString, Analyzer analyzer, IndexSearcher searcher, IndexReader reader, String fields, int no_of_wiris_ques, int no_of_qb_ques) throws Exception
	{
		ArrayList<String> qbids = new ArrayList<String>();  //qb question ids - static
		ArrayList<String> wqids = new ArrayList<String>(); 	//wiris questions ids -  dynamic
		HashMap<String, ArrayList<String>> mixedqids = new HashMap<String, ArrayList<String>>();
		
		country = queryString.get(0);
		level= queryString.get(1);
		stream = queryString.get(2);
		subject = queryString.get(3);
		
		
		topicTokens= new StringTokenizer(queryString.get(4), ","); 
		
		System.out.println("tokens: "+queryString);
		
		PhraseQuery countryQuery = new PhraseQuery();
		countryQuery.setSlop(0);
		countryQuery.add(new Term("country", country));
		
		PhraseQuery levelQuery = new PhraseQuery();
		levelQuery.setSlop(0);
		levelQuery.add(new Term("level", level));
		
		PhraseQuery streamQuery = new PhraseQuery();
		streamQuery.setSlop(0);
		streamQuery.add(new Term("stream", stream));
		
		PhraseQuery subjectQuery = new PhraseQuery();
		subjectQuery.setSlop(0);
		subjectQuery.add(new Term("subject", subject));
		
		while(topicTokens.hasMoreElements())
		{
			
			String Topics = (String) topicTokens.nextElement();
			if(!Topics.equalsIgnoreCase(""))
			{
			
			QueryParser parser = new QueryParser(Version.LUCENE_31, "topic_name", analyzer);
			Query topicQuery = parser.parse(Topics.trim());
			
			parser = new QueryParser(Version.LUCENE_31, fields, analyzer);
			Query topicTreeQuery = parser.parse(Topics.trim());
				
			BooleanQuery topicBooleanQuery = new BooleanQuery();
			topicBooleanQuery.add(new BooleanClause(topicQuery, Occur.SHOULD));
			topicBooleanQuery.add(new BooleanClause(topicTreeQuery, Occur.SHOULD));
		
			BooleanQuery booleanQuery = new BooleanQuery();
		
			booleanQuery.add(new BooleanClause(countryQuery , Occur.MUST));
			booleanQuery.add(new BooleanClause(levelQuery , Occur.MUST));
			booleanQuery.add(new BooleanClause(streamQuery , Occur.MUST));
			booleanQuery.add(new BooleanClause(subjectQuery , Occur.MUST));
			booleanQuery.add(new BooleanClause(topicBooleanQuery, Occur.MUST));
		
			TopDocs results = searcher.search(booleanQuery, reader.maxDoc());
		
			System.out.println("Built Query = "+booleanQuery.toString());
			System.out.println("Searching...");
		
			Float maxScore = results.getMaxScore();
		
			ScoreDoc[] hits = results.scoreDocs;
		
			int numTotalHits = results.totalHits;
		
			System.out.println(numTotalHits + " total matching documents");
		
			for(int i = 0; i < numTotalHits; i++)
			{
				Document doc =  searcher.doc(hits[i].doc);
			
				if(hits[i].score == maxScore)
				{
					//Display scores of each document found
					//System.out.print("Question id: "+doc.get("question_id")+" Score: "+hits[i].score+"\n");
					if(doc.get("question_source").equalsIgnoreCase("QB"))
					{
						qbids.add(doc.get("question_id"));
					}
					if(doc.get("question_source").equalsIgnoreCase("WI"))
					{
						wqids.add(doc.get("question_id"));
					}
				}
			}
			}
		}
		mixedqids.put("QB", qbids);
		mixedqids.put("WI", wqids);
		//Second argument contains number of questions requested for each question type
		//i.e no of questions meant for qb and no meant for wiris
		return RandomizeOrder(mixedqids, no_of_wiris_ques, no_of_qb_ques);	
	}
	
	private static HashMap<String, ArrayList<String>>RandomizeOrder(HashMap<String, ArrayList<String>> mixedids, int no_of_wiris_ques, int no_of_qb_ques)
	{
		ArrayList<String> qbids = mixedids.get("QB");
		ArrayList<String> wids = mixedids.get("WI");
		
		ArrayList<String> temp = new ArrayList<String>();
		
		int randomNum;
		
		HashMap<String, ArrayList<String>> randomizedIds = new HashMap<String, ArrayList<String>>();

		if(!qbids.isEmpty())
		{
			for(int i=0;i<(no_of_qb_ques);i++)
			{
				randomNum = (int)(Math.random()*(qbids.size()));
				temp.add(qbids.get(randomNum));
				qbids.remove(randomNum);
				if(qbids.isEmpty())break;
			}
			qbids = new ArrayList<String>(temp);
			
		}
		randomizedIds.put("QB", qbids);
		temp.clear();
		if(!wids.isEmpty())
		{
			for(int i=0;i<(no_of_wiris_ques);i++)
			{
				randomNum = (int)(Math.random()*(wids.size()));
				temp.add(wids.get(randomNum));
				wids.remove(randomNum);
				if(wids.isEmpty())break;
			}
			wids = new ArrayList<String>(temp);
			
		}
		randomizedIds.put("WI", wids);
		
		
		return randomizedIds;
	}
}
