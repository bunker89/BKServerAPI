package com.bunker.bkframework.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.ServerCore;
import com.bunker.bkframework.server.framework_api.ServerDefaultLog;

/**
 * 
 * Class for launch bk-framework server api.
 * 
 * {@link #This} This is singleton pattern of BKLauncher.

 * Recommend to use just call to {@link #init()} at first time and
 * othes times use {@link #getLauncher()}.
 * 
 * if you want to launch instance of ServerCore 
 * than you initializing the core through {@link #initCore(ServerCore)}
 * 
 * framework parameter is readed by file of 'setting.json'
 * @author ys89
 *
 */
public class BKLauncher {
	private static BKLauncher This;
	private JSONObject mSystemParam;
	private final String _TAG = "BKLauncher";
	
	/**
	 * init framework and load parameter
	 */
	public static void init(File paramFile) {
		File file = paramFile != null ? paramFile : new File("setting.json");
		This = new BKLauncher();
		This.mSystemParam = This.parseParamFile(file);
		This.initLoggingSystem();
	}
	
	/**
	 * return the singleton instance
	 * @return singleton instance
	 */
	public static BKLauncher getLauncher() {
		return This;
	}
	
	/**
	 * initializing server core, example NIOCore,
	 * @param core
	 */
	public void initCore(ServerCore core) {
		core.setBKLauncher(this);
	}
	
	public Object getSystemParam(String key) {
		if (mSystemParam.has(key))
			return mSystemParam.get(key);
		return null;
	}

	public void initLoggingSystem() {
		Boolean debug = (Boolean) getSystemParam("debugging");
		Boolean bkLog = (Boolean) getSystemParam("bklog_enable");
		
		if (bkLog == null)
			bkLog = true;
		
		Object timeZoneO = getSystemParam("time_zome");
		String timeZone = null;
		if (timeZoneO != null)
			timeZone = (String) timeZoneO;
		
		Object loggingTimeZoneO = getSystemParam("time_zome");
		if (loggingTimeZoneO != null)
			timeZone = (String) loggingTimeZoneO;
		
		if ((debug == null || debug == false) && bkLog) {
			Logger.mLog = new ServerDefaultLog(timeZone);
		}
	}
	
	private JSONObject parseParamFile(File file) {
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