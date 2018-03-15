package com.bunker.bkframework.server.framework_api;

import java.util.LinkedList;

public class WorkTraceList extends LinkedList<WorkTrace> {
	private int mIndex = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3003016330152348091L;

	@Override
	public boolean add(WorkTrace e) {
		e.setIndex(mIndex++);
		return super.add(e);
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n";
	}
}