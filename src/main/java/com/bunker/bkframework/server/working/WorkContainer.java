package com.bunker.bkframework.server.working;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.reflections.Reflections;

import com.bunker.bkframework.newframework.Logger;

/**
 * WorkContainer class was not synchronized.
 * @author 광수
 *
 */
public class WorkContainer {
	private String mName;
	private String jsonFolder;
	private Map<String, Class<? extends Working>> formWork = new HashMap<>();
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
		addWork(WorkConstants.DYNAMIC_LINKED_WORKING, multiWork);
		addformWork(WorkConstants.STATIC_LINKED_FORM, StaticLinkedWorking.class);
	}
	
	public void addformWork(String formName, Class<? extends Working> cl) {
		formWork.put(formName, cl);
	}
	
	public void setJSONParam(String jsonFolder) {
		this.jsonFolder = jsonFolder;
		
		File root = new File(jsonFolder);
		File [] files = root.listFiles();
		
		for (File f : files) {
			if (f.isDirectory())
				setJSONParam(jsonFolder);
			else {
				String key = f.getName().replace(".json", "");
				Working working = getWork(key);
				if (working != null) {
					jsonParamInject(key, working);
				}
				createFormWorking(key, f);
			}
		}
	}
	
	private void createFormWorking(String key, File jsonFile) {
		JSONObject json = jsonFromFile(jsonFile);
		if (!json.has("form"))
			return;
		String form = json.getString("form");
		if (!formWork.containsKey(form))
			throw new RuntimeException("There is no key at formwork. key:" + key);
		
		try {
			Working working = formWork.get(form).newInstance();
			
			boolean isPublic = false;
			if (json.has("is-public"))
				isPublic = json.getBoolean("is-public");
			
			addWork(key, working, isPublic);
		} catch (InstantiationException e) {
			e.printStackTrace();
			Logger.err(_TAG, "create formwork error", e);
		} catch (IllegalAccessException e) {
			Logger.err(_TAG, "create formwork error", e);
		}
				
	}
	
	void addInjection(String key, StaticLinkedWorking working) {
		List<StaticLinkedWorking> list = mInjectionMap.get(key);
		if (list == null) {
			list = new LinkedList<>();
			mInjectionMap.put(key, list);
		}

		list.add(working);
	}
	
	private JSONObject jsonFromFile(File file) {
		JSONTokener tokener;
		try {
			tokener = new JSONTokener(new FileReader(file));
			JSONObject json = new JSONObject(tokener);
			return json;
		} catch (FileNotFoundException e) {
			Logger.err(_TAG, "json read error", e);
		}
		return null;		
	}

	private JSONObject jsonFromFile(String fileName) {
		return this.jsonFromFile(new File(fileName));
	}
	
	private void jsonParamInject(String key, Working work) {
		Method[] methods = work.getClass().getMethods();
		for (Method m : methods) {
			Annotation a = m.getAnnotation(Jsonparam.class);
			if (a != null) {
				JSONObject json = jsonFromFile(jsonFolder + "/" + key + ".json");
				json.put("work-container", this);
				try {
					m.invoke(work, json);
				} catch (IllegalAccessException e) {
					Logger.err(_TAG, "illegal access error", e);
				} catch (IllegalArgumentException e) {
					Logger.err(_TAG, "illegal argument error", e);
				} catch (InvocationTargetException e) {
					Logger.err(_TAG, "invocation target error", e);
				}
			}
		}
	}

	private void addWorkCommon(String key, Working work) {
		if (jsonFolder != null) {
			jsonParamInject(key, work);
		}

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
		StaticLinkedWorking working = new StaticLinkedWorking();
		JSONObject json = new JSONObject();
		json.put("work-container", this);
		json.put("link-data", paramArray);
		working.setParam(json);
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