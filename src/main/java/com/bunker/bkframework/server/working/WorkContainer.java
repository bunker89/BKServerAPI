package com.bunker.bkframework.server.working;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.working.StaticLinkedWorking.LinkedWorkingBuilder;

public class WorkContainer {
	private String mName;
	private Map <String, Working> mPrivateWork = new HashMap<>();
	private Map <String, Working> mPublicWork = new HashMap<>();
	private final String _TAG = "WorkContainer";

	public WorkContainer() {
		this("unknown");
	}

	public WorkContainer(String name) {
		mName = name;
		DynamicLinkedWorking multiWork = new DynamicLinkedWorking();
		multiWork.setWorkContainer(this);
		try {
			loadWorkings("com.bunker.bkframework.server.working");
		} catch (InstantiationException | IllegalAccessException e) {
			Logger.err(_TAG, "framework working error", e);
		}
		addWork(WorkConstants.MULTI_WORKING, multiWork);
	}
	
	public void addWorkPrivate(String key, Working work) {
		mPrivateWork.put(key, work);
		loggingWork("private", key, work);
	}

	public void addWork(String key, Working work) {
		mPublicWork.put(key, work);
		loggingWork("public", key, work);
	}
	
	public void addLinkedWorkPublic(String key, String[] workKeys) {
		StaticLinkedWorking linkedWorking = makeLinkedWork(mPublicWork, key, workKeys);
		addWork(key, linkedWorking);
	}
	
	public void addLinkedWorkPrivate(String key, String[] workKeys) {
		StaticLinkedWorking linkedWorking = makeLinkedWork(mPublicWork, key, workKeys);
		addWorkPrivate(key, linkedWorking);
	}
	
	public StaticLinkedWorking makeLinkedWork(Map<String, Working> workMap, String key, String[]workKeys) {
		LinkedWorkingBuilder workBuilder = new LinkedWorkingBuilder(this);
		
		for (String s: workKeys) {
			workBuilder.addWorkLink(s, null);
		}
		StaticLinkedWorking linkedWorking = workBuilder.build();
		return linkedWorking;
	}
	
	public LinkedWorkingBuilder makeLinkedWorkBuilder() {
		return new LinkedWorkingBuilder(this);
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
		loadWorkings(new Reflections(packageName));
	}
	
	public void loadWorkings(Reflections reflections) throws InstantiationException, IllegalAccessException {
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