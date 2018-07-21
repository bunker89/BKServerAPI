package com.bunker.bkframework.server.working;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

@BKWork(key = WorkConstants.KEY_RENAME_WORKING,
input={"key-array"})
class KeyRenameWorking extends KeyConvertWorking {

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		JSONArray array = object.getJSONArray(WorkConstants.KEY_CONVERT_ARRAY);
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			String input = json.getString(WorkConstants.KEY_CONVERT_INPUT);
			String output = json.getString(WorkConstants.KEY_CONVERT_OUTPUT);
			if (object.has(input)) {
				object.put(output, object.remove(input));
			}
		}
		
		WorkingResult result = new WorkingResult();
		result.putReplyParam(WorkConstants.WORKING_RESULT, true);
		return result;
	}

	@Override
	public String getName() {
		return "JSONKeyConvertWorking";
	}

	@Override
	protected void convertKeys(List<String> list, JSONObject convertJSON) {
		JSONArray array = convertJSON.getJSONArray(WorkConstants.KEY_CONVERT_ARRAY);
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			String input = json.getString(WorkConstants.KEY_CONVERT_INPUT);
			String output = json.getString(WorkConstants.KEY_CONVERT_OUTPUT);
			if (list.contains(input)) {
				list.remove(input);
				list.add(output);
			}
		}
	}
}