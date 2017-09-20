package com.bunker.bkframework.server.framework_api;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Resource;

public class ThreadPool {
	private class InitRunnable implements Runnable {
		private Resource<ByteBuffer> mResource;
		private InitRunnable(Resource<ByteBuffer> resource) {
			mResource = resource;
		}

		@Override
		public void run() {
			Peer<ByteBuffer> p = mResource.getPeer();
			p.networkInited(mResource);
			mLifeCycle.manageLife(p);
		}		
	}

	private ExecutorService ex = Executors.newFixedThreadPool(30);
	private LifeCycle mLifeCycle;

	public ThreadPool(LifeCycle lifeCycle) {
		mLifeCycle = lifeCycle;
		Logger.logging(ThreadPool.class.getName(), "started..");
	}

	/**
	 * 클라이언트가 실행 중이면 실행 중인 클라이언트에 내용을 전달하고
	 * 그렇지 않으면 클라이언트 쓰레드를 다시 실행시킨다.
	 * 개발 주의사항 
	 * 1. 클라이언트가 종료되는 과정에 내용을 전달해서 전달된 내용이 누락되지 않도록 한다.
	 * 2. 같은 클라이언트가 동시에 2개 이상 실행되지 않도록 한다.
	 * TODO (검증 필요)
	 * 
	 * @param key 소캣의 채널
	 * @param data 읽은 데이터
	 * @return
	 */
	public void readData(Peer<ByteBuffer> peer, ByteBuffer readBuffer) {
		//실행중이지 않으면 실행시킨다.
		if (!peer.dispatch(readBuffer)) {
			ex.submit(peer);
		}
	}

	/**
	 * 새로은 피어가 들어왔을 때 호출된다.
	 * 피어에 관한 새로운 Peer가 복제되고
	 * 네트워크 연결을 초기화 한다.
	 * @param key
	 */
	public void newPeer(Resource<ByteBuffer> resource) {
		Peer<ByteBuffer> peer = resource.getPeer();
		resource.getPeer().interceptCycle();
		ex.submit(new InitRunnable(resource));
	}

	public void closePeer(Peer<ByteBuffer> peer) {
		peer.interceptCycle();
		peer.close();
	}
}