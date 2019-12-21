package connection;

import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.WorkContainer;

public class TestServer {
	public @Test void testServer() {
		WorkContainer works = new WorkContainer("test");
		works.addformWork("test", working.TestWorking.class);
		works.setJSONParam("jsons");
		HashMap<String, Object> enviroment = new HashMap<String, Object>();
		enviroment.put("trace_list", new LinkedList<>());
		works.getWork("test-linked").doWork(new JSONObject(), enviroment, new WorkTrace());
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