package com.bunker.bkframework.server.framework_api.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.function.BiConsumer;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.ByteBufferBusinessConnector;
import com.bunker.bkframework.newframework.Constants;
import com.bunker.bkframework.newframework.FixedSizeByteBufferPacketFactory;
import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.NIOWriter;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.PeerLife;
import com.bunker.bkframework.newframework.Resource;
import com.bunker.bkframework.sec.SecureFactory;
import com.bunker.bkframework.server.framework_api.CoreBase;
import com.bunker.bkframework.server.framework_api.ServerPeer;
import com.bunker.bkframework.server.framework_api.ZombieKiller;
import com.bunker.bkframework.server.framework_api.nio.NIOResourcePool.NIOResource;
import com.bunker.bkframework.server.resilience.DefaultResilience;
import com.bunker.bkframework.server.resilience.ErrMessage;
import com.bunker.bkframework.server.resilience.RecoverManager;
import com.bunker.bkframework.server.resilience.Resilience;

/**
 * 占쌘뱄옙 New IO占쏙옙 클占쏙옙占싱억옙트 占쏙옙占쏙옙, read占쏙옙 占쏙옙占시듸옙 클占쏙옙占쏙옙
 * 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 ThreadPool占쏙옙占쏙옙 占쏙옙占쏙옙占싹댐옙 Thread占썽에占쏙옙 占쌘들링 占싹곤옙
 * 채占싸곤옙 占쏙옙占쏙옙占싶몌옙 ThreadPool占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占싼댐옙.
 * 
 * New IO占쏙옙 占싱븝옙트 占쏙옙占� LifeCycle占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占실뤄옙
 * 占쏙옙占쏙옙 占쌍기를 占쏙옙청占쏙옙 占쏙옙 占십울옙占쏙옙 占쏙옙큼 占쏙옙占쏙옙構占� 占쏙옙占쏙옙占싹듸옙占쏙옙 占싼댐옙.
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 5.
 *
 *
 */
public class NIOCore extends CoreBase<ByteBuffer, byte[], byte[]> implements LifeCycle, ResourceEmptyCallback {
	private Selector selector;
	private ThreadPool threadPool;
	private NIOResourcePool mResourcePool;
	private boolean isSuspended = false;
	private Peer<ByteBuffer> prototypePeer;
	private static final String _Tag = "NIOCore";
	private int mWriteBufferSizeKb = 0;
	private boolean mLoopKeep = true;
	private boolean mIsLooping = true;

	private Resilience mLoopResilience = new DefaultResilience() {
	
		@Override
		public boolean recoverPart(ErrMessage msg) {
			reStartLoop();
			return true;
		}

		@Override
		public void needRecover(ErrMessage msg) {
			RecoverManager.getInstance().recover(this, msg);
		}

		@Override
		public String getResilienceName() {
			return "NIOCoreLoop";
		}
	};

	public NIOCore() {
		/*
		prototypePeer = new ServerPeer<ByteBuffer>(new FixedSizeByteBufferPacketFactory(), new ByteBufferBusinessConnector(business), 2000);
		prototypePeer.setLifeCycle(this);
		 */
		threadPool = new ThreadPool(this);
		mResourcePool = new NIOResourcePool();
		RecoverManager.getInstance().initResilienceModule(mLoopResilience);
	}
	
	private void reStartLoop() {
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					serverLoop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		th.start();
	}

	@Override
	public void launch(int port) {
		try {
			selector = Selector.open();
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			InetSocketAddress addr = new InetSocketAddress(port);
			serverSocket.bind(addr);
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			Logger.err(_Tag, "NIOserver launch exception");
		}
		
		try {
			serverLoop();
		} catch (IOException e) {
			mIsLooping = false;
			Logger.err(_Tag, "NIOserver loop exception", e);
		}
	}

