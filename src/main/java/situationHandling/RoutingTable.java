package situationHandling;

import java.net.URL;
import java.util.HashMap;

class RoutingTable {

	//TODO: Ist bei diesen Maps eine synchronisierung notwendig?
	
	/**
	 * 
	 * <Original-ID, Reply-Address>
	 * 
	 */
	private HashMap<String, URL> replyAddresses = new HashMap<>();

	/**
	 * 
	 * <surrogate, original>
	 */
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
	
	void printRoutingTable(){
		StringBuilder sb = new StringBuilder();
		sb.append("---------Routing Table---------\n");
		sb.append("<Original ID> --> <Reply Address>\n");
		for (String id : replyAddresses.keySet()) {
			sb.append(id + " --> " + replyAddresses.get(id));
			sb.append("\n");
		}
		sb.append("--------------\n");
		sb.append("<Surrogate ID> --> <Original Id>\n");
		for (String surrogate : surrogateTable.keySet()) {
			sb.append(surrogate + " --> " + surrogateTable.get(surrogate));
			sb.append("\n");
		}
		
		sb.append("---------End Routing Table---------\n");
		System.out.println(sb.toString());
	}
}
