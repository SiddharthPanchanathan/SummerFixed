package com.hm.newAge.services;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;



public  class Mapper1 {
		public String getQuestion() throws IOException{
			
			//String Country, String Level, String Stream
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			String[] keys= new String[20];
			keys[0] ="Ordering and Comparing Fractions"; // 1
			keys[1]="Prime Factorisation"; // 1
			keys[2]="Ratio, Rate and Proportion";//1
			keys[3]="Simplification of Linear Algebraic Expressions"; //1
			keys[4]="Factorisation of Linear Algebraic Expressions";//1
			keys[5]="Fundamental Algebra "; //2
			keys[6]="Percentage"; // 2
			keys[7]="Geometry"; // 1
			keys[8]="Bar Graph"; // 1
			keys[9]="Number Patterns and Number Sequences"; // 1
			keys[10]="Geometry of Solids";// 1
			keys[11]="Writing Algebraic Equations to Solve Problems"; // 1
			keys[12]="Comparing Two Quantities by Percentage";// 1
			keys[13]="Statistics";// 1
			keys[14]="Highest Common Factor";// 1
			keys[15]="Lowest Common Multiple";// 1
			keys[16]="Angles, Triangles and Polygons";// 1
			keys[17]="Data Handling";// 1
			keys[18]="Functions and Graphs";// 1
			keys[19]="Writing Algebraic Equations to Solve Problems";// 1
			int z = 0;		
			
			  for(int i=0;i<5;i++){
				  int s= (int)(Math.random()*100 + 1);
				  
				  			if(s%3==0)
				  			{
				  				z = 2;
	/*Randomness Element*/  }
				  			else
				  			{
				  				z=1;
				  			}
			    	map.put(keys[i],new Integer(z));
			    	}
			  
			    map.put(keys[5], new Integer(2));
			    map.put(keys[6], new Integer(1));
			   
			  for(int j=7;j<20;j++){
			    	map.put(keys[j], new Integer(z));
			    }
		    
		
			  String[] S = new String[20];
			  String MainQuestion="";
			//Add functionality to counter case where no result 
			  
			  int n,decide = 0;
			  //int count=0;
			  MainQuestion+="<questions>";
			  
			  
			  
			  
			  
			  for (int k=0;k<20;k++){
				  if(z==1)
				  {
				        n=(int)(Math.random()*100 + 1);
				  		if(n<=50){
				  				//decide=(int) Math.ceil(Math.random()+1);
				  			decide=1;
				  					}
				  		else{
				  				decide=0;
				  					}
				  }		
				  else{
					  decide=0;
				  }
				
				if(decide>0){
					 S[k]= TestGenerator.getTest("Singapore", "Secondary 1","Express",
							  "Mathematics", keys[k], map.get(keys[k]), decide);
					 
					 if(S[k].contains("No wiris tests found for the query")){
						 S[k]= TestGenerator.getTest("Singapore", "Secondary 2","Express",
								  "Mathematics","Solutions of Equations" , map.get(keys[k]),decide);
					 }
						
					
				}
				else{
					int c=map.get(keys[k])+1;
					S[k]= TestGenerator.getTest("Singapore", "Secondary 1","Express",
							  "Mathematics", keys[k], c, decide);
					 
					 if(S[k].contains("No wiris tests found for the query")){
						 S[k]= TestGenerator.getTest("Singapore", "Secondary 2","Express",
								  "Mathematics", "Inequality", map.get(keys[k]),decide);
					 }
						
					
				}
			 
			  
			  
			  MainQuestion=MainQuestion.concat(S[k]+"\n");
			  
			//  if(S[k].contains("School") ){
				//  count++;
			  //}
			  //System.out.println(S[k]);
			  }
			  MainQuestion+="</questions>";
			  
			 
			 //System.out.println(count);
			// System.out.println(MainQuestion);
		    
			 return MainQuestion;
			 
			 
			 
			 
		}	
		
		
		}
	/*
	 * <form action="demo_form.asp">
Country: <input type="text" name="Country" value="Singapore"><br>
Level: <input type="text" name="Level" value="Secondary 1"><br>
Stream: <input type="text" name="Stream" value="Express"><br>
</form>
*/
