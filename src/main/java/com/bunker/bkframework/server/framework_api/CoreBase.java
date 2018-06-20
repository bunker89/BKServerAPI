package com.bunker.bkframework.server.framework_api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Writer;

public abstract class CoreBase<PacketType> implements ServerCore<PacketType>, CoreController {
	private final String _TAG = "CoreBase";
	private JSONObject mSystemParam;

	public CoreBase() {
		mSystemParam = parseParamFile();
	}

	public void start() {
		
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
			Logger.err(_TAG, "parseParamFile:system json parse error", e);
		}
		return new JSONObject();
	}
	
	protected void initPeer(Peer<PacketType> peer, Writer<PacketType> writer, LifeCycle life) {
		peer.setLifeCycle(life);
		peer.setWriter(writer);
	}
}
