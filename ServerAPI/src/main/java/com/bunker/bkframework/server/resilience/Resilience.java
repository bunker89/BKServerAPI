package com.bunker.bkframework.server.resilience;

/**
 * 실패에 대한 복원가능한 모듈의 복원 인터페이스
 * @author 광수
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