package com.bunker.bkframework.server.framework_api;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.sec.SecureFactory;

abstract public class SocketCore<PacketType, SendDataType, ReceiveDataType> extends CoreBase<PacketType> implements Runnable {
	private int port = 9011;
	private String _TAG = getClass().getSimpleName();

	public static class SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> {
		private SocketCore<PacketType, SendDataType, ReceiveDataType> coreBase;
		private boolean isDefaultPeer = true;

		public SocketCoreBuilder(Class<? extends SocketCore<PacketType, SendDataType, ReceiveDataType>> cl) {
			try {
				coreBase = cl.newInstance();
			} catch (InstantiationException e) {
				Logger.err("CoreBase", "class loadException", e);
			} catch (IllegalAccessException e) {
				Logger.err("CoreBase", "class loadException", e);
			}
		}

		public SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> initLoggingSystem() {
			Boolean debug = (Boolean) coreBase.getSystemParam("debugging");
			if (debug == null || debug == false) {
				Logger.mLog = new ServerDefaultLog();
			}
			return this;
		}

		public SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> peer(Peer<PacketType> peer) {
			coreBase.setPeer(peer);
			isDefaultPeer = false;
			return this;
		}

		public SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> useServerPeer(SecureFactory<PacketType> sec, Business<PacketType, SendDataType, ReceiveDataType> business) {
			coreBase.usePeerServer(sec, business);
			return this;
		}

		public SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> useServerPeer(Business<PacketType, SendDataType, ReceiveDataType> business) {
			coreBase.usePeerServer(business); 
			return this;
		}
		
		public SocketCore<PacketType, SendDataType, ReceiveDataType> build() {
			if (isDefaultPeer)
				Logger.logging("CoreBase", "Business Peer is default!!");
			return coreBase;
		}
		
		public SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> setPort(int port) {
			coreBase.port = port;
			return this;
		}

		public SocketCoreBuilder<PacketType, SendDataType, ReceiveDataType> setParam(String paramName, Object param) {
			coreBase.setParam(paramName, param);
			return this;
		}
	}

	@Override
	final public void run() {
		launch(port);
	}	

	@Override
	public void start() {
		super.start();
		new Thread(this).start();
	}

	public abstract LifeCycle getLifeCycle();
	public abstract void launch(int port); 
	public abstract String getServerLog();
	public abstract void setParam(String paramName, Object param);
	protected abstract void setPeer(Peer<PacketType> peer);
	public abstract void usePeerServer(SecureFactory<PacketType> sec, Business<PacketType, SendDataType, ReceiveDataType> business);
	public abstract void usePeerServer(Business<PacketType, SendDataType, ReceiveDataType> business);
}