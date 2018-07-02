package com.bunker.bkframework.server.working;

public abstract class WorkingSkeletone implements Working {

	@Override
	public String getName() {
		return "unknown";
	}

	@Override
	public String getDescription() {
		return "nothing";
	}
}