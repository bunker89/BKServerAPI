package com.bunker.bkframework.server.working;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkingParamBuilder {
	private JSONObject mJSON = new JSONObject();
	
	public WorkingParamBuilder bringParam(String fromAs, String srcKey, String destKey) {
		if (!mJSON.has(fromAs)) {
			mJSON.put(fromAs, new JSONObject());
		}
		
		JSONObject json = mJSON.getJSONObject(fromAs);
		json.put(srcKey, destKey);
		return this;
	}
	
	public JSONObject build() {
		return mJSON;
	}
	
	public static void main(String []args) {
		System.out.println(new WorkingParamBuilder()
				.bringParam("t1", "test1-src1", "test1-src1")
				.bringParam("t1", "test1-src2", "test1-src2")
				.bringParam("t1", "test1-src3", "test1-src3")
				.build());
	}
}
