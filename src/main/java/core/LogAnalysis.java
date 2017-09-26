/**
 * 
 */
package core;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import common.IOHelper;
import interceptor.LogAnalysisInterceptor;
import io.LogReader;
import io.LogWriter;
import io.impl.NIOLogReader;
import io.impl.NIOLogWriter;
import io.impl.OIOLogReader;
import io.impl.OIOLogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yccao
 * 
 */
public class LogAnalysis {
	List<LogAnalysisInterceptor> interceptors;
	private static final Logger logger= LoggerFactory.getLogger(core.LogAnalysis.class);
	LogReader reader;
	LogWriter writer;
	List<String> fileName;
    String pattern;
	LogAnalysisInterceptor logAnalysisInterceptor;
	private static int num;
	private static String logdir;
	private final Pattern filePattern=Pattern.compile("cups_wcg");
	public static  int percent;

	public LogAnalysis(LogAnalysisInterceptor logAnalysisInterceptor,String pattern) throws Exception {
		super();
		this.reader = new NIOLogReader();
		this.writer = new  NIOLogWriter();

		getProperties();
		this.pattern = pattern;
		this.logAnalysisInterceptor=logAnalysisInterceptor;

	}

	public void start() throws ParseException, InterruptedException {
         int dnum=fileName.size()/num+1;
         System.out.print(dnum);
         int j=0;
         boolean flag=true;
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num);
		//按照文件数目将文件列表分配到线程池,若分配完毕则退出循环
       tf: for(int i=0;i<num;i++)
		{  List<String> ls=new ArrayList();
		   for(int t=0;t<dnum;t++)
		   {
		   	if(j==fileName.size()-1) {
				flag = false;
				break ;
			}
		   	ls.add(fileName.get(j));
		   	j++;

		   }
           fixedThreadPool.execute(new logThread(ls));
		   if(flag==false)
		   	break ;


		}
		fixedThreadPool.shutdown();
		try {
			fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

			logger.info("线程池任务结束");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	/*	for (File input:inputs)
		{
			Log source = reader.read(input);
			List<Log> sources = new ArrayList<Log>();
			sources.add(source);

				List<Log> target = new ArrayList<Log>();
				for (Log log : sources) {
					logAnalysisInterceptor.handle(log,pattern);
					target.addAll(log.getResult());
				}


		}*/
	}
	private void getProperties() throws Exception {
        try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("wlacfg.properties");
			InputStream is = url.openStream();
			Properties p = new Properties();
			p.load(is);
			logdir = p.get("fileDir").toString();
			num = Integer.parseInt(p.getProperty("threadNum"));
			logger.info("成功加载日志路径:"+logdir);
			File[] files = new File(logdir).listFiles();
			fileName = new ArrayList() ;
			for (int i = 0; i < files.length; i++) {
				if(filePattern.matcher(files[i].getName()).find())
					fileName.add(files[i].getName());
			}
			IOHelper.safeClose(is);
					}catch (Exception e)
		{
			logger.info("加载配置文件失败");
		}




	}
	//处理日志文件的线程
	public class logThread implements Runnable
	{   private  List<String> ls;
        public logThread(List<String> ls)
		{
          this.ls=ls;
		}
		@Override
		public void run() {

        	for(int i=0;i<ls.size();i++) {
        		String filename=ls.get(i);
				logger.info("正在处理文件:"+filename);
        		File input=new File(logdir+"//"+filename);
				Log source = reader.read(input);
					try {
						logAnalysisInterceptor.handle(source, pattern, filename);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
                 percent=(i+1)*100/ls.size();



			}
		}
	}
}
