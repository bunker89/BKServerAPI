package com.bunker.bkframework.server.reserved;

import java.util.List;

/**
 * 
 * 
 * @author ����
 *
 */
public interface LogComposite {
	public void bindAction();
	public List<Pair> logging();
	public void releaseLog();
}