package com.bunker.bkframework.server.working;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.bunker.bkframework.newframework.Logger;

public class WorkContainer {
	private String mName;
	private Map <String, Working> mPrivateWork = new HashMap<>();
	private Map <String, Working> mPublicWork = new HashMap<>();
	private final String _TAG = "WorkContainer";

	public WorkContainer() {
		mName = "unknown";
	}

	public WorkContainer(String name) {
		mName = name;
	}

	public void addWorkPrivate(String key, Working work) {
		mPrivateWork.put(key, work);
		loggingWork("private", key, work);
	}

	public void addWork(String key, Working work) {
		mPublicWork.put(key, work);
		loggingWork("public", key, work);
	}
	
	public void addLinkedWork(String key, String[]workKeys) {
		StaticLinkedWorking linkedWorking = new StaticLinkedWorking();
		List<Working> workings = new LinkedList<>();
		for (String k : workKeys) {
			Working work = getWork(k);
			if (work == null) {
				throw new NullPointerException(_TAG + ", addLinkedWork error, key not registered" + k);
			}
			workings.add(work);
		}
		linkedWorking.setWorkLink(workings);
		addWork(key, linkedWorking);
		Logger.logging(_TAG, "[" + key + "] needs " + linkedWorking.getParamRequired());
	}

	private void loggingWork(String range, String key, Working work) {
		Logger.logging(_TAG, "[" + mName + "]" + " registered " + range + " key " + "[" + key + "],named " + work.getName() + "");
	}

	public void loadWorkings(String []packageNames) throws InstantiationException, IllegalAccessException {
		for (String p : packageNames) {
			loadWorkings(p);
		}
	}

	public void loadWorkings(String packageName) throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends Working>> classes = reflections.getSubTypesOf(Working.class);
		for (Class<? extends Working> c : classes) {
			BKWork annotation = c.getAnnotation(BKWork.class);
			if (annotation != null && annotation.enable()) {
				if (annotation.isPublic()) {
					addWork(annotation.key(), c.newInstance());
				} else {
					addWorkPrivate(annotation.key(), c.newInstance());
				}
			}
		}
	}

	public Working getPublicWork(String key) {
		return mPublicWork.get(key);
	}

	public Working getWork(String key) {
		Working work = mPublicWork.get(key);
		if (work != null)
			return work;
		return mPrivateWork.get(key);
	}

	public List<String> getRegisteredKeys(boolean isPublic) {
		List<String> list = new ArrayList<>(mPublicWork.size() + mPrivateWork.size());

		if (!isPublic) {
			Iterator<String> keys = mPrivateWork.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next(); 
				list.add(key);
			}
		}

		Iterator<String> keys = mPublicWork.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next(); 
			list.add(key);
		}
		return list;
	}
}