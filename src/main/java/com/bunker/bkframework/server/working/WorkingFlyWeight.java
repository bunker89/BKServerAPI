package com.bunker.bkframework.server.working;

import java.util.HashMap;
import java.util.Map;

public class WorkingFlyWeight {
	private static Map <Object, Working> map = new HashMap<>();
		
	public static Working getWorking(Object key) {
		return map.get(key);
	}
	
	public static void setCreatedWorking(Object key, Working working) {
		if (working != null)
			map.put(key, working);
	}

	public static boolean contains(Object key) {
		return map.containsKey(key);
	}
}