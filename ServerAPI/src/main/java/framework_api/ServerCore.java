package framework_api;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Peer;

public interface ServerCore<PacketType> {
	public void launch(int port);
	public LifeCycle getLifeCycle();
	public Peer<PacketType> getPrototypePeer();
}