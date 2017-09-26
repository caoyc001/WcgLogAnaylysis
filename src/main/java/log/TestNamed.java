package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/9/20.
 */
public class TestNamed extends Thread{
    private static final Logger logger= LoggerFactory.getLogger(interceptor.impl.NumInterceptor.class);
    public static void main(String[]args)
    {   for (int i=0;i<100;i++)
             new  TestNamed().start();

    }

    @Override
    public void run() {
        while (true)
        logger.info("test"+this.toString());
    }
}
