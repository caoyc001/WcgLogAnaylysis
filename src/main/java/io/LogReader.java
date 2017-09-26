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
public interface LogReader extends Named{

	Log read(File file);

	String name();
}
