package connection;

import java.nio.ByteBuffer;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.PeerConnection;
import com.bunker.bkframework.server.framework_api.SocketCore.SocketCoreBuilder;
import com.bunker.bkframework.server.framework_api.nio.NIOCore;

public class TestConnection {
	public static void main(String []args) {
		new SocketCoreBuilder<ByteBuffer, byte[], byte[]>(NIOCore.class).setParam("wrtie_buffer", 8).setPort(9011).useServerPeer(new Business<ByteBuffer, byte[], byte[]>() {

			@Override
			public void established(PeerConnection<byte[]> arg0) {
			}

			@Override
			public void receive(PeerConnection<byte[]> arg0, byte[] arg1, int arg2) {
				System.out.println(new String(arg1));
				arg0.sendToPeer(arg1, arg2);
			}

			@Override
			public void removeBusinessData(PeerConnection<byte[]> arg0) {
			}

		}).build().start();
	}
}