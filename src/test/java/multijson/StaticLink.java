package multijson;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.bunker.bkframework.server.working.BKWork;
import com.bunker.bkframework.server.working.StaticLinkedWorking;
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
		List<Working> list = new LinkedList<>();
		list.add(new Worka());
		list.add(new Workb());
		StaticLinkedWorking multiJson = new StaticLinkedWorking();
		multiJson.setWorkLink(list);
	}
}
