package com.bunker.bkframework.server.resilience;

/**
 *
 */
public interface Resilience {
	public String getResilienceName();
	public boolean recoverPart(ErrMessage msg);
	public boolean restartPart(ErrMessage msg);
	public boolean changeSafetyModule(ErrMessage msg);
	public boolean outOfSystem(ErrMessage msg);

	public ResilienceState getRecoverState();
	public void needRecover(ErrMessage msg);
	public boolean isExceed();
	public void changeResilienceState(ResilienceState state, int status);
}