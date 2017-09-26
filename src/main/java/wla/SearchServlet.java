package wla;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import core.LogAnalysis;
import interceptor.impl.NumInterceptor;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/7/28.
 */

public class SearchServlet extends HttpServlet{
    private static final Logger logger= LoggerFactory.getLogger(wla.SearchServlet.class);
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
            String key = req.getParameter("key");
            key = URLEncoder.encode(key, "ISO-8859-1");
            key = URLDecoder.decode(key, "UTF-8");
            key = escapeExprSpecialWord(key);
            logger.info("收到序列号搜索请求" + key);
            try {
                long startTime = System.currentTimeMillis();
                search(req.getParameter("seq"), key, req.getParameter("date"));
                long endTime = System.currentTimeMillis();
                logger.info("搜索时间为： " + (endTime - startTime) + "ms");
            } catch (Exception e) {
                e.printStackTrace();
            }

            TreeMap<String,String>map=new TreeMap();
            for(Map.Entry<String,String>entry:NumInterceptor.numMap.entrySet())
            {  map.put(entry.getValue(),entry.getKey());
            }
            NavigableMap numMap=  map.descendingMap();
            map=new TreeMap();
            Set entries =numMap.entrySet();
            Iterator it = null;
            if (entries != null)
                it = entries.iterator();

            for(int k=1; it.hasNext();k++){
                Map.Entry entry = (Map.Entry) it.next();
                String key1 = entry.getKey().toString();
                String value1 = entry.getValue().toString();
                //logger.info(key1);
                map.put(key1,value1);
                if(k>1000)
                    break;
            }

            JSONObject jsonObject = JSONObject.fromObject(map.descendingMap());
            String content = jsonObject.toString();
            logger.info("搜索完成");
            //System.out.println(jsonObject);
            try {
                response.setContentType("application/json" + ";charset=UTF-8");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.getWriter().write(content);
                response.getWriter().flush();
                NumInterceptor.numMap = null;
                numMap=null;
                map=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }
    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
    public void search(String seq,String pattern,String date) throws Exception {
        NumInterceptor.numMap=new HashMap();
        if(!seq.equals("")) {
            NumInterceptor numInterceptor = new NumInterceptor();
            LogAnalysis logAnalysis = new LogAnalysis(numInterceptor,"序列号"+seq);
            logAnalysis.start();
           // System.out.print("sucess");
            return ;
        }
        if(pattern.equals("")&&date.equals(""))
        {

            return ;
        }

        if(date.equals("")||seq.equals("")) {
            String searchContent=date.equals("")?pattern:date;
            NumInterceptor numInterceptor = new NumInterceptor();
            LogAnalysis logAnalysis = new LogAnalysis(numInterceptor, searchContent);
            logAnalysis.start();
           // System.out.print("sucess");



        }
    }

}
