package com.bunker.bkframework.server.framework_api.text;

import java.util.HashMap;
import java.util.Map;

import com.bunker.bkframework.business.PeerConnection;

public class TextConnection implements PeerConnection<String> {
	private String mResult;
	private int mSequence;
	private Map<String ,Object> mEnviroment = new HashMap<String, Object>();

	@Override
	public void closePeer() {
	}

	@Override
	public Map<String, Object> getEnviroment() {
		return mEnviroment;
	}

	@Override
	public void sendToPeer(String data, int sequence) {
		mResult = data;
		mSequence = sequence;
	}

	public String getResult() {
		return mResult;
	}

	public int getSequence() {
		return mSequence;
	}
}