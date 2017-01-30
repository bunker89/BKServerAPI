package framework_api;

import java.nio.ByteBuffer;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.PeerConnection;
import com.bunker.bkframework.working.WorkingFlyWeight;
import com.bunker.bkframework.working.WorkingResult;

public class RJSonServerBusiness implements Business<ByteBuffer> {
	/**
	 * 세션 조작의 충돌을 막기 위해 동기화를 구현한 클래스
	 * Copyright 2016~ by bunker Corp.,
	 * All rights reserved.
	 *
	 * @author Young soo Ahn <bunker.ys89@gmail.com>
	 * 2016. 11. 4.
	 *
	 *
	 */
	public class Session {
		public static final int SESSION_BROKEN = -1;
		private static final int SESSION_IN = 1;
		public static final int SESSION_WAIT = 0;

		private	int state = SESSION_WAIT;
		private final Object mutex = new Object();
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

		private void sessionBroked() {
			synchronized (mutex) {
				state = SESSION_BROKEN;
				sessionBrake(mSessionId);
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

	@Override
	public void receive(PeerConnection connector, byte[] data, int sequence) {
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(new String(data));
			System.out.println("ServerBusiness:" + json);
			int work = (int) (long) json.get("working");
			WorkingResult result = WorkingFlyWeight.getWorking(work).doWork(json, connector.getEnviroment());
			connector.sendToPeer(result.getResultParams().toString().getBytes(), sequence);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeBusinessData(PeerConnection connector) {
		Session session = (Session) connector.getEnviroment().get("session");
		session.sessionBroked();
	}

	@Override
	public void established(PeerConnection b) {
		b.getEnviroment().put("connection", b);
		b.getEnviroment().put("session", new Session());
	}

	public void sessionBrake(String sessionId) {
	}
}