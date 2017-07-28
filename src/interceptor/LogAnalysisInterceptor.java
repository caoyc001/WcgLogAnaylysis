/**
 * 
 */
package interceptor;

import common.Named;
import core.Log;

import java.text.ParseException;


/**
 * 根据需求解析日志的拦截器
 * @author yccao
 *
 */
public interface LogAnalysisInterceptor extends Named{


	void handle(Log log,String pattern) throws ParseException;
	
}
