package com.bunker.bkframework.server.framework_api;

import org.json.JSONObject;

public class WorkTrace {
	private int mWork;
	private String mWorkName;
	private JSONObject mJson;

	public void setWorkNumber(int work) {
		mWork = work;
	}

	public void setName(String name) {
		mWorkName=  name;
	}

	public void putTraceData(String key, Object data) {
		if (mJson == null) {
			mJson = new JSONObject();
		}
		mJson.put(key, data);
	}
}