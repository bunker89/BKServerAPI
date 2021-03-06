package com.bunker.bkframework.server.working;

import java.util.Iterator;

import org.json.JSONObject;

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
	private JSONObject privateParam;
	public byte[] byteParam;
		
	public void putReplyParam(String key, Object data) {
		if (param == null)
			param = new JSONObject();
		param.put(key, data);
	}
	
	public void putAllParam(JSONObject param) {
		Iterator<String> keys = param.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			putReplyParam(key, param.get(key));
		}
	}
	
	public void putPrivateParam(String key, Object data) {
		if (privateParam == null) {
			privateParam = new JSONObject();
		}
		privateParam.put(key, data);
	}
	
	public Object getParam(String key) {
		return param.get(key);
	}

	public JSONObject getResultParams() {
		return param;
	}
	
	public JSONObject getPrivateParams() {
		return privateParam;
	}
}