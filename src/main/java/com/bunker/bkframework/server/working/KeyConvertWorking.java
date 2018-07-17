package com.bunker.bkframework.server.working;

import java.util.List;

import org.json.JSONObject;

abstract class KeyConvertWorking extends WorkingBase {
	protected abstract void convertKeys(List<String> list, JSONObject convertJSON);
}
