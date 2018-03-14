package com.bunker.bkframework.server.framework_api;

import java.io.Serializable;
import java.util.Map;

/**
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 11. 4.
 *
 *
 */
public class Session implements Serializable {
	private static final long serialVersionUID = 1265158012165193745L;
	public static final int SESSION_BROKEN = -1;
	private static final int SESSION_IN = 1;
	public static final int SESSION_WAIT = 0;

	private	int state = SESSION_WAIT;
	private transient final Object mutex = new Object();
	private String mSessionId = null;
	private long mInternalId = -1;

	public void sessoinIn(Map<String, Object> enviroment, SessionControl control, String sessionId, long internalId) {
		synchronized (mutex) {
			if (state == SESSION_WAIT) {
				control.syncSessionIn(enviroment, sessionId, internalId);
				state = SESSION_IN;
				mSessionId = sessionId;
				mInternalId = internalId;
			} else
				control.err(sessionId, state);
		}
	}

	public void sessionOut(SessionControl control) {
		synchronized (mutex) {
			control.syncSessionOut(mSessionId);
			state = SESSION_WAIT;
			mSessionId = null;
		}
	}

	public void sessionBroked() {
		synchronized (mutex) {
			state = SESSION_BROKEN;
			mSessionId = null;
		}
	}

	public String getSessionId() {
		return mSessionId;
	}

	public long getInternalId() {
		return mInternalId;
	}
}

