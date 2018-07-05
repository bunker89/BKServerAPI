package com.bunker.bkframework.server.framework_api.text;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.BusinessPeer;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.server.framework_api.CoreBase;
import com.bunker.bkframework.text.TextBusinessConnector;
import com.bunker.bkframework.text.TextPacketFactory;

public class TextServerCore extends CoreBase<String> {
	private BusinessPeer<String, String, String> mPrototype;	

	public TextServerCore(Business<String, String, String> business) {
		mPrototype = new BusinessPeer<>(new TextPacketFactory(), new TextBusinessConnector(business));
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
}