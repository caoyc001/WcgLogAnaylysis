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

public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(wla.LoginServlet.class);



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String ip=req.getRemoteAddr();
        logger.info("请求登录的ip为:"+ip);
        String rmessage;
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("wlacfg.properties");
        InputStream is = url.openStream();
        Properties p = new Properties();
        p.load(is);
        String uName = p.get("userName").toString();
        String pWord = p.get("passWord").toString();
        String authentication= p.get("authentication").toString();
        String ip1=p.get("avaIp1").toString();

            Map<String, String> jsonMap = new HashMap();
            String rusername = req.getParameter("username");
            String rpassword = req.getParameter("password");
            if (authentication.equals("0")||(uName.equals(rusername) && pWord.equals(rpassword))) {
                rmessage = "登录成功";
                HttpSession session = req.getSession();
                session.setAttribute("login-user", rusername);


            } else {
                rmessage = "登录失败";
            }
            jsonMap.put("message", rmessage);
            logger.info("用户名" + rusername + jsonMap.get("message"));
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

            } catch (IOException e) {
                e.printStackTrace();
            }


}
}