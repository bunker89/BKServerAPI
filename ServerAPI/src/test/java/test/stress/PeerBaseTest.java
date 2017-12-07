package test.stress;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.CoreBase.CoreBuilder;
import com.bunker.bkframework.server.framework_api.RJSonServerBusiness;
import com.bunker.bkframework.server.framework_api.nio.NIOCore;
import com.bunker.bkframework.server.reserved.Pair;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingFlyWeight;
import com.bunker.bkframework.server.working.WorkingResult;

import connection.TestPeerConnection;

public class PeerBaseTest {
	public class TestWorking implements Working {
		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment) {
			WorkingResult result = new WorkingResult();
			result.putReplyParam("result", true);
			return result;
		}		
	}

	public PeerBaseTest() {
		RJSonServerBusiness business = new RJSonServerBusiness();
		new Thread(new CoreBuilder<ByteBuffer>(NIOCore.class).
				setParam("wrtie_buffer", 8)
				.setPort(9011)
				.useServerPeer(business)
				.build()).start();

		business.bindAction();
		TestWorking working = new TestWorking();
		WorkingFlyWeight.setCreatedWorking(1, working);
		JSONObject json = new JSONObject();
		json.put("working", 1);
		business.receive(new TestPeerConnection(), json.toString().getBytes(), 1);
	}

	public static void main(String []args) {
		
	}
}
