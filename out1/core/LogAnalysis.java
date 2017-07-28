/**
 * 
 */
package core;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import interceptor.LogAnalysisInterceptor;
import interceptor.impl.RegularInterceptor;
import io.LogReader;
import io.LogWriter;
import interceptor.impl.NumInterceptor;

/**
 * @author yccao
 * 
 */
public class LogAnalysis {
	List<LogAnalysisInterceptor> interceptors;
	LogReader reader;
	LogWriter writer;
	List <File>inputs;
    String pattern;
	LogAnalysisInterceptor logAnalysisInterceptor;
	public LogAnalysis(LogAnalysisInterceptor logAnalysisInterceptor,LogReader reader, LogWriter writer, List <File> inputs,String pattern) {
		super();
		this.reader = reader;
		this.writer = writer;
		this.inputs = inputs;
		this.pattern = pattern;
		this.logAnalysisInterceptor=logAnalysisInterceptor;

	}

	public void start() throws ParseException {
		for (File input:inputs)
		{
			Log source = reader.read(input);
			List<Log> sources = new ArrayList<Log>();
			sources.add(source);

				List<Log> target = new ArrayList<Log>();
				for (Log log : sources) {
					logAnalysisInterceptor.handle(log,pattern);
					target.addAll(log.getResult());
				}
				sources = target;

			for (Log log : sources) {


			}
		}
	}
}
