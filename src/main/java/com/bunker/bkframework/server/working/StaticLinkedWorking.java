package com.bunker.bkframework.server.working;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public class StaticLinkedWorking extends WorkingBase {
	private List<Working> mWorkLink = new LinkedList<>();
	private List<String> mParamRequired = new LinkedList<>();

	public StaticLinkedWorking() {
	}
	
	public void setWorkLink(List<Working> work) {
		mWorkLink = work;
		setRequired();
	}

	private void setRequired() {
		List<String> outputOccum = new LinkedList<>();
		for (Working w : mWorkLink) {
			BKWork bkWork = w.getClass().getAnnotation(BKWork.class);
			if (bkWork == null)
				continue;
			String []inputs = bkWork.input();
			for (String s : inputs) {
				if (!outputOccum.contains(s))
					mParamRequired.add(s);
			}
			
			String []outputs = bkWork.output();
			for (String s : outputs) {
				outputOccum.add(s);
			}
		}
	}
	
	public List<String> getParamRequired() {
		return mParamRequired;
	}

	@Override
	final public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		return result;
	}
	
	@Override
	public String getName() {
		return "StaticLinkedWorking";
	}
}