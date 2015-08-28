
package situationHandling.workflowOperations;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.soap.SOAPException;

import main.GlobalProperties;

import org.apache.log4j.Logger;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import utils.soap.WsaSoapMessage;

/**
 * The Class MessageRouter is used to forward different requests and answers.
 * The message to send is always set in the constructor. If another message is
 * to be forwarded, the message has to be replaced using the setter.
 * <p>
 * It internally manages a routing table to ensure that answers are forwarded to
 * the correct address and so on. The routing table is independent from one
 * instance of Message router, i.e. it is no problem to use different instances
 * to forward the request and the answer.
 * <p>
 * Message Router heavily relies on WS-Addressing. If the ids of the messages
 * and so on are not set according to the WSA Request-Reply pattern, the
 * forwarding will not work in a correct way.
 * <p>
 * Note that the ids of the messages will be replaced with surrogates. The
 * surrogate will be replaced with the original before the answer is sent back
 * to the requestor.
 * <p>
 * The class offers different methods to forward different types of messages.
 * The correct type MUST be used.
 * 
 *
 * @author Stefan
 */
class MessageRouter {

    /** The routing table. */
    private static RoutingTable routingTable = new RoutingTable();

    /** The logger. */
    private static final Logger logger = Logger.getLogger(MessageRouter.class);

    private static final ExecutorService EXECUTOR_SERVICE = Executors
	    .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /** The soap message to send. */
    private WsaSoapMessage wsaSoapMessage;

    /**
     * Instantiates a new message router with a initial message to forward.
     *
     * @param wsaSoapMessage
     *            the soap message that will be sent.
     */
    MessageRouter(WsaSoapMessage wsaSoapMessage) {
	try {
	    this.wsaSoapMessage = new WsaSoapMessage(wsaSoapMessage.getSoapMessage());
	} catch (SOAPException e) {
	    logger.error("Invalid soap message submitted to router", e);
	}
    }

    /**
     * Method to forward a workflow request to an endpoint.
     *
     * @param receiverUrl
     *            the url of the receiver
     * @return the surrogate id that was assigned to this message, null if the
     *         forwarding of the message failed for some reason
     */
    String forwardRequest(URL receiverUrl) {
	logger.trace("Forwarding request: " + wsaSoapMessage.toStringCompact() + " to "
		+ receiverUrl.toString());
	URL answerRecipent = wsaSoapMessage.getWsaReplyTo();
	// set new answer address
	try {
	    String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
	    wsaSoapMessage.setWsaReplyTo(new URL("http://" + ownIPAdress + ":"
		    + GlobalProperties.NETWORK_PORT + "/" + GlobalProperties.ANSWER_ENDPOINT_PATH));
	} catch (MalformedURLException | UnknownHostException e) {
	    logger.error("Could not create answer address", e);
	    return null;
	}

	// set To address
	wsaSoapMessage.setWsaTo(receiverUrl);
	UUID surrogate = UUID.randomUUID();
	String originalId = wsaSoapMessage.getWsaMessageID();
	wsaSoapMessage.setWsaMessageId(surrogate.toString());

	// set SOAPAction header (if available)
	HashMap<String, String> headers = new HashMap<>();
	if (wsaSoapMessage.getWsaAction() != null) {
	    headers.put("SOAPAction", wsaSoapMessage.getWsaAction());
	}
	headers.put("Content-Type", "text/xml");

	// send message
	if (sendMessage(receiverUrl, wsaSoapMessage.getSoapMessage(), headers)) {
	    routingTable.addReplyAddress(originalId, answerRecipent);
	    routingTable.addSurrogateMessageId(originalId, surrogate.toString());
	    routingTable.printRoutingTable();
	    return surrogate.toString();
	}
	return null;
    }

    /**
     * Forward a rollback request, in case of a situation change that requires a
     * rollback. The message will be sent to the address that is specified in
     * the wsa:To header of the message.
     *
     * @return true, if successful, false if the forwarding failed.
     */
    boolean forwardRollbackRequest() {
	logger.trace("Forwarding rollback request: " + wsaSoapMessage.toStringCompact());

	// remove the old entry in the surrogate table
	routingTable.removeSurrogateId(wsaSoapMessage.getWsaRelatesTo());

	URL receiverUrl = wsaSoapMessage.getWsaTo();

	// set soapAction header (if available)
	HashMap<String, String> headers = new HashMap<>();
	if (wsaSoapMessage.getWsaAction() != null) {
	    headers.put("SOAPAction", wsaSoapMessage.getWsaAction());
	}

	routingTable.printRoutingTable();

	return sendMessage(receiverUrl, wsaSoapMessage.getSoapMessage(), headers);
    }

