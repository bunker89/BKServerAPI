package multijson;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.framework_api.WorkTraceList;
import com.bunker.bkframework.server.working.BKWork;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class StaticLink {
	@BKWork(key = "1", input={"a"}, output={"c","d"}, isPublic=false)
	class Worka implements Working {

		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			System.out.println("do a" + object);
			WorkingResult result = new WorkingResult();
			result.putReplyParam("c", "cv");
			result.putReplyParam(WorkConstants.WORKING_RESULT, true);
			return result;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}
	}

	@BKWork(key = "2", input={"c", "d", "e"}, output={"f","g"})
	class Workb implements Working {

		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			System.out.println("do b" + object);

			WorkingResult result = new WorkingResult();
			result.putReplyParam(WorkConstants.WORKING_RESULT, true);
			result.putReplyParam("f", "fv");
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
	public void test() {
		WorkContainer container = new WorkContainer();
		container.addWorkPrivate("1", new Worka());
		container.addWork("2", new Workb());

		JSONObject json = new JSONObject();
		json.put("a", "av");
		json.put("tt", "tt");

		Map<String, Object> enviroment = new HashMap<>();
		enviroment.put("trace_list", new WorkTraceList());
		container.getPublicWork("3").doWork(json, enviroment, null);
	}
}