package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Peer;

public interface ServerCore<PacketType> {
	public void start();
	public Peer<PacketType> getPrototypePeer();
}