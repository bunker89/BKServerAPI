package com.bunker.bkframework.server.working;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.WorkTrace;

public final class StaticLinkedWorking extends MultiWorking {	
	private List<WorkingSet> mWorkLink = new LinkedList<>();
//	private List<String> mParamRequired = new LinkedList<>();
	private static final String _TAG = "StaticLinkedWorking";
	
	private class WorkingSet {
		Working working;
		JSONObject workingParam;
		JSONObject staticParam;
		String as;
		String key;
		
		WorkingSet(String key, Working working, String as, JSONObject workingParam, JSONObject staticParam) {
			this.working = working;
			this.workingParam = workingParam;
			this.staticParam = staticParam;
			this.as = as;
			this.key = key;
		}
	}
	
	@Jsonparam public void setParam(JSONObject json) {
		WorkContainer container = (WorkContainer) json.get("work-container");
		JSONArray linkParams = json.getJSONArray("link-data");
		
		for (int i = 0; i < linkParams.length(); i++) {
			JSONObject workData = linkParams.getJSONObject(i);
			String subWork = workData.getString("key");
			String as = workData.getString("as");
			JSONObject param = null;
			if (workData.has("param"))
				param = workData.getJSONObject("param");
			JSONObject staticParam = null;
			if (workData.has("static"))
				staticParam = workData.getJSONObject("static");
			addWorkLink(container, subWork, as, param, staticParam);
		}
	}

	void checkTriangle(String key) {
		for (WorkingSet set : mWorkLink) {
			if (set.working instanceof StaticLinkedWorking) {
				((StaticLinkedWorking) set.working).checkTriangle(key);
			}
			
			if (key.equals(set.key)) {
				throw new RuntimeException("StaticLinkedWorking has triangle");
			}
		}
	}
	
	public void addWorkLink(WorkContainer container, String workKey, String as, JSONObject workingParam, JSONObject staticParam) {
		Working work = container.getWork(workKey);
		if (work instanceof StaticLinkedWorking) {
			StaticLinkedWorking staticLinked = (StaticLinkedWorking) work;
			staticLinked.checkTriangle(workKey);
		}
		if (work == null) {
			work = new DynamicFlagWorking(workKey);
			container.addInjection(workKey, this);
		}
		
		mWorkLink.add(new WorkingSet(workKey, work, as, workingParam, staticParam));
	}

	@Deprecated
	public void iteratePringWorkings() {
		for (WorkingSet w : mWorkLink) {
			System.out.println(w.working);
		}
	}
	
	void injectionWorking(String key, Working working) {
		if (working instanceof StaticLinkedWorking) {
			((StaticLinkedWorking) working).checkTriangle(key);
		}
		for (WorkingSet w : mWorkLink) {
			if (w.key.equals(key)) {
				w.working = working;
			}
		}
	}
	
	protected List<String> getRequired() {
		List<String> paramRequired = new LinkedList<>();
		List<String> outputOccum = new LinkedList<>();
		for (WorkingSet w : mWorkLink) {
			BKWork bkWork = w.working.getClass().getAnnotation(BKWork.class);
			if (bkWork == null)
				continue;
			String []inputs = bkWork.input();
			for (String s : inputs) {
				if (!outputOccum.contains(s))
					paramRequired.add(s);
			}

			String []outputs = bkWork.output();
			for (String s : outputs) {
				outputOccum.add(s);
			}
		}
		return paramRequired;
	}
		
	@Override
	final public WorkingResult doWork(JSONObject json, Map<String, Object> environment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		try {
			JSONObject resultJSON = doClient(json, environment);
			result.putAllParam(resultJSON);
		} catch (UnsupportedEncodingException e) {
			Logger.err(_TAG, "doWork errlr", e);
		}

		return result;
	}
	
	private JSONObject doClient(JSONObject json, Map<String, Object> environment) throws UnsupportedEncodingException {
		JSONObject resultJSON = new JSONObject();
		resultJSON.put(WorkConstants.WORKING_RESULT, true);
		HashMap<String, JSONObject> resultMap = new HashMap<>();
		for (WorkingSet w : mWorkLink) {
			JSONObject paramJSON = new JSONObject();
			putAllExceptResult(json, paramJSON);
			Working working = w.working;
			WorkingResult result;
			if (w.workingParam != null) {
				paramJSON.put(WorkConstants.WORKING_PARAM_JSON, w.workingParam);
			}
			if (w.staticParam != null) {
				Iterator<String> staticKeys = w.staticParam.keys();
				//iterate to result keys
				while (staticKeys.hasNext()) {
					String staticKey = staticKeys.next();
					paramJSON.put(staticKey, w.staticParam.get(staticKey));
				}
			}
			
			if (w.as != null) {
				paramJSON.put(WorkConstants.WORKING_RESULT_AS, w.as);
			}
			result = driveWorking(resultMap, working, "multiTest", paramJSON, environment);
			putAllExceptResult(result, resultJSON);
			if ((boolean) result.getParam(WorkConstants.WORKING_RESULT) == false) {
				resultJSON.remove(WorkConstants.WORKING_RESULT);
				resultJSON.put(WorkConstants.WORKING_RESULT, false);
				return resultJSON;
			}
		}
		return resultJSON;
	}

	@Override
	public String getName() {
		return "StaticLinkedWorking";
	}
}