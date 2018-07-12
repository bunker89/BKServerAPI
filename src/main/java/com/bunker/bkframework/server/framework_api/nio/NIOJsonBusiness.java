package com.bunker.bkframework.server.framework_api.nio;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.json.JSONObject;

import com.bunker.bkframework.business.PeerConnection;
import com.bunker.bkframework.server.framework_api.ServerBusiness;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.WorkingResult;

public class NIOJsonBusiness extends ServerBusiness<ByteBuffer, byte[], byte[]> {

	public NIOJsonBusiness(WorkContainer workContainer) {
		super(workContainer);
	}

	@Override
	protected JSONObject createJSON(byte[] data) {
		JSONObject json = new JSONObject(new String(data));
		return json;
	}

	@Override
	protected void sendToPeer(PeerConnection<byte[]> connection, WorkingResult result, int sequence) {
		try {
			connection.sendToPeer(result.getResultParams().toString().getBytes("utf-8"), sequence);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
