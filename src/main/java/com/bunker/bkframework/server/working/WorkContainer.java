package com.bunker.bkframework.server.working;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.working.StaticLinkedWorking.LinkedWorkingBuilder;

/**
 * WorkContainer class was not synchronized.
 * @author 광수
 *
 */
public class WorkContainer {
	private String mName;
	private Map <String, Working> mPrivateWork = new HashMap<>();
	private Map <String, Working> mPublicWork = new HashMap<>();
	private final String _TAG = "WorkContainer";
	private Map<String, List<StaticLinkedWorking>> mInjectionMap = new HashMap<>();

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

	void addInjection(String key, StaticLinkedWorking working) {
		List<StaticLinkedWorking> list = mInjectionMap.get(key);
		if (list == null) {
			list = new LinkedList<>();
			mInjectionMap.put(key, list);
		}

		list.add(working);
	}
	
	private void addWorkCommon(String key, Working work) {
		List<StaticLinkedWorking> list = mInjectionMap.remove(key);
		if (list != null) {
			for (StaticLinkedWorking s : list) {
				s.injectionWorking(key, work);
			}
		}
	}
	
	public void addWorkPrivate(String key, Working work) {
		addWorkCommon(key, work);
		mPrivateWork.put(key, work);
		loggingWork("private", key, work);
	}
	
	public void addWork(String key, Working work) {
		addWorkCommon(key, work);
		mPublicWork.put(key, work);
		loggingWork("public", key, work);
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
		Set<Class<? extends Working>> singleClasses = reflections.getSubTypesOf(Working.class);
		for (Class<? extends Working> c : singleClasses) {
			BKWork annotation = c.getAnnotation(BKWork.class);
			
			if (annotation != null && annotation.enable()) {
				Working working = c.newInstance();
				
				addWork(annotation.key(), working, annotation.isPublic());
			}
		}
	}
	
	public void addLinkedWork(String key, JSONArray chainJSON, boolean isPublic) {
		setStaticWorking(key, chainJSON, isPublic);
	}
	
	private void addWork(String key, Working working, boolean isPublic) {
		if (isPublic) {
			addWork(key, working);
		} else {
			addWorkPrivate(key, working);
		}
	}
	
	private void setStaticWorking(String linkedKey, JSONArray paramArray, boolean isPublic) {
		LinkedWorkingBuilder linkedBuilder = makeLinkedWorkBuilder();
		for (int i = 0; i < paramArray.length(); i++) {
			JSONObject item = ((JSONObject) paramArray.get(i));
			String key = item.getString("key");
			String as = item.getString("as");
			JSONObject workingParam = null;
			
			if (item.has("param")) {
				workingParam = item.getJSONObject("param");
			}
			linkedBuilder.addWorkLink(key, as, workingParam);
		}
		StaticLinkedWorking working = linkedBuilder.build();
		addWork(linkedKey, working, isPublic);
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