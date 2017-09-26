/**
 * 
 */
package interceptor;

import java.util.Properties;

/**
 * @author yccao
 *
 */
public interface Configurable {

	String[] getKeys();
	
	void setProperties(Properties p);
	
	Properties getProperties();
	
	void addProperty(String key, String value);
}
