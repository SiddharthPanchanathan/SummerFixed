package com.hm.newAge.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.hm.newAge.resources.LoadProperties;


public class Searcher {
	private String indexPath;
	private HashMap<String, ArrayList<String>> questionIds;
	
	public Searcher(){
		indexPath = LoadProperties.getQuery("search_index_dir");
	}
	public HashMap<String, ArrayList<String>> doSearch(ArrayList<String> searchWords, int no_of_wiris_ques, int no_of_qb_ques){
		
		try{
			IndexReader reader = IndexReader.open(FSDirectory.open(new File(indexPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
			setQuestionIds(SearchUtil.search(searchWords, analyzer, searcher, reader, "topic_tree", no_of_wiris_ques, no_of_qb_ques));
			
			searcher.close();
			reader.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return getQuestionIds();
		
	}
	
	
	public void setQuestionIds(HashMap<String, ArrayList<String>> hashMap) {
		this.questionIds = hashMap;
	}
	public HashMap<String, ArrayList<String>> getQuestionIds() {
		return questionIds;
	}

}

