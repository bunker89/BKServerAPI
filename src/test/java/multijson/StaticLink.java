package multijson;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.bunker.bkframework.server.working.BKWork;
import com.bunker.bkframework.server.working.MultiJSONWorking;
import com.bunker.bkframework.server.working.StaticLinkedWorking;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;
import com.bunker.bkframework.server.working.WorkingBase;

public class StaticLink {
	@BKWork(key = "1", input={"a"}, output={"c","d"})
	class Worka extends WorkingBase {
		
	}

	@BKWork(key = "2", input={"c", "d", "e"}, output={"f","g"})
	class Workb extends WorkingBase {
		
	}

	@Test
	public void test() {
		StaticLinkedWorking multiJson = new StaticLinkedWorking();
		multiJson.addWorkLink(new Worka());
		multiJson.addWorkLink(new Workb());
	}
}
