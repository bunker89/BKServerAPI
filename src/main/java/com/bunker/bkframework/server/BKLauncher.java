package com.bunker.bkframework.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.ServerCore;
import com.bunker.bkframework.server.framework_api.ServerDefaultLog;

public class BKLauncher {
	private JSONObject mSystemParam;
	private final String _TAG = "BKLauncher";
	
	public void init() {
		mSystemParam = parseParamFile();
		initLoggingSystem();
	}

	public Object getSystemParam(String key) {
		if (mSystemParam.has(key))
			return mSystemParam.get(key);
		return null;
	}

	public void initLoggingSystem() {
		Boolean debug = (Boolean) getSystemParam("debugging");
		if (debug == null || debug == false) {
			Logger.mLog = new ServerDefaultLog();
		}
	}
	
	private JSONObject parseParamFile() {
		File file = new File("setting.json");
		if (!file.exists()) {
			Logger.logging(_TAG , "setting.json is not found");
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
	
	public void startServer(ServerCore core) {
		core.setBKLauncher(this);
		core.start();
	}
}