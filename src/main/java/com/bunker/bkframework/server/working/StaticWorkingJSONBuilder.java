package com.bunker.bkframework.server.working;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class StaticWorkingJSONBuilder {
	private List<JSONObject> mWorks = new LinkedList<>();
	
	public StaticWorkingJSONBuilder insertWorking(String key, String as, JSONObject workingParam) {
		JSONObject json = new JSONObject();
		json.put("key", key);
		json.put("as", as);
		if (workingParam != null)
			json.put("param", workingParam);
		mWorks.add(json);
		return this;
	}
	
	public JSONArray build() {
		JSONArray array = new JSONArray();
		
		for (JSONObject j : mWorks) {
			array.put(j);
		}
		return array;
	}
	
	public static void main(String []args) {
		StaticWorkingJSONBuilder staticWorkingBuilder = new StaticWorkingJSONBuilder();
		staticWorkingBuilder.insertWorking("test", "t", null);
		staticWorkingBuilder.insertWorking("test2", "t2", new WorkingParamBuilder().bringParam("t1", "t1-src", "t1-dst").build());
		staticWorkingBuilder.insertWorking("test3", "t3", new WorkingParamBuilder().bringParam("t2", "t2-src", "t2-dst").build());
		System.out.println(staticWorkingBuilder.build());
	}
}