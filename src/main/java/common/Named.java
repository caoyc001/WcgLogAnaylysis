/**
 * 
 */
package common;

import java.io.File;

import core.Log;

/**
 * @author yccao
 *
 */
public interface Named {

	String name();

	Log read(File file);

	void write(Log log, File file);
}
