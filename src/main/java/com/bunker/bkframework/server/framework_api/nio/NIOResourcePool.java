package com.bunker.bkframework.server.framework_api.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.bunker.bkframework.newframework.Constants;
import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Resource;

public class NIOResourcePool {
	private final String _TAG = "NIOResourcePool";
	private ResourceEmptyCallback mEmptyCallback;
	private boolean mLoggingOn = true;

	public class NIOResource implements Resource<ByteBuffer>{
		private ByteBuffer remainBuffer;
		private String remoteAddress;

		public NIOResource(SelectionKey key, Peer<ByteBuffer> peer) {
			mPeer = peer;
			mKey = key;
			if (key != null) {
				SocketChannel channel = (SocketChannel) key.channel();
				if (channel != null) {
					try {
						remoteAddress = channel.getRemoteAddress().toString();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		Peer<ByteBuffer> mPeer;
		SelectionKey mKey;

		@Override
		public ByteBuffer getReadBuffer() {
			//TODO ������ üũ
			if (remainBuffer == null) {
				return ByteBuffer.allocateDirect(Constants.PACKET_DEFAULT_TOTAL_SIZE);
			}
			else
				return remainBuffer;
		}

		@Override
		public void destroy() {
			resourceMap.remove(mKey);
			if (mLoggingOn)
				Logger.logging(_TAG, "resource killed, count:" + resourceMap.size());
			if (resourceMap.size() == 0) {
				System.gc();
			}
		}

		@Override
		public Peer<ByteBuffer> getPeer() {
			return mPeer;
		}

		public void remainBuffer(ByteBuffer buffer) {
			remainBuffer = buffer;
		}

		@Override
		public String getClientHostInfo() {
			return remoteAddress;
		}
	}

	private Map<SelectionKey, NIOResource> resourceMap = new HashMap<SelectionKey, NIOResource>();

	public void setOnResourceEmptyCallback(ResourceEmptyCallback callback) {
		mEmptyCallback = callback;
	}

	public NIOResource getResource(SelectionKey key) {
		return resourceMap.get(key);
	}

	public NIOResource newPeer(SelectionKey key, Peer<ByteBuffer> peer) {
		NIOResource resource = new NIOResource(key, peer);
		resourceMap.put(key, resource);
		if (mLoggingOn)
			Logger.logging(_TAG, "resource added, count:" + resourceMap.size());
		return resource;
	}

	public void forEarch(BiConsumer<? super SelectionKey, ? super NIOResource> consumer) {
		resourceMap.forEach(consumer);
	}

	public int getResourceCount() {
		return resourceMap.size();
	}
	
	public void enableLog(boolean on) {
		mLoggingOn = on;
	}
}