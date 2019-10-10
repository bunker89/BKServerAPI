package com.bunker.bkframework.server.reserved;

import java.util.List;

/**
 * 
 * 
 *
 */
public interface LogComposite {
	public void bindAction();
	public List<Pair> logging();
	public void releaseLog();
	public void invokeTestErr();
}