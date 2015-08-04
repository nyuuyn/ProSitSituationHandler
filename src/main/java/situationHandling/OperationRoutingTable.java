package situationHandling;

import java.net.URL;
import java.util.HashMap;

class OperationRoutingTable {

	private HashMap<String, URL> replyAddresses = new HashMap<>();

	
	void addReplyAddress (String messageId, URL target){
		replyAddresses.put(messageId, target);
	}
	
	URL getReplyAddress(String messageId){
		return replyAddresses.get(messageId);
	}
}
