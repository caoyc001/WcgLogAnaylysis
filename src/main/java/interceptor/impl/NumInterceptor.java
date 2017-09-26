package interceptor.impl;

import com.sun.xml.internal.bind.v2.util.QNameMap;

import core.Log;
import interceptor.LogAnalysisInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.io.File;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**根据内容查找序列号
 * Created by yccao on 2017/7/18.
 */
public class NumInterceptor implements LogAnalysisInterceptor{
    public static Map<String,String> numMap=new HashMap();
    public static Map<String,String> cacheMap=new HashMap();
    private final Pattern numpattern=Pattern.compile("\\[\\u5e8f\\u5217\\u53f7\\d{1,6}\\]");
    private final Pattern datepattern=Pattern.compile("20[012]\\d-[01]\\d-[0123]\\d\\s\\d\\d:\\d\\d:\\d\\d");
    private static final Logger logger= LoggerFactory.getLogger(interceptor.impl.NumInterceptor.class);
    public String name() {
        return "Num";
    }
    public void   handle(Log log,String pattren,String fileName ) throws ParseException {

        logger.info("开始搜索关键字:"+pattren);
        Pattern regular = Pattern.compile(pattren);
        boolean flag = false,seqFlag=false,dateFlag=false;
        String seq=new String();
        String date=new String();
        List<String> content = log.getContent();
        for(String line : content){


            if(regular.matcher(line).find()){
                flag=true;
                seqFlag=true;
                dateFlag=true;

            }
            if(flag&&seqFlag)
            {    Matcher seqm = numpattern.matcher(line);
                while(seqm.find())

                {
                    seqFlag=false;
                    seq=seqm.group().substring(4,10);

                }

            }
            if(flag&&dateFlag)
            {    Matcher datem = datepattern.matcher(line);
                while(datem.find())

                {
                    dateFlag=false;
                    date=datem.group();

                }

            }
            if(flag&&!seqFlag&&!dateFlag)
            {
                flag=false;
                if(!numMap.containsKey(seq))
                numMap.put(seq,date);
                if(!cacheMap.containsKey(seq))
                    cacheMap.put(seq,fileName);

            }
        }
      /*  while(true) {
            for (Map.Entry<String, String> tmap : numMap.entrySet()) {
                logger.info(tmap.getKey() + "," + tmap.getValue());

            }
        }*/


    }
	public Log read(File file) {
		// TODO Auto-generated method stub
		return null;
	}
	public void write(Log log, File file) {
		// TODO Auto-generated method stub
		
	}
}
