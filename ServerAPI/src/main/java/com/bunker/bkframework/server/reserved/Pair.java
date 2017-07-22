package com.bunker.bkframework.server.reserved;import java.util.Collection;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

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
		if (mValue instanceof JSONAware || mValue instanceof JSONStreamAware) {
			String str = mValue.toString();
			str.replaceAll("'", "\'\'");
			return str;
		}
		return getValue();
	}
}