package com.bunker.bkframework.server.working;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.WorkTrace;

@BKWork(key = WorkConstants.MULTI_WORKING)
public class MultiJSONWorking extends MultiWorking {
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
		JSONArray workingArray = object.getJSONArray(WorkConstants.MULTI_WORKING_ARRAY);
		JSONArray resultArray = doClient(workingArray, enviroment);
		if (resultArray != null) {
			result.putReplyParam(WorkConstants.WORKING_RESULT, true);
			result.putReplyParam(WorkConstants.MULTI_WORKING_RESULT_ARRAY, resultArray);
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
				if (!json.has(WorkConstants.WORKING))
					throw new NullPointerException("json doesn't has working data");
				String work = json.getString(WorkConstants.WORKING);
				Working working = mWorkContainer.getPublicWork(work);

				WorkingResult result;
				if (working instanceof KeyConvertWorking) {
					result = driveWorking(working, "multiTest", paramJSON, enviroment);
				}
				else {
					result = driveWorking(working, "multiTest", json, enviroment);
				}
				resultArray.put(result.getResultParams());
				putAllExceptResult(result.getResultParams(), paramJSON);
			} catch (UnsupportedEncodingException e) {
				Logger.err(_TAG, "un support encoding", e);
				return resultArray;
			}
		}

		return resultArray;
	}
}