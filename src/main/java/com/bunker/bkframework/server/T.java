package com.bunker.bkframework.server;

import java.util.Set;

import org.reflections.Reflections;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.working.BKWork;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;

public class T {
	private WorkContainer mWorkContainer;
	
	public T(WorkContainer container) {
		mWorkContainer = container;
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
					mWorkContainer.addWork(annotation.key(), c.newInstance());
				} else {
					mWorkContainer.addWorkPrivate(annotation.key(), c.newInstance());
				}
			}
		}
	}
	
	public static void main(String []args) {
		WorkContainer container = new WorkContainer("test");
		try {
			new T(container).loadWorkings("com.bunker.bkframework");
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}