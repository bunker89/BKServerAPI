package com.bunker.bkframework.server.working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

@BKWork(key="test-static-load",
chainJSON="[{\"as\":\"t\",\"key\":\"test\"},{\"as\":\"t2\",\"param\":{\"t1\":[{\"t1-src\":\"t1-dst\"}]},\"key\":\"test2\"},{\"as\":\"t3\",\"param\":{\"t2\":[{\"t2-src\":\"t2-dst\"}]},\"key\":\"test3\"}]")
public class TestStaticLoadWorking implements Working {

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
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
