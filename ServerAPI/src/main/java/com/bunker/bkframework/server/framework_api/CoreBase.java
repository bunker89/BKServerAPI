package com.bunker.bkframework.server.framework_api;

import java.util.Iterator;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Resource;
import com.bunker.bkframework.sec.SecureFactory;

abstract public class CoreBase<PacketType> extends Thread implements ServerCore<PacketType>, CoreController {
	private int port = 9011;
	public static class CoreBuilder <PacketType> {
		private CoreBase<PacketType> coreBase;
		private boolean isDefaultPeer = true;

		public CoreBuilder(Class<? extends CoreBase<PacketType>> cl) {
			try {
				coreBase = cl.newInstance();
			} catch (InstantiationException e) {
				Logger.err("CoreBase", "class loadException");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Logger.err("CoreBase", "class loadException");
				e.printStackTrace();
			}
		}
		
		public CoreBuilder<PacketType> peer(Peer<PacketType> peer) {
			coreBase.setPeer(peer);
			isDefaultPeer = false;
			return this;
		}
		
		public CoreBuilder<PacketType> useServerPeer(SecureFactory<PacketType> sec, Business<PacketType> business) {
			coreBase.usePeerServer(sec, business);
			return this;
		}
		
		public CoreBuilder<PacketType> useServerPeer(Business<PacketType> business) {
			coreBase.usePeerServer(business); 
			return this;
		}
		
		public CoreBase<PacketType> build() {
			if (isDefaultPeer)
				Logger.logging("CoreBase", "Business Peer is default!!");
			return coreBase;
		}
		
		public CoreBuilder<PacketType> setPort(int port) {
			coreBase.port = port;
			return this;
		}
		
		public CoreBuilder<PacketType> setWriteBuffer(String paramName, Object param) {
			coreBase.setParam(paramName, param);
			return this;
		}
	}

	@Override
	final public void run() {
		launch(port);
	}

	abstract void setPeer(Peer<PacketType> peer);
	abstract void usePeerServer(SecureFactory<PacketType> sec, Business<PacketType> business);
	abstract void usePeerServer(Business<PacketType> business);
	abstract String getServerLog();
	abstract void setParam(String paramName, Object param);
}