package test.stress;

import java.nio.ByteBuffer;
import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.SocketCore.SocketCoreBuilder;
import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.framework_api.nio.NIOCore;
import com.bunker.bkframework.server.framework_api.nio.NIOJsonBusiness;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

import connection.TestPeerConnection;

public class PeerBaseTest {
	public class TestWorking implements Working {
		
		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			WorkingResult result = new WorkingResult();
			result.putReplyParam("result", true);
			return result;
		}
		
		@Override
		public String getName() {
			return null;
		}
	}
	
	public PeerBaseTest() {
		WorkContainer works = new WorkContainer();
		NIOJsonBusiness business = new NIOJsonBusiness(works);
		new Thread(new SocketCoreBuilder<ByteBuffer, byte[], byte[]>(NIOCore.class).
				setParam("wrtie_buffer", 8)
				.setPort(9011)
				.useServerPeer(business)
				.build()).start();

		business.bindAction();
		TestWorking working = new TestWorking();
		works.addWork("1", working);
		JSONObject json = new JSONObject();
		json.put("working", "1");
		business.receive(new TestPeerConnection(), json.toString().getBytes(), 1);
	}

	public static void main(String []args) {
		
	}
}