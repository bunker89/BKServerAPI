package multijson;

import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.StaticLinkedWorking;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class WorkContainerStaticLinkTest {
	private class TestWorking implements Working {

		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			WorkingResult result = new WorkingResult();
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
		WorkContainer container = new WorkContainer();
		Working working = new TestWorking();
		container.addWork("test1", working);
		container.addWork("test2", working);
		container.addWork("test3", working);
		StaticLinkedWorking linkedWorking = (StaticLinkedWorking) container.getWork("test-static-load");
		linkedWorking.iteratePringWorkings();
	}
}
