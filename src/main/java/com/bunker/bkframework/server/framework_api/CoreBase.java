package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Writer;
import com.bunker.bkframework.server.BKLauncher;

public abstract class CoreBase<PacketType> implements ServerCore, CoreController {
	private BKLauncher mLauncher;

	public CoreBase() {
	}

	public Object getSystemParam(String key) {
		return mLauncher.getSystemParam(key);
	}
	
	@Override
	public void setBKLauncher(BKLauncher launcher) {
		mLauncher = launcher;
	}

	public void start() {
	}
	
	protected void initPeer(Peer<PacketType> peer, Writer<PacketType> writer, LifeCycle life) {
		peer.setLifeCycle(life);
		peer.setWriter(writer);
	}

	public abstract Peer<PacketType> getPrototypePeer();
}
