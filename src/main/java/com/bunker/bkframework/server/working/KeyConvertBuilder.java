package com.bunker.bkframework.server.working;

import org.json.JSONArray;
import org.json.JSONObject;

public class KeyConvertBuilder {
	private JSONArray mArray = new JSONArray();

	public KeyConvertBuilder putConvert(String origin, String output) {
		JSONObject json = new JSONObject();
		json.put(WorkConstants.KEY_CONVERT_INPUT, origin);
		json.put(WorkConstants.KEY_CONVERT_OUTPUT, output);
		mArray.put(json);
		return this;
	}

	public JSONObject build() {
		JSONObject json = new JSONObject();
		json.put(WorkConstants.KEY_CONVERT_ARRAY, mArray);
		return json;
	}
}