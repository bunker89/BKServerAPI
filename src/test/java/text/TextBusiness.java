package text;

import org.junit.Test;

import com.bunker.bkframework.server.BKLauncher;
import com.bunker.bkframework.server.framework_api.text.TextJSONBusiness;
import com.bunker.bkframework.server.framework_api.text.TextServerCore;
import com.bunker.bkframework.server.working.WorkContainer;

import working.TestWorking;

public class TextBusiness {

	@Test
	public void test() {
		BKLauncher bkLauncher = new BKLauncher();
		WorkContainer works = new WorkContainer();
		TextJSONBusiness business = new TextJSONBusiness(works);
		works.addWork("test", new TestWorking());
		TextServerCore textCore = new TextServerCore();
		textCore.setPeer(business);
		bkLauncher.startServer(textCore);
		System.out.println(textCore.doWork("{\"working\":\"test\"}"));
	}
}