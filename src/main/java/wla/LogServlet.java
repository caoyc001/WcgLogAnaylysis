package wla;

import core.LogAnalysis;
import interceptor.impl.NumInterceptor;
import interceptor.impl.RegularInterceptor;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/28.
 */

public class LogServlet extends HttpServlet{
    private static final Logger logger= LoggerFactory.getLogger(wla. LogServlet.class);

    Pattern p = Pattern.compile("[0-9]{1,3}");
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = req.getSession();

        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("wlacfg.properties");
        InputStream is = url.openStream();
        Properties p = new Properties();
        p.load(is);
        String authentication = p.get("authentication").toString();
        if(session.getAttribute("login-user")!=null||authentication.equals("0")) {
            try {
                logger.info("收到日志搜索请求,搜索序列号为:" + req.getParameter("seq") + "的日志");
                RegularInterceptor.stringMap = new ConcurrentHashMap();
                searchSeq(req.getParameter("seq"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, String> jsonMap = new HashMap();
            String fileName = "", log = "";
            for (Map.Entry<String, String> temp : RegularInterceptor.stringMap.entrySet()) {
                fileName += (temp.getKey() + "\t");

                log += temp.getValue();

            }
            jsonMap.put(fileName, log);
            JSONObject jsonObject = JSONObject.fromObject(jsonMap);
            String content = jsonObject.toString();
            // System.out.println(jsonObject);
            try {
                response.setContentType("application/json" + ";charset=UTF-8");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.getWriter().write(content);
                response.getWriter().flush();
                RegularInterceptor.stringMap = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }
    public void searchSeq(String num) throws Exception {
        String fileName=NumInterceptor.cacheMap.get(num);
        Matcher filem = p.matcher(fileName);
        String fileNum="";
        while(filem.find())

        {
            fileNum=filem.group();

        }

        List<String> fileList=new ArrayList();
        fileList.add(fileName);
        if(fileNum.equals(""))
            fileList.add(fileName+".1");
        else {
            if (fileNum .equals("1") ) {
                fileList.add("cups_wcg.log");
                fileList.add("cups_wcg.log.2");
            } else {
                fileList.add("cups_wcg.log." + (Integer.parseInt(fileNum) - 1));
                fileList.add("cups_wcg.log." + (Integer.parseInt(fileNum) + 1));
            }
        }
        RegularInterceptor numInterceptor = new RegularInterceptor();
        LogAnalysis logAnalysis=new LogAnalysis(numInterceptor,num);
        LogAnalysis.logThread logThread=logAnalysis.new logThread(fileList);
        logThread.run();
        for(String file:fileList)
            logger.info("文件名为:"+file);


    }


}
