package framework_api;

import java.util.Calendar;

import com.bunker.bkframework.business.BusinessConnector;
import com.bunker.bkframework.business.BusinessPeer;
import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.Resource;
import com.bunker.bkframework.sec.SecureFactory;

import framework_api.ZombieKiller.Killable;

public class ServerPeer<PacketType> extends BusinessPeer<PacketType> implements Killable {
	private Resource<PacketType> mResource;
	private ZombieKiller mKiller = new ZombieKiller();

	//--------공유 안되는 데이터
	private long mLastAttachTime = currentTime;
	private boolean mCloseLoopGuard = false;

	@SuppressWarnings("unchecked")
	public ServerPeer(PacketFactory<PacketType> factory, SecureFactory<PacketType> secFac,
			BusinessConnector<PacketType> business, int maxPeer) {
		super(factory, secFac, business);
		mKiller.start();
	}

	public ServerPeer(PacketFactory<PacketType> factory, BusinessConnector<PacketType> business, int maxPeer) {
		this(factory, null, business, maxPeer);
	}

	@Override
	public void networkInited(Resource<PacketType> resource) {
		mResource = resource;
		mLastAttachTime = Calendar.getInstance().getTimeInMillis();
		mKiller.addKillable(this);
		super.networkInited(resource);
	}

	@Override
	public void decodePacket(PacketType packet, int sequence) {
		super.decodePacket(packet, sequence);
		mLastAttachTime = Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public void close() {
		if (mCloseLoopGuard == true)
			return;
		mCloseLoopGuard = true;
		super.close();
		mResource.destroy();
		mKiller.removeKillable(this);
		mWriter.destroy();
	}

	@Override
	public long getLastEventTime() {
		return mLastAttachTime;
	}

	@Override
	public void kill() {
		close();
	}
}