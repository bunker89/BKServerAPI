package connection;

import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.Jsonparam;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class TestServer {
	public class TestWorking implements Working {
		@Jsonparam public void insertJSON(JSONObject json) {
			System.out.println(json);
		}

		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			WorkingResult result = new WorkingResult();
			result.putReplyParam("result", true);
			System.out.println("abc");
			return result;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}		
	}
	
	public @Test void testServer() {
		WorkContainer works = new WorkContainer("test");
		works.addformWorkWork("test", working.TestWorking.class);
		works.setJSONParam("jsons");
//		NIOJsonBusiness business = new NIOJsonBusiness(works);
//		new Thread(new SocketCoreBuilder<ByteBuffer, byte[], byte[]>(NIOCore.class).
//				setParam("wrtie_buffer", 8)
//				.setPort(9011)
//				.useServerPeer(business)
//				.build()).start();
//		business.bindAction();
//		JSONObject json = new JSONObject();
//		json.put("working", "1");
//		TestPeerConnection connection = new TestPeerConnection();
//		business.established(connection);
//		business.receive(connection, json.toString().getBytes(), 1);
	}
}