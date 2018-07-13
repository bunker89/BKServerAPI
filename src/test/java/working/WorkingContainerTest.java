package working;

import org.junit.Test;

import com.bunker.bkframework.server.working.WorkContainer;

public class WorkingContainerTest {
	public @Test void test() {
		WorkContainer container = new WorkContainer();
		try {
			container.loadWorkings("com.bunker.bkframework");
			container.addLinkedWork("linked", new String[]{"work-base"});
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}