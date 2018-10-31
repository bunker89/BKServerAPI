package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.server.BKLauncher;

/**
 * 
 * @author ys89
 *
 */
public interface ServerCore {
	public void start();
	public void setParam(String paramName, Object param);
	public void setBKLauncher(BKLauncher launcher);
}