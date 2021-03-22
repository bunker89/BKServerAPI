package working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.Jsonparam;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class TestWorking implements Working {

	@Jsonparam public void insertJSON(JSONObject json) {
		System.out.println(json);
	}
	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		System.out.println(object);
		result.putReplyParam(WorkConstants.WORKING_RESULT, true);
		result.putReplyParam("test", "Tt");
		return result;
	}

	@Override
	public String getName() {
		return null;
	}
}
