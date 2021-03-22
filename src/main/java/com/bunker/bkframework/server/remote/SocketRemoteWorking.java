package com.bunker.bkframework.server.remote;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class SocketRemoteWorking implements Working {
	private String addr;
	private int port;
	
	public SocketRemoteWorking(String addr, int port) {
		this.addr = addr;
		this.port = port;
	}

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		return result;
	}

	@Override
	public String getName() {
		return null;
	}
}