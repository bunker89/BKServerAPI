package com.bunker.bkframework.server.working;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public class MultiWorking extends WorkingBase {

	protected void putAllExceptResult(JSONObject src, JSONObject dest) {
		Iterator<String> keys = src.keys();
		while (keys.hasNext()) {
			String s = keys.next();
			if (!s.equals(WorkConstants.WORKING_RESULT)) {
				dest.remove(s);
				dest.put(s, src.get(s));
			}
		}
	}
	
	protected WorkingResult driveWorking(Working working, String work, JSONObject json, Map<String, Object> enviroment) throws UnsupportedEncodingException {

		if (working == null)
			throw new NullPointerException("Working is not registered");
		
		WorkTrace trace = new WorkTrace();
		trace.setWork(work);
		trace.setName(working.getName());
		
		WorkingResult result = working.doWork(json, enviroment, trace);
		addTrace(enviroment, trace);
		return result;
	}

	private void addTrace(Map<String, Object> enviroment, WorkTrace trace) {
		@SuppressWarnings("unchecked")
		List<WorkTrace> list = (List<WorkTrace>) enviroment.get("trace_list");
		list.add(trace);
	}

}
