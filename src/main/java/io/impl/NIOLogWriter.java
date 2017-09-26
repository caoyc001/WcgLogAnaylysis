/**
 * 
 */
package io.impl;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import common.IOHelper;
import core.Log;
import io.LogWriter;


/**
 * @author yccao
 *
 */
public class NIOLogWriter implements LogWriter {

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.io.LogWriter#write(com.npc.lte.tools.loganalysis.io.Log, java.io.File)
	 */
	public void write(Log log, File file) {
		if(file.exists()){
			throw new IllegalArgumentException("Output file EXISTS");
		}
		Charset utf_8 = Charset.forName("UTF-8");
		RandomAccessFile rFile = null;
		FileChannel fc = null;
		try {
			rFile = new RandomAccessFile(file,"rw");
			fc = rFile.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 128);
			for(String line : log.getContent()){
				buffer.put(line.getBytes(utf_8));
				buffer.put(Character.LETTER_NUMBER);
				buffer.flip();
				fc.write(buffer);
				buffer.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			IOHelper.safeClose(rFile);
			IOHelper.safeClose(fc);
		}

	}

	public String name() {
		return "NIOLogWriter";
	}

	public Log read(File file) {
		// TODO Auto-generated method stub
		return null;
	}

}
