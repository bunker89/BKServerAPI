package com.bunker.bkframework.server.framework_api.nio;

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
	 * Ŭ���̾�Ʈ�� ���� ���̸� ���� ���� Ŭ���̾�Ʈ�� ������ �����ϰ�
	 * �׷��� ������ Ŭ���̾�Ʈ �����带 �ٽ� �����Ų��.
	 * ���� ���ǻ��� 
	 * 1. Ŭ���̾�Ʈ�� ����Ǵ� ������ ������ �����ؼ� ���޵� ������ �������� �ʵ��� �Ѵ�.
	 * 2. ���� Ŭ���̾�Ʈ�� ���ÿ� 2�� �̻� ������� �ʵ��� �Ѵ�.
	 * TODO (���� �ʿ�)
	 * 
	 * @param key ��Ĺ�� ä��
	 * @param data ���� ������
	 * @return
	 */
	public void readData(Peer<ByteBuffer> peer, ByteBuffer readBuffer) {
		//���������� ������ �����Ų��.
		if (!peer.dispatch(readBuffer)) {
			ex.submit(peer);
		}
	}

	/**
	 * ������ �Ǿ ������ �� ȣ��ȴ�.
	 * �Ǿ ���� ���ο� Peer�� �����ǰ�
	 * ��Ʈ��ũ ������ �ʱ�ȭ �Ѵ�.
	 * @param key
	 */
	public void newPeer(Resource<ByteBuffer> resource) {
		resource.getPeer().interceptCycle();
		ex.submit(new InitRunnable(resource));
	}

	public void closePeer(Peer<ByteBuffer> peer) {
		peer.interceptCycle();
		peer.close();
	}
}