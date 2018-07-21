package com.bunker.bkframework.server.framework_api.text;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.BusinessPeer;
import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.PeerLife;
import com.bunker.bkframework.server.framework_api.CoreBase;
import com.bunker.bkframework.text.TextBusinessConnector;
import com.bunker.bkframework.text.TextPacketFactory;
import com.bunker.bkframework.text.TextWriter;

public class TextServerCore extends CoreBase<String> implements LifeCycle {
	private final String _TAG = "TextServerCore";
	private Peer<String> mPrototype;	

	public TextServerCore(Business<String, String, String> business) {
		mPrototype = new BusinessPeer<>(createPacketFactory(), new TextBusinessConnector(business));
	}

	@Override
	public Peer<String> getPrototypePeer() {
		return mPrototype;
	}

	@Override
	public void destroyCore() {
	}

	@Override
	public void setParam(String paramName, Object param) {
	}

	public String doWork(String string) {
		TextWriter writer = new TextWriter();
		@SuppressWarnings("unchecked")
		Peer<String> newPeer = newPeer(writer);
		newPeer.dispatch(string);
		newPeer.run();
		return writer.getResult();
	}

	private Peer<String> newPeer(TextWriter writer) {
		try {
			Peer<String> newPeer = (Peer<String>) mPrototype.clone();
			initPeer(newPeer, writer, this);
			newPeer.networkInited(new TextResource());
			newPeer.setWriter(writer);

			return newPeer;
		} catch (CloneNotSupportedException e) {
			Logger.err(_TAG, "clone err", e);
		}
		
		return null;
	}

	//---------------아래로 서버 컨트롤 관련
	@Override
	public void suspendNewPeer() {
	}

	@Override
	public void acceptNewPeer() {
	}

	@Override
	public void moduleForceRestart() {
	}

	@Override
	public void moduleSafetyStop() {
	}

	@Override
	public boolean isStoped() {
		return false;
	}

	@Override
	public boolean moduleStart() {
		return false;
	}

	@Override
	public void manageLife(PeerLife arg0) {
		while (arg0.needRecycle()) {
			arg0.life();
		}
	}

	@Override
	public PacketFactory<String> createPacketFactory() {
		return new TextPacketFactory();
	}
}