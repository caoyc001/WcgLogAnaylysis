/**
 * 
 */
package io.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import common.IOHelper;
import core.Log;
import io.LogReader;


/**
 * @author yccao
 *
 */
public class OIOLogReader implements LogReader {

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.io.LogReader#read(java.io.File)
	 */
	public Log read(File file) {
		Log log = new Log(file.getName());
		if(!file.exists()){
			throw new IllegalArgumentException("Input file NOT exists");
		}
		List<String> content = new ArrayList<String>(10000);
		BufferedReader br = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while((line = br.readLine())!= null){
				content.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			IOHelper.safeClose(fis);
			IOHelper.safeClose(br);
		}
		log.setContent(content);
		return log;
	}

	public String name() {
		return "OIOLogReader";
	}

	public void write(Log log, File file) {
		// TODO Auto-generated method stub
		
	}
	

}
