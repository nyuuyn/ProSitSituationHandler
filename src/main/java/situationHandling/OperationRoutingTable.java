package situationHandling;

import java.net.URL;
import java.util.HashMap;

class OperationRoutingTable {

	private HashMap<String, URL> replyAddresses = new HashMap<>();

	private HashMap<String, String> surrogateTable = new HashMap<>();

	void addReplyAddress(String messageId, URL target) {
		replyAddresses.put(messageId, target);
	}

	URL getReplyAddress(String messageId) {
		return replyAddresses.get(messageId);
	}
	
	void removeReplyEntry (String messageId){
		replyAddresses.remove(messageId);
	}

	void addSurrogateMessageId(String messageId, String surrogate) {
		surrogateTable.put(surrogate, messageId);
	}

	String getOriginalMessageId(String surrogateId) {
		return surrogateTable.get(surrogateId);
	}
	
	void removeSurrogateId(String surrogate){
		surrogateTable.remove(surrogate);
	}
}
