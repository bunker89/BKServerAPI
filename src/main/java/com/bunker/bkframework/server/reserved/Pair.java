package com.bunker.bkframework.server.reserved;import org.json.JSONArray;
import org.json.JSONObject;

public class Pair {
	private String mKey;
	private Object mValue;
	
	public Pair(String key, Object value) {
		mKey = key;
		mValue = value;
	}

	public String getKey() {
		return mKey;
	}

	public String getValue() {
		return mValue.toString();
	}

	public String getDatabaseValue() {
		if (mValue instanceof String)
			return "'" + mValue + "'";
		if (mValue instanceof JSONObject || mValue instanceof JSONArray) {
			String str = mValue.toString();
			str.replaceAll("'", "\'\'");
			return str;
		}
		return getValue();
	}
}