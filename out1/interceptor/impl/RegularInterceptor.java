/**
 * 
 */
package interceptor.impl;

import java.util.ArrayList;
import java.util.List;
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
     public  static StringBuilder sb;
	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.common.Named#name()
	 */
	@Override
	public String name() {
		return "Regular";
	}

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.interceptor.Configurable#getKeys()
	 */
	@Override
	public String[] getKeys() {
		return new String[]{"Pattern"};
	}

	/* (non-Javadoc)
	 * @see com.npc.lte.tools.loganalysis.interceptor.LogAnalysisInterceptor#handle(com.npc.lte.tools.loganalysis.core.Log)
	 */



	public void handle(Log log,String pattren ) {

		Integer a = Integer.parseInt(pattren)+1;
		String spattren=a.toString();
		Pattern regular = Pattern.compile(pattren);
		Pattern sregular = Pattern.compile(spattren);
		boolean flag = false;
		List<Log> result = new ArrayList<Log>();
		log.setResult(result);
		Log _log = new Log(log.getName());
		_log.appendName(pattren.replaceAll("\\\\", ";"));
		result.add(_log);
		StringBuilder tempsb=new StringBuilder();
		List<String> content = log.getContent();
		for(String line : content){

			if(sregular.matcher(line).find())
				break;
			if(flag==true)
			{
				_log.getContent().add(line);
				tempsb.append(line+"\n");


			}
			if(regular.matcher(line).find()){
				flag=true;
			}
		}
		if (!tempsb.toString().equals(""))
			sb=tempsb;

		
	}


}
