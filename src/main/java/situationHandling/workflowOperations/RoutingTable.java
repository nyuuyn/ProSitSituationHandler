package situationHandling.workflowOperations;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The Class RoutingTable wraps several tables that are used by
 * {@link MessageRouter} to store information about forwarded requests.
 * <p>
 * More precisely, two tables are used. One for the reply address. Here, the
 * original ids of the requests are mapped to the reply address. Another for the
 * original ids. Here the surrogate ids are mapped to the original ids.
 */
class RoutingTable {

    /** The logger. */
    private static final Logger logger = Logger.getLogger(RoutingTable.class);


    /**
     * Stores the reply addresses for workflow requests.
     * <p>
     * Maps the original id to the address.
     * <p>
     * &lt;Original-ID, Reply-Address&gt;
     */
    private Map<String, URL> replyAddresses = Collections.synchronizedMap(new HashMap<>());

    /**
     * Stores the original ids of the requests.
     * <p>
     * Maps the surrogate id, that was assigned to the request, to the original
     * id.
     * <p>
     * &lt;surrogate, original&gt;
     */
    private Map<String, String> surrogateTable = Collections.synchronizedMap(new HashMap<>());

    /**
     * Adds a new reply address to the table.
     *
     * @param messageId
     *            the message id
     * @param target
     *            the reply address
     */
    void addReplyAddress(String messageId, URL target) {
	replyAddresses.put(messageId, target);
    }

    /**
     * Gets the reply address from the table.
     *
     * @param messageId
     *            the message id
     * @return the reply address, , null if no entry exists
     */
    URL getReplyAddress(String messageId) {
	return replyAddresses.get(messageId);
    }

    /**
     * Removes the entry for this message from the table.
     *
     * @param messageId
     *            the message id
     */
    void removeReplyEntry(String messageId) {
	replyAddresses.remove(messageId);
    }

    /**
     * Adds the surrogate for the message id.
     *
     * @param messageId
     *            the message id
     * @param surrogate
     *            the surrogate
     */
    void addSurrogateMessageId(String messageId, String surrogate) {
	surrogateTable.put(surrogate, messageId);
    }

    /**
     * Gets the original message id.
     *
     * @param surrogateId
     *            the surrogate id
     * @return the original message id, , null if no entry exists
     */
    String getOriginalMessageId(String surrogateId) {
	return surrogateTable.get(surrogateId);
    }

    /**
     * Removes the surrogate id.
     *
     * @param surrogate
     *            the surrogate
     */
    void removeSurrogateId(String surrogate) {
	surrogateTable.remove(surrogate);
    }

    /**
     * Prints the routing table in the log.
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
