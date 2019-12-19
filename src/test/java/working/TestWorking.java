package working;

import java.util.Map;

import org.json.JSONObject;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.Jsonparam;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class TestWorking implements Working {

	@Jsonparam public void insertJSON(JSONObject json) {
		System.out.println(json);
	}
	@Override
	public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
		WorkingResult result = new WorkingResult();
		result.putReplyParam("result", true);
		result.putReplyParam("test", "Tt");
		System.out.println("TestWorking param" + object);
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
