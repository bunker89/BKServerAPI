package com.bunker.bkframework.server.resilience;

/**
 * �ý����� ����� �� �� ����� �Ǵ� �κе�.
 * @author ����
 *
 */
public interface SystemModule {
	public void moduleForceRestart();
	public void moduleSafetyStop();
	public boolean isStoped();
	public boolean moduleStart();
}