package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

public interface Working {
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment);
}