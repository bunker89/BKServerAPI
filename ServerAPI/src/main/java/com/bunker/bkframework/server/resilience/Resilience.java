package com.bunker.bkframework.server.resilience;

/**
 * ���п� ���� ���������� ����� ���� �������̽�
 * @author ����
 *
 */
public interface Resilience {
	public String getResilienceName();
	public boolean recoverPart(ErrMessage msg);
	public boolean restartPart(ErrMessage msg);
	public boolean changeSafetyModule(ErrMessage msg);
	public boolean outOfSystem(ErrMessage msg);

	public void needRecover(ErrMessage msg);
	public void changeResilienceState(ResilienceState state, int status);
}