package multijson;

import org.junit.Test;

import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;
import com.bunker.bkframework.server.working.Working;

import working.Worka;
import working.Workb;

public class MultiLink {
	@Test public void test() {
		WorkContainer container = new WorkContainer();
		container.addWork("a", new Worka());
		container.addWork("b", new Workb());
		Working working = container.getWork(WorkConstants.MULTI_WORKING);
	}
}
