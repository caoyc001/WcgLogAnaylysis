/**
 * 
 */
package io.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import common.IOHelper;
import core.Log;
import io.LogWriter;


/**
 * @author yccao
 *
 */
public class OIOLogWriter implements LogWriter {

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.io.LogWriter#write(com.npc.lte.tools.loganalysis.io.Log, java.io.File)
	 */
	@Override
	public void write(Log log, File file) {
		if(file.exists()){
			throw new IllegalArgumentException("Output file EXISTS");
		}
		PrintWriter w = null;
		try {
			w = new PrintWriter(file);
			for(String line:log.getContent()){
				w.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			IOHelper.safeClose(w);
		}

	}

	@Override
	public String name() {
		return "OIOLogWriter";
	}

}
