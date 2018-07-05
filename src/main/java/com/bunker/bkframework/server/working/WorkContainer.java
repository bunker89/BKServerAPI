package com.bunker.bkframework.server.working;

import java.util.HashMap;
import java.util.Map;

public class WorkContainer {
	private Map <Object, Working> mPrivateWork = new HashMap<>();
	private Map <Object, Working> mPublicWork = new HashMap<>();
	
	public void addWorkPrivate(Object key, Working work) {
		mPrivateWork.put(key, work);
	}
	
	public void addWork(Object key, Working work) {
			mPublicWork.put(key, work);
	}

	public Working getPublicWork(Object key) {
		return mPublicWork.get(key);
	}
	
	public Working getWork(Object key) {
		Working work = mPublicWork.get(key);
		if (work != null)
			return work;
		return mPrivateWork.get(key);
	}
}