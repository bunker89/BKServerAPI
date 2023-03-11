package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

/**
 * must created by default constructor
 * @author 광수
 *
 */
@BKWork(key = "work-base", enable=false)
class WorkingBase implements Working {
	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> environment, WorkTrace trace) {
		return null;
	}

	@Override
	public String getName() {
		return "no named";
	}
}