package com.bunker.bkframework.server.working;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.WorkTrace;

public class StaticLinkedWorking extends MultiWorking {
	private List<WorkingSet> mWorkLink = new LinkedList<>();
	private List<String> mParamRequired = new LinkedList<>();
	private static final String _TAG = "StaticLinkedWorking";
	
	private class WorkingSet {
		Working working;
		JSONObject workingParam;
		String as;
		
		WorkingSet(Working working, String as, JSONObject workingParam) {
			this.working = working;
			this.workingParam = workingParam;
			this.as = as;
		}
	}

	public static class LinkedWorkingBuilder {
		StaticLinkedWorking working = new StaticLinkedWorking();
		WorkContainer workContainer;

		public LinkedWorkingBuilder(WorkContainer workContainer) {
			this.workContainer = workContainer;
		}

		public LinkedWorkingBuilder addWorkLink(String workKey, String as) {
			addWorkLink(workKey, as, null);
			return this;
		}

		public LinkedWorkingBuilder addWorkLink(String workKey, String as, JSONObject workingParam) {
			Working work = workContainer.getWork(workKey);
			if (work == null) {
				throw new NullPointerException(_TAG + ", addLinkedWork error, key not registered" + workKey);
			}

			working.mWorkLink.add(working.new WorkingSet(work, as, workingParam));
			return this;
		}

		public StaticLinkedWorking build() {
			working.mParamRequired = working.getRequired();
			return working;
		}
	}

	public StaticLinkedWorking() {
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

	public List<String> getParamRequired() {
		return mParamRequired;
	}

	@Override
	final public WorkingResult doWork(JSONObject json, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		try {
			JSONObject resultJSON = doClient(json, enviroment);
			result.putReplyParam(WorkConstants.WORKING_RESULT, true);
		} catch (UnsupportedEncodingException e) {
			Logger.err(_TAG, "doWork errlr", e);
		}

		return result;
	}

	private JSONObject doClient(JSONObject json, Map<String, Object> enviroment) throws UnsupportedEncodingException {
		JSONObject resultJSON = new JSONObject();
		HashMap<String, JSONObject> resultMap = new HashMap<>();
		for (WorkingSet w : mWorkLink) {
			JSONObject paramJSON = new JSONObject();
			putAllExceptResult(json, paramJSON);
			Working working = w.working;
			WorkingResult result;
			if (w.workingParam != null) {
				paramJSON.put(WorkConstants.WORKING_PARAM_JSON, w.workingParam);
			}
			
			if (w.as != null) {
				paramJSON.put(WorkConstants.WORKING_RESULT_AS, w.as);
			}
			result = driveWorking(resultMap, working, "multiTest", paramJSON, enviroment);
			putAllExceptResult(result.getResultParams(), resultJSON);
		}
		return resultJSON;
	}

	@Override
	public String getName() {
		return "StaticLinkedWorking";
	}
}