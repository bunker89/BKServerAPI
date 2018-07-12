package com.bunker.bkframework.server.working;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public class StaticLinkedWorking extends WorkingBase {
	private List<Working> mWorkLink = new LinkedList<>();
	private boolean mLinkError;
	private int mErrorCode;
	private List<String> paramRequired = new LinkedList<>();

	public StaticLinkedWorking() {
		mLinkError = setRequired();
	}
	
	public void addWorkLink(Working work) {
		mWorkLink.add(work);
	}

	private boolean setRequired() {
		List<String> outputOccum = new LinkedList<>();
		for (Working w : mWorkLink) {
			BKWork bkWork = w.getClass().getAnnotation(BKWork.class);
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
		System.out.println(paramRequired);
		return true;
	}

	@Override
	final public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		return result;
	}
}