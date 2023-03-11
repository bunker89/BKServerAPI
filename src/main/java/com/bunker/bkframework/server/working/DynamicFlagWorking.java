package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public class DynamicFlagWorking implements Working {
	private final String mKey;
	
	public DynamicFlagWorking(String key) {
		mKey = key;
	}
	
	public String getWorkKey() {
		return mKey;
	}

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> environment, WorkTrace trace) {
		throw new RuntimeException(mKey + " is not registerd, by DynamicFlagWorking (default class)");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
