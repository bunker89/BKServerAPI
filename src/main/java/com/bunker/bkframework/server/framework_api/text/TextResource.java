package com.bunker.bkframework.server.framework_api.text;

import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Resource;

public class TextResource implements Resource<String> {

	@Override
	public void destroy() {
	}

	@Override
	public String getClientHostInfo() {
		return null;
	}

	@Override
	public Peer<String> getPeer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReadBuffer() {
		return null;
	}
}
