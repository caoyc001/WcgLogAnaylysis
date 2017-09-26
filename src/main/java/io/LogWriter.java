/**
 * 
 */
package io;

import java.io.File;

import common.Named;
import core.Log;


/**
 * @author yccao
 *
 */
public interface LogWriter extends Named{

	void write(Log log, File file);

	String name();
}
