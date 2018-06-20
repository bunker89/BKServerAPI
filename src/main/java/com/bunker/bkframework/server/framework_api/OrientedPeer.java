package com.bunker.bkframework.server.framework_api;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.bunker.bkframework.business.BusinessConnector;
import com.bunker.bkframework.business.BusinessPeer;
import com.bunker.bkframework.newframework.Packet;
import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.Resource;
import com.bunker.bkframework.sec.SecureFactory;
import com.bunker.bkframework.server.framework_api.ZombieKiller.Killable;

public class OrientedPeer<PacketType, SendDataType, ReceiveDataType> extends BusinessPeer<PacketType, SendDataType, ReceiveDataType> implements Killable {
	private ZombieKiller mKiller = new ZombieKiller();

	//--------���� �ȵǴ� ������
	private Resource<PacketType> mResource;
	private long mLastAttachTime = currentTime;
	private boolean mCloseLoopGuard = false;
	private boolean mIsNetInited = false;

	public OrientedPeer(PacketFactory<PacketType> factory, SecureFactory<PacketType> secFac,
			BusinessConnector<PacketType, SendDataType, ReceiveDataType> business, int maxPeer) {
		super(factory, secFac, business);
		mKiller.start();
	}

	public OrientedPeer(PacketFactory<PacketType> factory, BusinessConnector<PacketType, SendDataType, ReceiveDataType> business, int maxPeer) {
		this(factory, null, business, maxPeer);
	}

	@Override
	public void networkInited(Resource<PacketType> resource) {
		mResource = resource;
		mLastAttachTime = Calendar.getInstance().getTimeInMillis();
		mKiller.addKillable(this);
		super.networkInited(resource);
		if (mCloseLoopGuard)
			serverPeerClose();
		else
			mIsNetInited = true;
	}

	@Override
	public void decodePacket(PacketType packet, int sequence) {
		super.decodePacket(packet, sequence);
		mLastAttachTime = Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public void close() {
		if (mIsNetInited) {
			serverPeerClose();
			super.close();
		}
	}
	
	private void serverPeerClose() {
		mCloseLoopGuard = true;
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

	public ZombieKiller getZombieKiller() {
		return mKiller;
	}
	
	@Override
	protected void logPacket(long arg0, List<Packet<PacketType>> arg1, List<PacketType> arg2, List<PacketType> arg3,
			String arg4) throws IOException {
		if (arg4 != null)
			super.logPacket(arg0, arg1, arg2, arg3, arg4 + "\n" + mResource.getClientHostInfo());
		else
			super.logPacket(arg0, arg1, arg2, arg3, mResource.getClientHostInfo());
	}
}