package com.hm.newage.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

public class Queries {

		
		//public static String appPath= new java.io.File("").getAbsolutePath();
		public static String appPath="D:/Eclipse Workspace/QuickTest";
		static File propFile = new File( appPath+ "/WebContent/config/properties/"+machine()+".globals.properties");
	    private static long lastModified;
	    private static Properties properties = null;

		private static String machine() {
	        BufferedReader reader;
	        StringBuilder stringBuilder = new StringBuilder();
	        String line = null;
	        String ls = System.getProperty("line.separator");
			try {
				reader = new BufferedReader( new FileReader (appPath + "/WebContent/config/machine.txt"));
				while( ( line = reader.readLine() ) != null ) {
	            stringBuilder.append( line );
	            stringBuilder.append( ls );}
			}
	        catch (Exception e) {e.printStackTrace();}
	        return stringBuilder.toString().trim();
	    }
		
	    private static Properties loadProperties() 
	    {
	        Properties p = new Properties();
	        try {
	            lastModified = propFile.lastModified();
	            FileInputStream fis = new FileInputStream(propFile);
	            p.load(fis);
	            fis.close();
	            return p;
	            } 
	        catch (Exception e) 
	        {
	            System.out.println("Error in loading properties file: " + propFile);
	            e.printStackTrace();
	            return null;
	        }
	    }
	        public static void checkProps() 
	        {
	        if (properties == null || propFile.lastModified() > lastModified) {
	            properties = loadProperties();
	        }
	    }
	        
	        public static String getQuery(String key) 
	        {
	        checkProps();
	        if (properties != null) {
	            Object obj = properties.get(key);
	            if (obj != null) {
	                return ((String) obj).trim();
	            }
	        }
	        return null;
	        }
}	        
