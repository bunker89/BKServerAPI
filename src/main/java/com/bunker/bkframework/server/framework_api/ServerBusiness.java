package com.bunker.bkframework.server.framework_api;

import java.io.UnsupportedEncodingException;
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
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public abstract class ServerBusiness<PacketType, SendDataType, ReceiveDataType> implements Business<PacketType, SendDataType, ReceiveDataType>, LogComposite {
	private final String _TAG = "ServerBusiness";
	private WorkContainer mWorkContainer;
	private WorkingResult mExceptionResult = new WorkingResult();

	public ServerBusiness(WorkContainer workContainer) {
		mWorkContainer = workContainer;
		mExceptionResult.putReplyParam(WorkConstants.WORKING_RESULT, false);
	}

	private class WorkLog {
		private int mAccumTime, mAccumCount, mMaxCalTime;
	}

	private boolean mLogActionInited = false;
	private Map<String, WorkLog> mWorkLogMap;
	private final Object mLogMutex = new Object();

	@Override
	public void receive(PeerConnection<SendDataType> connector, ReceiveDataType data, int sequence) {
		JSONObject json = createJSON(data);

		try {
			if (mLogActionInited)
				loggingDriveJson(connector, json, sequence);
			else
				driveJson(connector, json, sequence);
		} catch (Exception e) {
			Logger.err(_TAG, "receive err", e);
		}
	}

	protected abstract JSONObject createJSON(ReceiveDataType data);

	private void loggingDriveJson(PeerConnection<SendDataType> connector, JSONObject json, int sequence) throws UnsupportedEncodingException {
		Object workObj = json.get(WorkConstants.WORKING);
		String workKey = objKeyToString(workObj);
		long time = Calendar.getInstance().getTimeInMillis();
		driveJson(connector, json, sequence);
		time = Calendar.getInstance().getTimeInMillis() - time;
		if (!mWorkLogMap.containsKey(workKey)) {
			synchronized (mLogMutex) {
				if (!mWorkLogMap.containsKey(workKey)) {
					mWorkLogMap.put(workKey, new WorkLog());
				}
			}
		}

		if (time > 10000000)
			time = 10000000;

		WorkLog wLog = mWorkLogMap.get(workKey);

		synchronized (mLogMutex) {
			wLog.mAccumCount++;
			wLog.mAccumTime += time;
			if (wLog.mMaxCalTime < time)
				wLog.mMaxCalTime = (int) time;
		}
	}

	private void driveJson(PeerConnection<SendDataType> connection, JSONObject json, int sequence) throws UnsupportedEncodingException {
		if (!json.has(WorkConstants.WORKING))
			throw new NullPointerException("json doesn't has working data\n" + json);
		Object workObj = json.get(WorkConstants.WORKING);
		String workKey = objKeyToString(workObj);
		Working working = mWorkContainer.getPublicWork(workKey);
		if (working == null)
			throw new NullPointerException("Working is not registered" + workObj);

		WorkTrace trace = new WorkTrace();
		trace.setWork(workKey);
		trace.setName(working.getName());

		Map<String, Object> enviroment = connection.getEnviroment();

		try {
			WorkingResult result = working.doWork(json, enviroment, trace);
			addTrace(enviroment, trace);
			sendToPeer(connection, result, sequence);
		} catch (Exception e) {
			Logger.err(_TAG, "business exception\n" + json.toString(), e);
			sendToPeer(connection, mExceptionResult, sequence);
		}
	}

	private String objKeyToString(Object workObj) {
		return workObj instanceof Number ? workObj + "" : workObj.toString();
	}

	protected abstract void sendToPeer(PeerConnection<SendDataType> connection, WorkingResult result, int sequence);

	private void addTrace(Map<String, Object> enviroment, WorkTrace trace) {
		WorkTraceList list = (WorkTraceList) enviroment.get("trace_list");
		list.add(trace);
	}

	@Override
	public void removeBusinessData(PeerConnection<SendDataType> connector) {
		Session session = (Session) connector.getEnviroment().get("session");
		session.sessionBroked();
	}

	@Override
	public void established(PeerConnection<SendDataType> b) {
		b.getEnviroment().put("connection", b);
		b.getEnviroment().put("session", new Session());
		b.getEnviroment().put("trace_list", new WorkTraceList());
	}

	@Override
	public List<Pair> logging() {
		JSONArray jsonArray = new JSONArray();
		Iterator<Entry<String, WorkLog>> iter = mWorkLogMap.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<String, WorkLog> entry = iter.next();
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
