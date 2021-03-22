package test;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.server.framework_api.ServerDefaultLog;
import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.framework_api.text.TextJSONBusiness;
import com.bunker.bkframework.server.framework_api.text.TextServerCore;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class TestServer {
	private class TestWorking implements Working {
		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			WorkingResult result = new WorkingResult();
			object.getString("abc");
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
	}

	public TestServer() {
		Logger.mLog = new ServerDefaultLog();
		WorkContainer container = new WorkContainer();
		container.addWork("a", new TestWorking());
		JSONObject json = new JSONObject();
		json.put(WorkConstants.WORKING, "a");

		TextServerCore server = new TextServerCore();
		TextJSONBusiness business = new TextJSONBusiness(container);
		server.setPeer(business);
		
		server.doWork(json.toString());
	}

	public static void main(String []args) {
		Logger.mLog = new ServerDefaultLog();
		new TestServer();
	}
}