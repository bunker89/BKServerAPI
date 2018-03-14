package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public interface Working {
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace);
	public String getName();
	public String getDescription();
}