package multijson;

import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import com.bunker.bkframework.server.framework_api.WorkTrace;
import com.bunker.bkframework.server.working.BKWork;
import com.bunker.bkframework.server.working.KeyConvertBuilder;
import com.bunker.bkframework.server.working.StaticLinkedWorking;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;
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
		WorkContainer container = new WorkContainer();
		container.addWork("a", new Worka());
		container.addWork("b", new Workb());
		StaticLinkedWorking working = container.makeLinkedWorkBuilder()
				.addWorkLink("a")
				.addWorkLink(WorkConstants.KEY_RENAME_WORKING, new KeyConvertBuilder()
						.putConvert("f", "e")
						.build())
				.addWorkLink("b")
				.build();
		container.addWork("test", working);
		System.out.println(working.getParamRequired());
	}
}
