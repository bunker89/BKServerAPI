package multijson;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.BKWork;
import com.bunker.bkframework.server.working.StaticLinkedWorking;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingResult;

public class StaticLink {
	@BKWork(key = "1", input={"a"}, output={"c","d"})
	class Worka implements Working {

		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			// TODO Auto-generated method stub
			return null;
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

	@BKWork(key = "2", input={"c", "d", "e"}, output={"f","g"})
	class Workb implements Working {

		@Override
		public WorkingResult doWork(JSONObject object, Map<String, Object> enviroment, WorkTrace trace) {
			// TODO Auto-generated method stub
			return null;
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
		List<Working> list = new LinkedList<>();
		list.add(new Worka());
		list.add(new Workb());
		StaticLinkedWorking multiJson = new StaticLinkedWorking();
		multiJson.setWorkLink(list);
	}
}
