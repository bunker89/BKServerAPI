package com.bunker.bkframework.working;

import java.util.Map;

import org.json.simple.JSONObject;

public class WorkingResult {
	public static final int RESULT_FAIL = 0b1;
	private static final int RESULT_SUCCESS = 0b0;
	public static final int RESULT_IGNORE = 0b10;
	private static final int RESULT_REPLAY = 0b00;
	
	public String dump = null;
	private int resultFlag;
	public Object resultObj;
	public int resultInt;
	public int request;
	
	/**
	 * use to putReplyParam instead of param
	 */
	@Deprecated
	public JSONObject param;
	public byte[] byteParam;
		
	public void putReplyParam(Object key, Object data) {
		if (param == null)
			param = new JSONObject();
		param.put(key, data);
	}
	
	public void putAll(Map json) {
		if (param == null)
			param = new JSONObject();
		param.putAll(json);
	}
	
	public Object getParam(Object key) {
		return param.get(key);
	}

	public JSONObject getResultParams() {
		return param;
	}
}
