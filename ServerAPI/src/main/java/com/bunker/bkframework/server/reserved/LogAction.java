package com.bunker.bkframework.server.reserved;

import java.util.List;

public interface LogAction {
	public void bindAction();
	public List<Pair> act();
}