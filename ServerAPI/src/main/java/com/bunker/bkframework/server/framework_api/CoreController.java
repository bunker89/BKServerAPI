package com.bunker.bkframework.server.framework_api;

public interface CoreController {
	public void destroyCore();
	public void suspendNewPeer();
	public void restart();
}
