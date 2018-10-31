package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.server.resilience.SystemModule;

/**
 * 
 * interface of controll server.
 * instance of this class are must implementation to control method of server operation.
 * it can be used from remote server or terminal command.
 * @author ys89
 *
 */
public interface CoreController extends SystemModule {
	public void destroyCore();
	public void suspendNewPeer();
	public void acceptNewPeer();
}