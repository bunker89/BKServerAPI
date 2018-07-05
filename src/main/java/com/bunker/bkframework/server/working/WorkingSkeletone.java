package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

@BKWork(key = "workSkeleton")
public class WorkingSkeletone implements Working {

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
