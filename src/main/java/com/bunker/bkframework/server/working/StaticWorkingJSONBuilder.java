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
		
		/*
		StaticWorkingJSONBuilder staticWorkingBuilder = new StaticWorkingJSONBuilder();
		staticWorkingBuilder.insertWorking("host-detail-private", "detail", null);
		staticWorkingBuilder.insertWorking("host-detail", "coin", new WorkingParamBuilder()
				.bringParam("detail", "coin_request_array", "coin_request_array").build());
		System.out.println(staticWorkingBuilder.build());
		*/
		StaticWorkingJSONBuilder builder = new StaticWorkingJSONBuilder();
		builder.insertWorking("listing-banner-internal", "banner", null);
		builder.insertWorking("listing-event", "store", new WorkingParamBuilder()
				.bringParam("banner", "event_index_array", "event_index_array2")
				.bringParam("banner", "banner_name", "banner_name2")
				.build());
		System.out.println(builder.build());
	}
}