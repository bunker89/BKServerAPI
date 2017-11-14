package com.bunker.bkframework.server.framework_api;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.PeerConnection;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.reserved.LogComposite;
import com.bunker.bkframework.server.reserved.Pair;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingFlyWeight;
import com.bunker.bkframework.server.working.WorkingResult;

public class RJSonServerBusiness implements Business<ByteBuffer>, LogComposite {
	private final String _TAG = getClass().getSimpleName();
	public static final String LOG_WORK = "work";

	private class WorkLog {
		private int mAccumTime, mAccumCount, mMaxCalTime;
	}

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

	private boolean mLogActionInited = false;
	private Map<Integer, WorkLog> mWorkLogMap;
	private final Object mLogMutex = new Object();

	@Override
	public void receive(PeerConnection connector, byte[] data, int sequence) {
		JSONObject json = new JSONObject(new String(data));
		//TODO 여기 json utf-8로 잘 들어오는지 확인

		try {
			if (mLogActionInited)
				loggingDriveJson(connector, json, sequence);
			else
				driveJson(connector, json, sequence);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.err(_TAG, "receive:UnsupportedEncoding");
		}
	}

	private void loggingDriveJson(PeerConnection connector, JSONObject json, int sequence) throws UnsupportedEncodingException {
		int work = json.getInt("working");
		long time = Calendar.getInstance().getTimeInMillis();
		driveJson(connector, json, sequence);
		time = Calendar.getInstance().getTimeInMillis() - time;
		if (!mWorkLogMap.containsKey(work)) {
			synchronized (mLogMutex) {
				if (!mWorkLogMap.containsKey(work)) {
					mWorkLogMap.put(work, new WorkLog());
				}
			}
		}

		if (time > 10000000)
			time = 10000000;

		WorkLog wLog = mWorkLogMap.get(work);

		synchronized (mLogMutex) {
			wLog.mAccumCount++;
			wLog.mAccumTime += time;
			if (wLog.mMaxCalTime < time)
				wLog.mMaxCalTime = (int) time;
		}
	}

	private void driveJson(PeerConnection connector, JSONObject json, int sequence) throws UnsupportedEncodingException {
		if (!json.has("working"))
			throw new NullPointerException("JSon has no working data");
		int work = json.getInt("working");
		Working working = WorkingFlyWeight.getWorking(work);
		if (working == null)
			throw new NullPointerException("Working is not registered");

		WorkingResult result = WorkingFlyWeight.getWorking(work).doWork(json, connector.getEnviroment());
		String jsonString = result.getResultParams().toString();
		connector.sendToPeer(jsonString.getBytes("utf-8"), sequence);
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

	@Override
	public List<Pair> logging() {
		JSONArray jsonArray = new JSONArray();
		Iterator<Entry<Integer, WorkLog>> iter = mWorkLogMap.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<Integer, WorkLog> entry = iter.next();
			WorkLog log = entry.getValue();
			int averageTime = log.mAccumTime / log.mAccumCount;
			JSONObject json = new JSONObject();
			json.put("work_num", entry.getKey());
			json.put("work_average_time", averageTime);
			json.put("work_max_time", log.mMaxCalTime);
			json.put("work_count", log.mAccumCount);
			jsonArray.put(json);
		}

		List<Pair> list = new LinkedList<>();
		Pair pair = new Pair("work", jsonArray);
		list.add(pair);
		return list;
	}

	@Override
	public void bindAction() {
		mWorkLogMap = new HashMap<>();
		mLogActionInited = true;
	}

	@Override
	public void releaseLog() {
		mLogActionInited = false;
	}

	@Override
	public void invokeTestErr() {
	}
}
