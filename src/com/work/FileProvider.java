package com.work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;

public class FileProvider {
	
	public FileProvider() throws IOException {
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
			throw new IOException("SD Card is not mounted.  It is " + state + ".");
			
	}
	
	File audioPath(String path,int position) {
		
		File mFileName = new File(Environment.getExternalStorageDirectory()+ "/proloquo/" + path + "/audio/" + (position+1) +".3gp");
		return mFileName;
	}
	
	public String[] readLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    List<String> lines = new ArrayList<String>();
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) 
	        lines.add(line);
	    
	    bufferedReader.close();
	    return lines.toArray(new String[lines.size()]);
		
	}
	
	public File directory(String path) { // flag identifies whether for dialog box or tab
		File dir;
		dir = new File(Environment.getExternalStorageDirectory().getPath() + "/proloquo/" + path);
		return dir;
	}
	
	public String getTitle(String path,String position) throws Exception{
		String[] texttoshow = null;
		try {
				texttoshow = readLines(Environment.getExternalStorageDirectory().getPath() + "/Proloquo/" + path + "title.txt");
		}
			
    	catch (IOException e) {
    	}
    			
    	finally {
    		if(texttoshow == null) {
    				texttoshow = new String[files(path).length];
    				for(int i = 0; i<texttoshow.length;i++) {
    					texttoshow[i] = "Default";
    				}
    			}
    		}
		int pos = Integer.parseInt(position);
		return texttoshow[pos-1];
	}
	
	public String[] files(String path) {
		
		File dir;
		String[] fileNames;
		dir = directory(path);
		/////////// VERY IMP TAT IT IS NUMBERED
		fileNames = dir.list(new FilenameFilter() { 
   		  public boolean accept (File dir, String name) {
   		      if (new File(dir,name).isDirectory())
   		         return false;
   		      return name.endsWith(".png");
   		  }
   		  
   		});
		
		for(int i=0;i<fileNames.length;i++) 
			fileNames[i]=""+(i+1)+".png";
			
		return(fileNames);
	}
	
	public String[] textToShow(String path) {
	
		String[] texttoshow = null;
		try {
				texttoshow = readLines(Environment.getExternalStorageDirectory().getPath() + "/Proloquo/" + path + "title.txt");
		}
			
    	catch (IOException e) {
    	}
    			
    	finally {
    		if(texttoshow == null) {
    				texttoshow = new String[files(path).length];
    				for(int i = 0; i<texttoshow.length;i++) {
    					texttoshow[i] = "Default";
    				}
    			}
    		}
		
		return texttoshow;
	}
	
	public boolean checkDialogExist(String path, String position) {
		try {
		String[] info = readLines(Environment.getExternalStorageDirectory().getPath() + "/Proloquo/" + path + "info.txt");
		if(info[Integer.parseInt(position)-1].equalsIgnoreCase("touch")) {
			return true;
		}
		}
		catch(Exception e) {
			return false;
		}
		return false;
	}
}