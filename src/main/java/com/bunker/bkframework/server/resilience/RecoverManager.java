package com.bunker.bkframework.server.resilience;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecoverManager {
	private static RecoverManager This;
	private Set<Resilience> mRecoverings = new HashSet<>();

	public static RecoverManager getInstance() {
		if (This == null) {
			synchronized (RecoverManager.class) {
				if (This == null)
					This = new RecoverManager();
			}
		}
		
		return This;
	}

	public void initResilienceModule(Resilience resilience) {
		resilience.changeResilienceState(mPartRecover, 0);
	}

	private final ResilienceState mPartRecover = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			if (resilience.recoverPart(msg))
				return;

			resilience.changeResilienceState(mPartRestart, 1);
			resilience.needRecover(msg);
		}
	};

	private final ResilienceState mPartRestart = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {			
			if (resilience.restartPart(msg))
				return;

			resilience.changeResilienceState(mChangeSafety, 2);
			resilience.needRecover(msg);
		}
	};

	private final ResilienceState mChangeSafety = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			if (resilience.changeSafetyModule(msg))
				return;

			resilience.changeResilienceState(mOutOfSystem, 3);
			resilience.needRecover(msg);
		}
	};

	private final ResilienceState mOutOfSystem = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			if (resilience.outOfSystem(msg))
				return;

			resilience.changeResilienceState(mCantRecover, 4);
			resilience.needRecover(msg);
		}
	};

	private final ResilienceState mCantRecover = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			restart();
		}
	};

	private List<SystemModule> mSystemModules = new ArrayList<>();

	public void restart() {
		
	}

	synchronized public void recover(final Resilience resilience, final ErrMessage msg) {
		if (mRecoverings.contains(resilience))
			return;

		new Thread() {
			@Override
			public void run() {
				resilience.getRecoverState().recorver(resilience, msg);
			}
		}.start();
	}
}