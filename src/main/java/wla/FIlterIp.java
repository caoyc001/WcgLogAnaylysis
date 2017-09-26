package wla;

/**
 * Created by Administrator on 2017/9/5.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class FIlterIp implements Filter {

    protected String FilteredIP;
    protected String isFiltered;
    /**
     * 初始化
     */
    @Override
    public void init(FilterConfig conf) throws ServletException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("wlacfg.properties");
        InputStream is = null;
        try {
            is = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties p = new Properties();
        try {
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilteredIP = p.get("avaIp1").toString();//获取信任的ip地址
       isFiltered = p.get("ipFilter").toString();//获取过滤器开关
        if (FilteredIP==null) {
            FilteredIP="";
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");//设置编码格式
        RequestDispatcher dispatcher = request.getRequestDispatcher("error.html");//定义错误转向页面
        //读出本地Ip
        String remoteIP = request.getRemoteAddr();
        //将其与要过滤掉的Ip比较，如果相同，就转到错误处理界面
        if (isFiltered.equals("1")&&!remoteIP.equals(FilteredIP)) {
            dispatcher.forward(request, response);
        }else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
