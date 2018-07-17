package com.bunker.bkframework.server.working;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;

public class StaticLinkedWorking extends MultiWorking {
	private List<WorkingSet> mWorkLink = new LinkedList<>();
	private List<String> mParamRequired = new LinkedList<>();
	private static final String _TAG = "StaticLinkedWorking";
	private class WorkingSet {
		Working working;
		JSONObject convert;
		WorkingSet(Working working, JSONObject convert) {
			this.working = working;
			this.convert = convert;
		}
	}
	
	public static class LinkedWorkingBuilder {
		StaticLinkedWorking working = new StaticLinkedWorking();
		WorkContainer workContainer;
		
		public LinkedWorkingBuilder(WorkContainer workContainer) {
			this.workContainer = workContainer;
		}
		
		public LinkedWorkingBuilder addWorkLink(String workKey) {
			this.addWorkLink(workKey, null);
			return this;
		}

		public LinkedWorkingBuilder addWorkLink(String workKey, @Nullable JSONObject convert) {
			Working work = workContainer.getWork(workKey);
			if (work == null) {
				throw new NullPointerException(_TAG + ", addLinkedWork error, key not registered" + workKey);
			}

			working.mWorkLink.add(working.new WorkingSet(work, convert));
			return this;
		}
		
		public StaticLinkedWorking build() {
			working.mParamRequired = working.getRequired();
			return working;
		}
	}

	public StaticLinkedWorking() {
	}

	protected List<String> getRequired() {
		List<String> paramRequired = new LinkedList<>();
		List<String> outputOccum = new LinkedList<>();
		for (WorkingSet w : mWorkLink) {
			BKWork bkWork = w.working.getClass().getAnnotation(BKWork.class);
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
			
			if (w.working instanceof KeyConvertWorking) {
				if (w.convert != null) {
					((KeyConvertWorking) w.working).convertKeys(outputOccum, w.convert);
				}
			}
		}
		return paramRequired;
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