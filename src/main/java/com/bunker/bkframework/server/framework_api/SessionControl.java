package com.bunker.bkframework.server.framework_api;

import java.util.Map;

public interface SessionControl {
	/**
	 * @return Session Id
	 */
	public void syncSessionIn(Map<String, Object> enviroment, String sessionId, long internalId);
	public void syncSessionOut(String sessionId);
	public void err(String sessionId, int state);
}