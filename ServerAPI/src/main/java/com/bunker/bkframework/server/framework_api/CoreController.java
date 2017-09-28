package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.server.resilience.SystemModule;

public interface CoreController extends SystemModule {
	public void destroyCore();
	public void suspendNewPeer();
	public void acceptNewPeer();
}
