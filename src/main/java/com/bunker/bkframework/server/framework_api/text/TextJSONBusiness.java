package com.bunker.bkframework.server.framework_api.text;

import org.json.JSONObject;

import com.bunker.bkframework.business.PeerConnection;
import com.bunker.bkframework.server.framework_api.ServerBusiness;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.WorkingResult;

public class TextJSONBusiness extends ServerBusiness<String, String, String> {

	public TextJSONBusiness(WorkContainer workContainer) {
		super(workContainer);
	}

	@Override
	protected JSONObject createJSON(String data) {
		JSONObject json = new JSONObject(data);
		return json;
	}

	@Override
	protected void sendToPeer(PeerConnection<String> connection, WorkingResult result, int sequence) {
		connection.sendToPeer(result.getResultParams().toString(), sequence);
	}
}