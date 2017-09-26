/**
 * 
 */
package interceptor.impl;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import core.Log;
import interceptor.ConfigurableBase;
import interceptor.LogAnalysisInterceptor;


/**
 * 根据正则表达式匹配日志
 * @author yccao
 *
 */
public class RegularInterceptor extends ConfigurableBase implements
		LogAnalysisInterceptor {
     public  static Map<String,String> stringMap=new ConcurrentHashMap(500);
	private final Pattern datepattern=Pattern.compile("20[012]\\d-[01]\\d-[0123]\\d\\s\\d\\d:\\d\\d:\\d\\d");

	/* (non-Javadoc)
         * @see com.npc.lte.tools.loganalysis.common.Named#name()
         */
	public String name() {
		return "Regular";
	}

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.interceptor.Configurable#getKeys()
	 */
	public String[] getKeys() {
		return new String[]{"Pattern"};
	}

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.interceptor.LogAnalysisInterceptor#handle(com.npc.lte.tools.loganalysis.core.Log)
	 */

	public static String htmlReplace(String str){
		//str = str.replace("&ldquo;", "“");
		//str = str.replace("&rdquo;", "”");
		//str = str.replace("&nbsp;", " ");
		//str = str.replace("&", "&amp;");
		//str = str.replace("&#39;", "'");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		//str = str.replace("&rsquo;", "’");
		//str = str.replace("&mdash;", "—");
		//str = str.replace("&ndash;", "–");
		return str;
	}
@Override
	public  void  handle (Log log,String pattren ,String fileName) {
		Integer a = Integer.parseInt(pattren)+1;
		Pattern regular = Pattern.compile("\\u5e8f\\u5217\\u53f7"+pattren);
		boolean flag = false;
		StringBuilder tempsb=new StringBuilder("");
		List<String> content = log.getContent();
		for(String line : content){
			if(datepattern.matcher(line).find())
				flag=false;
			if(regular.matcher(line).find()){
				flag=true;
			}
			if(flag==true)
			{tempsb.append(line+"/n");
			}

		}
		if (!tempsb.toString().equals("")&&!stringMap.containsKey(fileName)) {
			String st=htmlReplace(tempsb.toString());
			st = st.replace("/n", "<br/>");
			stringMap.put(fileName,st);
		}

		
	}

	public Log read(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(Log log, File file) {
		// TODO Auto-generated method stub
		
	}


}
