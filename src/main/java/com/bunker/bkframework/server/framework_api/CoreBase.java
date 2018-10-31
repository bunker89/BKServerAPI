package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Writer;
import com.bunker.bkframework.server.BKLauncher;

/**
 * 
 * 
 * @author ys89
 *
 * @param <PacketType>
 */
public abstract class CoreBase<PacketType> implements ServerCore, CoreController {
	private BKLauncher mLauncher;

	public CoreBase() {
	}

	public Object getSystemParam(String key) {
		return mLauncher.getSystemParam(key);
	}
	
	/**
	 * do not use this method.
	 * this method is only called by {@link BKLauncher#initCore(ServerCore)}
	 * use {@link BKLauncher#initCore(ServerCore)} instead of this method
	 * 
	 */
	@Override
	public void setBKLauncher(BKLauncher launcher) {
		mLauncher = launcher;
	}

	public void start() {
	}
	
	/**
	 * initialize peer for core. peer is generated with prototype pattern.
	 * any connection will have a separated peer.
	 * @param peer prototype peer
	 * @param writer peer send data through writer class.
	 * @param life core can choice life cycle of peer. it can be pooling or interrupt.
	 */
	protected void initPeer(Peer<PacketType> peer, Writer<PacketType> writer, LifeCycle life) {
		peer.setLifeCycle(life);
		peer.setWriter(writer);
	}

	/**
	 * return standard peer instance.
	 * @return prototype peer.
	 */
	public abstract Peer<PacketType> getPrototypePeer();
	
	/**
	 * factory class of creating packet. see {@link PacketFactory}
	 * @return
	 */
	public abstract PacketFactory<PacketType> createPacketFactory();
}
