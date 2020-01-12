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
			System.out.println(object);
			result.putReplyParam(name + "-src1", "Asg");
			result.putReplyParam(name + "-src2", "Asg");
			result.putReplyParam(name + "-src3", "Asg");
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
	
//	@Test
	public void workLoadTest() {
	}
}