package com.bunker.bkframework.server.framework_api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.sec.SecureFactory;

abstract public class CoreBase<PacketType> implements ServerCore<PacketType>, CoreController, Runnable {
	private int port = 9011;
	private JSONObject mSystemParam;
	private String _TAG = getClass().getSimpleName();

	public CoreBase() {
		mSystemParam = parseParamFile();
	}

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

		public CoreBuilder<PacketType> setParam(String paramName, Object param) {
			coreBase.setParam(paramName, param);
			return this;
		}
	}

	@Override
	final public void run() {
		launch(port);
	}

	public Object getSystemParam(String key) {
		return mSystemParam.get(key);
	}

	private JSONObject parseParamFile() {
		File file = new File("setting.json");
		if (!file.exists())
			return new JSONObject();
		try {
			FileReader reader = new FileReader(file);
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			return json;
		} catch (IOException | ParseException e) {
			Logger.err(_TAG, "parseParamFile:system json parse error");
		}
		return new JSONObject();
	}

	public void start() {
		new Thread(this).start();
	}

	protected abstract void setPeer(Peer<PacketType> peer);
	public abstract void usePeerServer(SecureFactory<PacketType> sec, Business<PacketType> business);
	public abstract void usePeerServer(Business<PacketType> business);
	public abstract String getServerLog();
	protected abstract void setParam(String paramName, Object param);
}