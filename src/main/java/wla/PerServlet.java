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
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/28.
 */

public class PerServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(wla.PerServlet.class);



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String> jsonMap = new HashMap();

        jsonMap.put("per", new Integer(LogAnalysis.percent).toString());
        logger.info(jsonMap.get("per"));
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