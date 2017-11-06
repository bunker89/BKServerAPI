package com.bunker.bkframework.server.resilience;

/**
 * 시스템이 재시작 할 때 재시작 되는 부분들.
 * @author 광수
 *
 */
public interface SystemModule {
	public void moduleForceRestart();
	public void moduleSafetyStop();
	public boolean isStoped();
	public boolean moduleStart();
}