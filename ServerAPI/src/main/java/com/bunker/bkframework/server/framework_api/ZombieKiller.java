package com.bunker.bkframework.server.framework_api;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ZombieKiller extends Thread {
	public interface Killable {
		public long getLastEventTime();
		public void kill();
	}
	private static final long THREADSHOLD = 1000 * 60 * 10;
	private static final long SUSPEND_TIME = 1000 * 60 * 2;

	public List<Killable> mKillables = Collections.synchronizedList(new LinkedList<>());

	public ZombieKiller() {
		setPriority(MIN_PRIORITY);
	}

	public void addKillable(Killable r) {
		mKillables.add(r);
	}

	public void removeKillable(Killable r) {
		mKillables.remove(r);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(SUSPEND_TIME);
				long currentTime = Calendar.getInstance().getTimeInMillis();
				List<Killable> temp = new LinkedList<>(mKillables);
				for (Killable k : temp) {
					if (currentTime - k.getLastEventTime() > THREADSHOLD) {
						k.kill();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}