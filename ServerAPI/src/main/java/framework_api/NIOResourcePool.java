package framework_api;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.bunker.bkframework.newframework.Constants;
import com.bunker.bkframework.newframework.Peer;
import com.bunker.bkframework.newframework.Resource;

public class NIOResourcePool {
	public class NIOResource implements Resource<ByteBuffer>{
		private ByteBuffer remainBuffer;
		public NIOResource(SelectionKey key, Peer<ByteBuffer> peer) {
			mPeer = peer;
			mKey = key;
		}
		Peer<ByteBuffer> mPeer;
		SelectionKey mKey;

		@Override
		public ByteBuffer getReadBuffer() {
			//TODO 과부하 체크
			if (remainBuffer == null) {
				return ByteBuffer.allocateDirect(Constants.PACKET_TOTAL_SIZE);
			}
			else
				return remainBuffer;
		}

		@Override
		public void destroy() {
			resourceMap.remove(mKey);
			System.out.println("resource killed, count:" + resourceMap.size());;
		}

		@Override
		public Peer<ByteBuffer> getPeer() {
			return mPeer;
		}

		public void remainBuffer(ByteBuffer buffer) {
			remainBuffer = buffer;
		}
	}
	
	private Map<SelectionKey, NIOResource> resourceMap = new HashMap<SelectionKey, NIOResource>();
	
	public NIOResource getResource(SelectionKey key) {
		return resourceMap.get(key);
	}
	
	public NIOResource newPeer(SelectionKey key, Peer<ByteBuffer> peer) {
		NIOResource resource = new NIOResource(key, peer);
		resourceMap.put(key, resource);
		System.out.println("resource added, count:" + resourceMap.size());
		return resource;
	}
	
	public void forEarch(BiConsumer<? super SelectionKey, ? super NIOResource> consumer) {
		resourceMap.forEach(consumer);
	}
}