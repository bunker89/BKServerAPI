package text;

import org.junit.Test;

import com.bunker.bkframework.server.framework_api.text.TextConnection;
import com.bunker.bkframework.server.framework_api.text.TextJSONBusiness;

public class TextBusiness {

	@Test
	public void test() {
		TextJSONBusiness business = new TextJSONBusiness();
		TextConnection connection = new TextConnection();
		business.established(connection);
		business.receive(connection, "{\"test\":1}", 0);
		System.out.println(connection.getResult());
	}
}
