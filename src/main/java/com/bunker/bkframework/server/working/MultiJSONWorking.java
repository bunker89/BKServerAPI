package com.bunker.bkframework.server.working;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.WorkTrace;

@BKWork(key = "multi-json")
public class MultiJSONWorking extends WorkingBase {
	private final String _TAG = getClass().getSimpleName();
	private WorkContainer mWorkContainer;

	public MultiJSONWorking() {
	}

	public void setWorkContainer(WorkContainer workContainer) {
		mWorkContainer = workContainer;		
	}

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		JSONArray workingArray = object.getJSONArray(WorkConstants.MULTI_JSON_WORKING_ARRAY);
		JSONArray resultArray = doClient(workingArray, enviroment);
		if (resultArray != null) {
			result.putReplyParam(WorkConstants.WORKING_RESULT, true);
			result.putReplyParam(WorkConstants.MULTI_JSON_RESULT_ARRAY, resultArray);
		} else {
			result.putReplyParam(WorkConstants.WORKING_RESULT, false);
		}
		return result;
	}

	private JSONArray doClient(JSONArray workingArray, Map<String, Object> enviroment) {
		JSONArray resultArray = new JSONArray();
		
		JSONObject paramJSON = new JSONObject();
		for (int i = 0; i < workingArray.length(); i++) {
			JSONObject json = workingArray.getJSONObject(i);
			putAllExceptResult(paramJSON, json);
			try {
				WorkingResult result = driveJson(json, enviroment);
				resultArray.put(result.getResultParams());
				putAllExceptResult(result.getResultParams(), paramJSON);
			} catch (UnsupportedEncodingException e) {
				Logger.err(_TAG, "un support encoding", e);
				return resultArray;
			}
		}
		
		return resultArray;
	}
	
	private void putAllExceptResult(JSONObject src, JSONObject dest) {
		Iterator<String> keys = src.keys();
		while (keys.hasNext()) {
			String s = keys.next();
			if (!s.equals("result")) {
				dest.remove(s);
				dest.put(s, src.get(s));
			}
		}
	}
	
	private WorkingResult driveJson(JSONObject json, Map<String, Object> enviroment) throws UnsupportedEncodingException {
		if (!json.has("working"))
			throw new NullPointerException("json doesn't has working data");
		String work = json.getString("working");
		Working working = mWorkContainer.getPublicWork(work);
		if (working == null)
			throw new NullPointerException("Working is not registered");
		
		WorkTrace trace = new WorkTrace();
		trace.setWork(work);
		trace.setName(working.getName());
		
		WorkingResult result = mWorkContainer.getPublicWork(work).doWork(json, enviroment, trace);
		addTrace(enviroment, trace);
		return result;
	}

	private void addTrace(Map<String, Object> enviroment, WorkTrace trace) {
		@SuppressWarnings("unchecked")
		List<WorkTrace> list = (List<WorkTrace>) enviroment.get("trace_list");
		list.add(trace);
	}
}
