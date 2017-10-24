package com.bunker.bkframework.server.resilience;

public abstract class DefaultResilience implements Resilience {
	private long mExceedThreashold;

	@Override
	public boolean recoverPart(ErrMessage msg) {
		return false;
	}

	@Override
	public boolean restartPart(ErrMessage msg) {
		return false;
	}

	@Override
	public boolean changeSafetyModule(ErrMessage msg) {
		return false;
	}

	@Override
	public boolean outOfSystem(ErrMessage msg) {
		return false;
	}

	@Override
	public boolean isExceed() {
		return false;
	}

	@Override
	public void changeResilienceState(ResilienceState state, int status) {
		
	}
}
