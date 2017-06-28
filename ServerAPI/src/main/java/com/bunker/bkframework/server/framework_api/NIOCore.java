package com.bunker.bkframework.server.framework_api;

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
import com.bunker.bkframework.server.framework_api.NIOResourcePool.NIOResource;

/**
 * 자바 New IO의 클라이언트 접속, read와 관련된 클래스
 * 읽은 정보는 ThreadPool에서 관리하는 Thread들에서 핸들링 하고
 * 채널과 데이터를 ThreadPool에게 관리를 위임한다.
 * 
 * New IO는 이벤트 기반 LifeCycle을 가지고 있으므로
 * 생명 주기를 요청할 때 필요한 만큼 사용하고 종료하도록 한다.
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 5.
 *
 *
 */
public class NIOCore extends CoreBase<ByteBuffer> implements LifeCycle {
	private Selector selector;
	private ThreadPool threadPool;
	private NIOResourcePool mResourcePool;
	private boolean isSuspended = false;
	private Peer<ByteBuffer> prototypePeer;
	private static final String _Tag = "NIOCore";
	private int mWriteBufferSizeKb = 0;

	public NIOCore() {
/*
		prototypePeer = new ServerPeer<ByteBuffer>(new FixedSizeByteBufferPacketFactory(), new ByteBufferBusinessConnector(business), 2000);
		prototypePeer.setLifeCycle(this);
		*/
		threadPool = new ThreadPool(this);
		mResourcePool = new NIOResourcePool();
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

			while (true) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 피어의 접속
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
			writer.setWriteBufferSize(mWriteBufferSizeKb);
			c.setWriter(new NIOWriter((SocketChannel) keyTwo.channel(), c));
			Resource<ByteBuffer> resource = mResourcePool.newPeer(keyTwo, c);
			threadPool.newPeer(resource);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 특정 피어의 데이터를 읽어들인다
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
		//버퍼 할당이 제대로 되지 않았을 때
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

		if (numRead + offset < Constants.PACKET_DEFAULT_TOTAL_SIZE) { //패킷이 짤려서 날라온 경우 붙이는 작업
			resource.remainBuffer(buffer);
			return;
		} else if (numRead + offset == Constants.PACKET_DEFAULT_TOTAL_SIZE) { //패킷이 다 왔을 때
			resource.remainBuffer(null); //남아있는 버퍼를 없앤다.
			buffer.flip(); //버퍼의 사용르 위해 flip
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
	public void restart() {
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
	public void setPeer(Peer<ByteBuffer> peer) {
		prototypePeer = peer;
		prototypePeer.setLifeCycle(this);
	}

	@Override
	public Peer<ByteBuffer> getPrototypePeer() {
		return prototypePeer;
	}

	@Override
	void usePeerServer(SecureFactory<ByteBuffer> sec, Business<ByteBuffer> business) {
		if (sec != null)
			prototypePeer = new ServerPeer<ByteBuffer>(new FixedSizeByteBufferPacketFactory(), sec, new ByteBufferBusinessConnector(business), 2000);
		setPeer(prototypePeer);
	}

	@Override
	void usePeerServer(Business<ByteBuffer> business) {
		prototypePeer = new ServerPeer<ByteBuffer>(new FixedSizeByteBufferPacketFactory(), new ByteBufferBusinessConnector(business), 2000);
		setPeer(prototypePeer);
	}

	public NIOResourcePool getResourcePool() {
		return mResourcePool;
	}

	@Override
	protected String getServerLog() {
		BiConsumer<SelectionKey, NIOResource> consumer = new BiConsumer<SelectionKey, NIOResource>() {

			@Override
			public void accept(SelectionKey s, NIOResource r) {
				
			}
		};
		if (prototypePeer instanceof ServerPeer) {
			String zombieLog = makeZombieData(((ServerPeer) prototypePeer).getZombieKiller());
		}
		mResourcePool.forEarch(consumer);
		return "test";
	}

	private String makeZombieData(ZombieKiller killer) {
		return "zombie test";
	}

	@Override
	void setParam(String paramName, Object param) {
		if (paramName.equals("write_buffer"))
			mWriteBufferSizeKb = (int) param;
	}
}