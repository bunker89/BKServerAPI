package com.bunker.bkframework.server.resilience;

public interface ResilienceState {
	public void recorver(Resilience resilience, ErrMessage msg);
}
