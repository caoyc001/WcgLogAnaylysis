package Test;

/**
 * Created by Administrator on 2017/7/19.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {

    public static void main(String args[]) {
        String str = "2017-07-05 01:43:57,190 [LogOutputThread0] INFO  com.cup.wcg.clients.instances.specs.cmbc.util.security.SecuRuleSM203 - >>>转base64";


        Pattern r = Pattern.compile("20[012]\\d-[01]\\d-[0123]\\d\\s\\d\\d:\\d\\d:\\d\\d");
        Matcher m = r.matcher(str);

        while(m.find())

        {

            System.out.println(m.group()); //每次返回第一个即可 可用groupcount()方法来查看捕获的组数 个数

        }
    }

}