package com.bunker.bkframework.server.framework_api;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingFlyWeight;
import com.bunker.bkframework.server.working.WorkingResult;
import com.bunker.bkframework.server.working.WorkingSkeletone;

public class MultiJSONWorking extends WorkingSkeletone {
	private final String _TAG = getClass().getSimpleName();

	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		JSONArray workingArray = object.getJSONArray("working_array");
		JSONArray resultArray = doClient(workingArray, enviroment);
		if (resultArray != null) {
			result.putReplyParam("result", true);
			result.putReplyParam("result_array", resultArray);
		} else {
			result.putReplyParam("result", false);
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
				dest.put(s, src.get(s));
			}
		}
	}
	
	private WorkingResult driveJson(JSONObject json, Map<String, Object> enviroment) throws UnsupportedEncodingException {
		if (!json.has("working"))
			throw new NullPointerException("json doesn't has working data");
		Object work = json.get("working");
		Working working = WorkingFlyWeight.getWorking(work);
		if (working == null)
			throw new NullPointerException("Working is not registered");
		
		WorkTrace trace = new WorkTrace();
		trace.setWork(work);
		trace.setName(working.getName());
		
		WorkingResult result = WorkingFlyWeight.getWorking(work).doWork(json, enviroment, trace);
		addTrace(enviroment, trace);
		return result;
	}

	private void addTrace(Map<String, Object> enviroment, WorkTrace trace) {
		@SuppressWarnings("unchecked")
		List<WorkTrace> list = (List<WorkTrace>) enviroment.get("trace_list");
		list.add(trace);
	}
}
