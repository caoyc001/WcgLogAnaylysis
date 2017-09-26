package wla;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/26.
 */
public class WelcomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("wlacfg.properties");
        InputStream is = url.openStream();
        Properties p = new Properties();
        p.load(is);
        String authentication = p.get("authentication").toString();

        if(authentication.equals("0"))
        {
            RequestDispatcher dispatcher = req.getRequestDispatcher("index.html");    // 使用req对象获取RequestDispatcher对象
            dispatcher.forward(req, resp);
        }
        else{
            RequestDispatcher dispatcher = req.getRequestDispatcher("login.html");    // 使用req对象获取RequestDispatcher对象
            dispatcher.forward(req, resp);
        }
    }
}
