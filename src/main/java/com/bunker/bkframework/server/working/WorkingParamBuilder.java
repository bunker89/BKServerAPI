package com.bunker.bkframework.server.working;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkingParamBuilder {
	private JSONObject mJSON = new JSONObject();
	
	public WorkingParamBuilder bringParam(String fromAs, String srcKey, String destKey) {
		if (!mJSON.has(fromAs)) {
			mJSON.put(fromAs, new JSONArray());
		}
		
		JSONObject json = new JSONObject();
		json.put(srcKey,  destKey);
		JSONArray array = mJSON.getJSONArray(fromAs);
		array.put(json);
		return this;
	}
	
	public JSONObject build() {
		return mJSON;
	}
}
