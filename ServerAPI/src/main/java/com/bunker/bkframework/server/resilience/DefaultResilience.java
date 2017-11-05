package com.bunker.bkframework.server.resilience;

public abstract class DefaultResilience implements Resilience {
	private long mExceedThreashold;
	private ResilienceState mState;

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
		mState = state;
	}

	@Override
	public void needRecover(ErrMessage msg) {
		RecoverManager.getInstance().recover(this, msg);
	}

	@Override
	public ResilienceState getRecoverState() {
		return mState;
	}
}