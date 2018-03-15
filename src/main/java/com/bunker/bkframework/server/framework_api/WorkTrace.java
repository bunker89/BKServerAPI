package com.bunker.bkframework.server.framework_api;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

public class WorkTrace {
	private int mIndex = 0;
	private int mWork;
	private String mWorkName;
	private JSONObject mJson;

	public void setWorkNumber(int work) {
		mWork = work;
	}
	
	public void setIndex(int index) {
		mIndex = index;
	}

	public void setName(String name) {
		mWorkName=  name;
	}

	public void putTraceData(String key, Object data) {
		if (mJson == null) {
			mJson = new JSONObject();
		}
		mJson.put(key, data);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("index:" + mIndex + "\n");
		builder.append("worktrace:" + "\n");
		builder.append("work key:" + mWork + "\n");
		builder.append("work name:" + mWorkName + "\n");
		if (mJson == null) {
			mJson = new JSONObject();
		}
		builder.append("trace data:" + mJson.toString() + "\n");
		return builder.toString();
	}

	public static void main(String []args) {
		WorkTraceList traces = new WorkTraceList();
		traces.add(new WorkTrace());
		traces.add(new WorkTrace());
		System.out.println(traces.toString());
	}
}