package com.bunker.bkframework.server.working;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.WorkTrace;

public class MultiWorking extends WorkingBase {
	private final String _TAG = "MultiWorking";
	
	protected void putAllExceptResult(WorkingResult result, JSONObject dest) {
		putAllExceptResult(result.getResultParams(), dest);
	}
	
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
	
	protected WorkingResult driveWorking(Map<String, JSONObject> resultMap, Working working, String work, JSONObject json, Map<String, Object> enviroment) throws UnsupportedEncodingException {
		if (working == null)
			throw new NullPointerException("Working is not registered");
		
		WorkTrace trace = new WorkTrace();
		trace.setWork(work);
		trace.setName(working.getName());
		
		bringWorkParam(resultMap, json);
		WorkingResult result = working.doWork(json, enviroment, trace);
		putResultAs(resultMap, json, result);
		addTrace(enviroment, trace);
		return result;
	}
	
	private void putResultAs(Map<String, JSONObject> resultMap, JSONObject json, WorkingResult result) {
		if (json.has(WorkConstants.WORKING_RESULT_AS)) {
			String resultAs = json.getString(WorkConstants.WORKING_RESULT_AS);
			JSONObject resultAsJSON = new JSONObject();
			resultAsJSON.put("private", result.getPrivateParams());
			resultAsJSON.put("result", result.getResultParams());
			resultMap.put(resultAs, resultAsJSON);
		}
	}
	
	private void bringWorkParam(Map<String, JSONObject> resultMap, JSONObject json) {
		if (!json.has(WorkConstants.WORKING_PARAM_JSON)) {
			return;
		}
		
		JSONObject workParam = json.getJSONObject(WorkConstants.WORKING_PARAM_JSON);
		
		Iterator<String> resultAses = workParam.keys();
		//iterate to result keys
		while (resultAses.hasNext()) {
			String resultAs = resultAses.next();
			JSONArray paramArray = workParam.getJSONArray(resultAs);
			
			for (int i = 0; i < paramArray.length(); i++) {
				JSONObject j =  paramArray.getJSONObject(i);
				//iterate to param names
				iterateParamJSON(resultMap, resultAs, j, json);
			}
		}
	}
	
	private void iterateParamJSON(Map<String, JSONObject> resultMap, String resultAs, JSONObject paramJSON, JSONObject dest) {
		Iterator<String> keys = paramJSON.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			try {
				JSONObject asResult = resultMap.get(resultAs);
				JSONObject privateParam = null;
				if (asResult.has("private"))
					privateParam = asResult.getJSONObject("private");
				JSONObject resultParam = null;
				if (asResult.has("result"))
					resultParam = asResult.getJSONObject("result");
				
				if (privateParam != null && privateParam.has(key))
					dest.put(paramJSON.getString(key), privateParam.get(key));
				else if (resultParam != null && resultParam.has(key))
					dest.put(paramJSON.getString(key), resultParam.get(key));
			} catch(Exception e) {
				Logger.err(_TAG, "param missmatched\n"
						+ "[result map:" + resultMap + "]\n"
								+ "result as:" + resultAs + "\n"
								+ "key:" + key, e);
			}
		}
	}
	
	private void addTrace(Map<String, Object> enviroment, WorkTrace trace) {
		@SuppressWarnings("unchecked")
		List<WorkTrace> list = (List<WorkTrace>) enviroment.get("trace_list");
		list.add(trace);
	}
}