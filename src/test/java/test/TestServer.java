package test;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.SocketCore;
import com.bunker.bkframework.server.framework_api.SocketCore.SocketCoreBuilder;
import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.framework_api.nio.NIOCore;
import com.bunker.bkframework.server.framework_api.nio.NIOJsonBusiness;
import com.bunker.bkframework.server.reserved.Pair;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingFlyWeight;
import com.bunker.bkframework.server.working.WorkingResult;

import connection.TestPeerConnection;

public class TestServer {
	public class TestWorking implements Working {
		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			WorkingResult result = new WorkingResult();
			result.putReplyParam("result", true);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

	public TestServer() {
		NIOJsonBusiness business = new NIOJsonBusiness();
		SocketCore<ByteBuffer, byte[], byte[]> core = new SocketCoreBuilder<ByteBuffer, byte[], byte[]>(NIOCore.class)
				.setParam("wrtie_buffer", 8)
				.setPort(9011)
				.useServerPeer(business)
				.build(); 
		new Thread(core).start();
		System.out.println(core.getSystemParam("use_ping_port"));

		business.bindAction();
		TestWorking working = new TestWorking();
		WorkingFlyWeight.setCreatedWorking(1, working);
		JSONObject json = new JSONObject();
		json.put("working", 1);

		business.receive(new TestPeerConnection(), json.toString().getBytes(), 1);

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				System.out.println("run");

				List<Pair> list = business.logging();
				Iterator<Pair> iter = list.iterator();
				System.out.println(iter.next().getValue());
			}
		};

		timer.schedule(task, 0);
	}

	public static void main(String []args) {
		new TestServer();
	}
}