    /**
     * Forward the answer of a successful workflow operation to the requester.
     *
     * @return true, if successful, false if the forwarding failed
     */
    boolean forwardAnswer() {
	logger.trace("Forwarding answer: " + wsaSoapMessage.toStringCompact());
	// lookup original id
	String surrogateId = wsaSoapMessage.getWsaRelatesTo();
	String originalId = routingTable.getOriginalMessageId(surrogateId);
	if (originalId == null) {
	    logger.warn("No message id found for surrogate: " + wsaSoapMessage.getWsaRelatesTo());
	    return false;
	}
	wsaSoapMessage.setWsaRelatesTo(originalId);

	// get receiver
	URL receiver = routingTable.getReplyAddress(originalId);
	if (receiver == null) {
	    logger.warn("No receiver found for message with id: " + originalId);
	    return false;
	}

	// set receiver
	wsaSoapMessage.setWsaTo(receiver);

	// set soapAction header (if available)
	HashMap<String, String> headers = new HashMap<>();
	if (wsaSoapMessage.getWsaAction() != null) {
	    headers.put("SOAPAction", wsaSoapMessage.getWsaAction());
	}
	headers.put("Content-Type", "text/xml");

	// remove entries in routing table
	routingTable.removeReplyEntry(originalId);
	routingTable.removeSurrogateId(surrogateId);
	routingTable.printRoutingTable();

	return sendMessage(receiver, wsaSoapMessage.getSoapMessage(), headers);
    }

    /**
     * Forward a fault message, if the processing of a message failed. The
     * parameter surrogate id relates to the surrogate that was assigned to the
     * message when sending it. Null can be used, when no surrogate was assigned
     * yet.
     *
     * @param surrogateId
     *            the surrogate id
     */
    void forwardFaultMessage(String surrogateId) {

	if (surrogateId != null) {
	    // remove the old entry in the surrogate table
	    routingTable.removeSurrogateId(surrogateId);
	    routingTable.removeReplyEntry(wsaSoapMessage.getWsaRelatesTo());
	    routingTable.printRoutingTable();
	}

	// set soapAction header (if available)
	HashMap<String, String> headers = new HashMap<>();
	if (wsaSoapMessage.getWsaAction() != null) {
	    headers.put("SOAPAction", wsaSoapMessage.getWsaAction());
	}

	if (!sendMessage(wsaSoapMessage.getWsaTo(), wsaSoapMessage.getSoapMessage(), headers)) {
	    logger.error("Error sending Fault message...");
	}
    }

    /**
     * Helper method that does the sending of the message. Sending in this case
     * means that a HTTP-POST operation is performed at the stated url.
     *
     * @param url
     *            the url of the receiver
     * @param payload
     *            the payload of the message
     * @param headers
     *            the http headers to send with the request. Use the key as the
     *            header and the value as the value of the header.
     * @return true when successful, false else
     */
    private boolean sendMessage(URL url, String payload, Map<String, String> headers) {
	PluginManager pm = PluginManagerFactory.getPluginManager();
	// set params
	PluginParams params = new PluginParams();
	params.setParam("Http method", "POST");
	// http headers
	StringBuilder httpHeaders = new StringBuilder();
	Iterator<String> keyIterator = headers.keySet().iterator();
	while (keyIterator.hasNext()) {
	    String key = keyIterator.next();
	    httpHeaders.append(key + ":" + headers.get(key));
	    if (keyIterator.hasNext()) {
		httpHeaders.append("$");
	    }
	}
	params.setParam("Request Headers", httpHeaders.toString());

	Map<String, String> results = null;

	System.out.println("Sending message:\n" + payload);

	try {
	    results = EXECUTOR_SERVICE.submit(
		    pm.getPluginSender("situationHandler.http", url.toString(), payload, params))
		    .get();
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Error when invoking Endpoint.", e);
	    return false;
	}

	if (Boolean.parseBoolean(results.get("success"))) {
	    logger.trace("Success invoking Endpoint. Result: " + results.get("message"));
	    return true;
	} else {
	    logger.debug("Invoking Endpoint failed. Result: " + results.get("message"));
	    return false;
	}
    }

    /**
     * Set a new message to send
     * 
     * @param wsaSoapMessage
     */
    void setWsaSoapMessage(WsaSoapMessage wsaSoapMessage) {
	this.wsaSoapMessage = wsaSoapMessage;
    }

    /**
     * Message Router Cleanup.
     */
    static void shutdown() {
	EXECUTOR_SERVICE.shutdown();
    }

}
