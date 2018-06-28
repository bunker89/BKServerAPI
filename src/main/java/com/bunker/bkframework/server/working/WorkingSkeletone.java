package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public abstract class WorkingSkeletone implements Working {

	@Override
	public String getName() {
		return "unknown";
	}

	@Override
	public String getDescription() {
		return "nothing";
	}
}
