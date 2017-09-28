package com.bunker.bkframework.server.reserved;

import java.util.List;

/**
 * 
 * 
 * @author ±¤¼ö
 *
 */
public interface LogComposite {
	public void bindAction();
	public List<Pair> logging();
	public void releaseLog();
}