package situationHandling.workflowOperations;

import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class RoutingTable.
 */
class RoutingTable {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(RoutingTable.class);

	// TODO: Ist bei diesen Maps eine synchronisierung notwendig?

	/** <Original-ID, Reply-Address>. */
	private HashMap<String, URL> replyAddresses = new HashMap<>();

	/** <surrogate, original>. */
	private HashMap<String, String> surrogateTable = new HashMap<>();

	/**
	 * Adds the reply address.
	 *
	 * @param messageId the message id
	 * @param target the target
	 */
	void addReplyAddress(String messageId, URL target) {
		replyAddresses.put(messageId, target);
	}

	/**
	 * Gets the reply address.
	 *
	 * @param messageId the message id
	 * @return the reply address
	 */
	URL getReplyAddress(String messageId) {
		return replyAddresses.get(messageId);
	}

	/**
	 * Removes the reply entry.
	 *
	 * @param messageId the message id
	 */
	void removeReplyEntry(String messageId) {
		replyAddresses.remove(messageId);
	}

	/**
	 * Adds the surrogate message id.
	 *
	 * @param messageId the message id
	 * @param surrogate the surrogate
	 */
	void addSurrogateMessageId(String messageId, String surrogate) {
		surrogateTable.put(surrogate, messageId);
	}

	/**
	 * Gets the original message id.
	 *
	 * @param surrogateId the surrogate id
	 * @return the original message id
	 */
	String getOriginalMessageId(String surrogateId) {
		return surrogateTable.get(surrogateId);
	}

	/**
	 * Removes the surrogate id.
	 *
	 * @param surrogate the surrogate
	 */
	void removeSurrogateId(String surrogate) {
		surrogateTable.remove(surrogate);
	}

	/**
	 * Prints the routing table.
	 */
	void printRoutingTable() {
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
		logger.trace(sb.toString());
	}
}
