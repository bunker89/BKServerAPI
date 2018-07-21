package multijson;

import org.junit.Test;

import com.bunker.bkframework.server.working.KeyConvertBuilder;
import com.bunker.bkframework.server.working.StaticLinkedWorking;
import com.bunker.bkframework.server.working.WorkConstants;
import com.bunker.bkframework.server.working.WorkContainer;

import working.Worka;
import working.Workb;

public class StaticLink {

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
