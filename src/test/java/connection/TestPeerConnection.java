package connection;

import java.util.HashMap;
import java.util.Map;

import com.bunker.bkframework.business.PeerConnection;

public class TestPeerConnection implements PeerConnection<byte[]> {
	private HashMap<String, Object> enviroment = new HashMap<>();
	
	@Override
	public void closePeer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getEnviroment() {
		return enviroment;
	}

	@Override
	public void sendToPeer(byte[] arg0, int arg1) {
	}

}