	private void serverLoop() throws IOException {
		mIsLooping = true;
		while (mLoopKeep) {
			selector.select();
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				keys.remove();

				try {
					if (!key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					}
				} catch (IOException e) {
					Logger.logging(_Tag, "connection broked");
					NIOResource resource = mResourcePool.getResource(key);
					if (resource != null) {
						threadPool.closePeer(resource.mPeer);
					}
					key.cancel();
				}
			} 
		}
		mIsLooping = false;
	}

	/**
	 * 占실억옙占쏙옙 占쏙옙占쏙옙
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		if (isSuspended)
			return;

		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);
		SelectionKey keyTwo = channel.register(selector, SelectionKey.OP_READ);

		try {
			Peer<ByteBuffer> c = prototypePeer.clone();
			NIOWriter writer = new NIOWriter((SocketChannel) keyTwo.channel(), c);
			if (mWriteBufferSizeKb > 0)
				writer.setWriteBufferSize(mWriteBufferSizeKb);
			c.setWriter(writer);
			Resource<ByteBuffer> resource = mResourcePool.newPeer(keyTwo, c);
			threadPool.newPeer(resource);
		} catch (CloneNotSupportedException e) {
			Logger.err(_Tag, "accept:clone not support exception", e);
		}
	}

	/**
	 * 특占쏙옙 占실억옙占쏙옙 占쏙옙占쏙옙占싶몌옙 占싻억옙占쏙옙灌占�
	 * @param key
	 * @throws IOException
	 */
	private void read(SelectionKey key) throws IOException  {
		SocketChannel channel = (SocketChannel) key.channel();
		NIOResource resource = mResourcePool.getResource(key);
		if (resource == null) {
			Logger.err("NIOCore", "resource is null, maybe the channel was not disconnected when resource is removed");
			channel.close();
			return;
		}
		ByteBuffer buffer = resource.getReadBuffer();
		int offset = buffer.position();
		//占쏙옙占쏙옙 占쌀댐옙占쏙옙 占쏙옙占쏙옙占� 占쏙옙占쏙옙 占십억옙占쏙옙 占쏙옙
		if (buffer.limit() != Constants.PACKET_DEFAULT_TOTAL_SIZE) {
			Logger.err("NIOCore", "Buffer alloc err");
			return;
		}
		int numRead = -1;

		numRead = channel.read(buffer);
		if (numRead < 0) {
			channel.close();
			key.cancel();
			threadPool.closePeer(resource.mPeer);
		}

		if (numRead + offset < Constants.PACKET_DEFAULT_TOTAL_SIZE) { //占쏙옙킷占쏙옙 짤占쏙옙占쏙옙 占쏙옙占쏙옙占� 占쏙옙占� 占쏙옙占싱댐옙 占쌜억옙
			resource.remainBuffer(buffer);
			return;
		} else if (numRead + offset == Constants.PACKET_DEFAULT_TOTAL_SIZE) { //占쏙옙킷占쏙옙 占쏙옙 占쏙옙占쏙옙 占쏙옙
			resource.remainBuffer(null); //占쏙옙占쏙옙占쌍댐옙 占쏙옙占쌜몌옙 占쏙옙占쌔댐옙.
			buffer.flip(); //占쏙옙占쏙옙占쏙옙 占쏙옙釉� 占쏙옙占쏙옙 flip
			threadPool.readData(resource.getPeer(), buffer);
		} else {
			Logger.err("NIOCore", "Buffer Overflow");
		}
	}

	@Override
	public void destroyCore() {
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void suspendNewPeer() {
		isSuspended = true;
	}

	@Override
	public void acceptNewPeer() {
		isSuspended = false;
	}

	@Override
	public LifeCycle getLifeCycle() {
		return this;
	}

	@Override
	public void manageLife(PeerLife life) {
		while (life.needRecycle()) {
			life.life();
		}
	}

	@Override
	protected void setPeer(Peer<ByteBuffer> peer) {
		prototypePeer = peer;
		prototypePeer.setLifeCycle(this);
	}

	@Override
	public Peer<ByteBuffer> getPrototypePeer() {
		return prototypePeer;
	}

	@Override
	public void usePeerServer(SecureFactory<ByteBuffer> sec, Business<ByteBuffer, byte[], byte[]> business) {
		if (sec != null)
			prototypePeer = new ServerPeer<ByteBuffer, byte[], byte[]>(new FixedSizeByteBufferPacketFactory(), sec, new ByteBufferBusinessConnector(business), 2000);
		setPeer(prototypePeer);
	}

	@Override
	public void usePeerServer(Business<ByteBuffer, byte[], byte[]> business) {
		prototypePeer = new ServerPeer<ByteBuffer, byte[], byte[]>(new FixedSizeByteBufferPacketFactory(), new ByteBufferBusinessConnector(business), 2000);
		setPeer(prototypePeer);
	}

	public NIOResourcePool getResourcePool() {
		return mResourcePool;
	}

	@Override
	public String getServerLog() {
		BiConsumer<SelectionKey, NIOResource> consumer = new BiConsumer<SelectionKey, NIOResource>() {

			@Override
			public void accept(SelectionKey s, NIOResource r) {
			}
		};

		if (prototypePeer instanceof ServerPeer) {
			String zombieLog = makeZombieData(((ServerPeer<ByteBuffer, byte[], byte[]>) prototypePeer).getZombieKiller());
		}
		mResourcePool.forEarch(consumer);
		return "test";
	}

	private String makeZombieData(ZombieKiller killer) {
		return "zombie test";
	}

	@Override
	protected void setParam(String paramName, Object param) {
		if (paramName.equals("write_buffer"))
			mWriteBufferSizeKb = (int) param;
	}

	@Override
	public void moduleForceRestart() {
		Logger.logging(_Tag, "moduleForceRestarting..");
	}

	@Override
	public void moduleSafetyStop() {
		suspendNewPeer();
	}

	@Override
	public void empty() {
	}

	@Override
	public boolean isStoped() {
		return !mIsLooping;
	}

	@Override
	public boolean moduleStart() {
		if (mIsLooping || mLoopKeep)
		return false;
		
		reStartLoop();
		return true;
	}
}