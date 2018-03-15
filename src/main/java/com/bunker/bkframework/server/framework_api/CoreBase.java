package com.bunker.bkframework.server.framework_api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.sec.SecureFactory;

abstract public class CoreBase<PacketType, SendDataType, ReceiveDataType> implements ServerCore<PacketType>, CoreController, Runnable {
	private int port = 9011;
	private JSONObject mSystemParam;
	private String _TAG = getClass().getSimpleName();

	public CoreBase() {
		mSystemParam = parseParamFile();
	}

	public static class CoreBuilder<PacketType, SendDataType, ReceiveDataType> {
		private CoreBase<PacketType, SendDataType, ReceiveDataType> coreBase;
		private boolean isDefaultPeer = true;

		public CoreBuilder(Class<? extends CoreBase<PacketType, SendDataType, ReceiveDataType>> cl) {
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
	
		public CoreBuilder<PacketType, SendDataType, ReceiveDataType> initLoggingSystem() {
			Boolean debug = (Boolean) coreBase.getSystemParam("logging");
			if (debug != null || debug == false) {
				Logger.mLog = new ServerDefaultLog();
			}
			return this;
		}

		public CoreBuilder<PacketType, SendDataType, ReceiveDataType> peer(Peer<PacketType> peer) {
			coreBase.setPeer(peer);
			isDefaultPeer = false;
			return this;
		}

		public CoreBuilder<PacketType, SendDataType, ReceiveDataType> useServerPeer(SecureFactory<PacketType> sec, Business<PacketType, SendDataType, ReceiveDataType> business) {
			coreBase.usePeerServer(sec, business);
			return this;
		}
		
		public CoreBuilder<PacketType, SendDataType, ReceiveDataType> useServerPeer(Business<PacketType, SendDataType, ReceiveDataType> business) {
			coreBase.usePeerServer(business); 
			return this;
		}
		
		public CoreBase<PacketType, SendDataType, ReceiveDataType> build() {
			if (isDefaultPeer)
				Logger.logging("CoreBase", "Business Peer is default!!");
			return coreBase;
		}
		
		public CoreBuilder<PacketType, SendDataType, ReceiveDataType> setPort(int port) {
			coreBase.port = port;
			return this;
		}

		public CoreBuilder<PacketType, SendDataType, ReceiveDataType> setParam(String paramName, Object param) {
			coreBase.setParam(paramName, param);
			return this;
		}
	}

	@Override
	final public void run() {
		launch(port);
	}

	public Object getSystemParam(String key) {
		if (mSystemParam.has(key))
			return mSystemParam.get(key);
		return null;
	}

	private JSONObject parseParamFile() {
		File file = new File("setting.json");
		if (!file.exists()) {
			Logger.logging(_TAG, "setting.json is not found");
			return new JSONObject();
		}
		try {
			FileReader reader = new FileReader(file);
			
			JSONTokener tok = new JSONTokener(reader);
			JSONObject json = new JSONObject(tok);
			return json;
		} catch (IOException e) {
			Logger.err(_TAG, "parseParamFile:system json parse error");
		}
		return new JSONObject();
	}

	public void start() {
		new Thread(this).start();
	}

	protected abstract void setPeer(Peer<PacketType> peer);
	public abstract void usePeerServer(SecureFactory<PacketType> sec, Business<PacketType, SendDataType, ReceiveDataType> business);
	public abstract void usePeerServer(Business<PacketType, SendDataType, ReceiveDataType> business);
	public abstract String getServerLog();
	protected abstract void setParam(String paramName, Object param);
}