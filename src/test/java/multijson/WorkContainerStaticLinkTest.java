package multijson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class WorkContainerStaticLinkTest {
	private class TestWorking implements Working {
		String name;
		
		TestWorking(String name) {
			this.name = name;
		}
		
		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			WorkingResult result = new WorkingResult();
			result.putPrivateParam(name + "-src", name);
			System.out.println(name);
			result.putReplyParam(WorkConstants.WORKING_RESULT, true);
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
	
	@Test
	public void workLoadTest() {
		String chainJSONString = "[{\"as\":\"t1\",\"key\":\"test1\"},{\"as\":\"t2\",\"param\":{\"t1\":[{\"t1-src\":\"t1-dst\"}]},\"key\":\"test2\"},{\"as\":\"t3\",\"param\":{\"t2\":[{\"t2-src\":\"t2-dst\"}]},\"key\":\"test3\"}]";
		WorkContainer container = new WorkContainer();
		
		container.addWork("test1", new TestWorking("t1"));
		container.addWork("test2", new TestWorking("t2"));
		container.addWork("test3", new TestWorking("t3"));
		
		container.addLinkedWork("testLink", new JSONArray(chainJSONString), true);
		HashMap<String, Object> enviroment = new HashMap<>();
		
		enviroment.put("trace_list", new LinkedList<>());
		container.getWork("testLink").doWork(new JSONObject(), enviroment, new WorkTrace());
	}
}