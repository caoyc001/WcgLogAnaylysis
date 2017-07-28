package interceptor.impl;

import com.sun.xml.internal.bind.v2.util.QNameMap;
import core.Log;
import interceptor.LogAnalysisInterceptor;

import javax.swing.*;
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
    public  static  Set<Integer> numSet=new HashSet<>();
    public static Map<Date,Integer> numMap=new TreeMap<>();
    private final Pattern numpattern=Pattern.compile("\\u5e8f\\u5217\\u53f7\\d{6}");
    private final Pattern datepattern=Pattern.compile("20[012]\\d-[01]\\d-[0123]\\d\\s\\d\\d:\\d\\d:\\d\\d");

    @Override
    public String name() {
        return "Num";
    }
    public void   handle(Log log,String pattren ) throws ParseException {


        Pattern regular = Pattern.compile(pattren);
        boolean flag = false,seqFlag=false,dateFlag=false;
        Integer seq=new Integer(0);
        Date date=new Date();
        List<Log> result = new ArrayList<Log>();
        log.setResult(result);

        Log _log = new Log(log.getName());
        _log.appendName(pattren.replaceAll("\\\\", ";"));

        result.add(_log);
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
                    seq=Integer.parseInt(seqm.group().substring(3,9));

                }

            }
            if(flag&&dateFlag)
            {    Matcher datem = datepattern.matcher(line);
                while(datem.find())

                {    SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    dateFlag=false;
                    date=sdf.parse(datem.group());

                }

            }
            if(flag&&!seqFlag&&!dateFlag)
            {
                flag=false;
                if(!numMap.containsValue(seq))
                numMap.put(date,seq);

            }
        }
       /* for(Map.Entry<Date,Integer> tmap:numMap.entrySet())
        {
            System.out.println(tmap.getKey()+","+tmap.getValue());

        }*/


    }
}
