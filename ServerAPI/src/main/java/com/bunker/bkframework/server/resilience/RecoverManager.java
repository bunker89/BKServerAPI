package com.bunker.bkframework.server.resilience;

import java.util.ArrayList;
import java.util.List;

public class RecoverManager {
	private static RecoverManager This;

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
		}
	};

	private final ResilienceState mChangeSafety = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			
		}
	};

	private final ResilienceState mOutOfSystem = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			// TODO Auto-generated method stub

		}
	};

	private final ResilienceState mCantRecover = new ResilienceState() {

		@Override
		public void recorver(Resilience resilience, ErrMessage msg) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private List<SystemModule> mSystemModules = new ArrayList<>();

	public void restart() {
	}

	public void recover(Resilience resilience) {
		
	}
}