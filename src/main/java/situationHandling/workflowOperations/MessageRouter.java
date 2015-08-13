/**
 * 
 */
package situationHandling.workflowOperations;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

import javax.xml.soap.SOAPException;

import main.GlobalProperties;

import org.apache.log4j.Logger;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;
import utils.soap.WsaSoapMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class MessageRouter.
 *
 * @author Stefan
 */
class MessageRouter {

	/** The routing table. */
	private static RoutingTable routingTable = new RoutingTable();

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(MessageRouter.class);

	/** The wsa soap message. */
	private WsaSoapMessage wsaSoapMessage;

	/**
	 * Instantiates a new message router.
	 *
	 * @param wsaSoapMessage the wsa soap message
	 */
	MessageRouter(WsaSoapMessage wsaSoapMessage) {
		try {
			this.wsaSoapMessage = new WsaSoapMessage(
					wsaSoapMessage.getSoapMessage());
		} catch (SOAPException e) {
			logger.error("Invalid soap message submitted to router", e);
		}
	}

	/**
	 * Forward request.
	 *
	 * @param receiverUrl the receiver url
	 * @return the string
	 */
	String forwardRequest(URL receiverUrl) {
		logger.trace("Forwarding request: " + wsaSoapMessage.toStringCompact()
				+ " to " + receiverUrl.toString());
		URL answerRecipent = wsaSoapMessage.getWsaReplyTo();
		// set new answer address
		try {
			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
			wsaSoapMessage.setWsaReplyTo(new URL("http://" + ownIPAdress + ":"
					+ GlobalProperties.NETWORK_PORT + "/"
					+ GlobalProperties.ANSWER_ENDPOINT_PATH));
		} catch (MalformedURLException | UnknownHostException e) {
			logger.error("Could not create answer address", e);
			return null;
		}

		// set To address
		wsaSoapMessage.setWsaTo(receiverUrl);
		UUID surrogate = UUID.randomUUID();
		String originalId = wsaSoapMessage.getWsaMessageID();
		wsaSoapMessage.setWsaMessageId(surrogate.toString());

		// send message
		if (sendMessage(receiverUrl, wsaSoapMessage.getSoapMessage())) {
			routingTable.addReplyAddress(originalId, answerRecipent);
			routingTable
					.addSurrogateMessageId(originalId, surrogate.toString());
			routingTable.printRoutingTable();
			return surrogate.toString();
		}
		return null;
	}

	/**
	 * Forward rollback request.
	 *
	 * @return true, if successful
	 */
	boolean forwardRollbackRequest() {
		logger.trace("Forwarding rollback request: "
				+ wsaSoapMessage.toStringCompact());

		// remove the old entry in the surrogate table
		routingTable.removeSurrogateId(wsaSoapMessage.getWsaRelatesTo());

		URL receiverUrl = wsaSoapMessage.getWsaTo();

		routingTable.printRoutingTable();
		return sendMessage(receiverUrl, wsaSoapMessage.getSoapMessage());
	}

	/**
	 * Forward answer.
	 *
	 * @return true, if successful
	 */
	boolean forwardAnswer() {
		logger.trace("Forwarding answer: " + wsaSoapMessage.toStringCompact());
		// lookup original id
		String surrogateId = wsaSoapMessage.getWsaRelatesTo();
		String originalId = routingTable.getOriginalMessageId(surrogateId);
		if (originalId == null) {
			logger.warn("No message id found for surrogate: "
					+ wsaSoapMessage.getWsaRelatesTo());
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

		// remove entries in routing table
		routingTable.removeReplyEntry(originalId);
		routingTable.removeSurrogateId(surrogateId);
		routingTable.printRoutingTable();

		return sendMessage(receiver, wsaSoapMessage.getSoapMessage());
	}

	/**
	 * Forward fault message.
	 *
	 * @param surrogateId the surrogate id
	 */
	void forwardFaultMessage(String surrogateId) {

		if (surrogateId != null) {
			// remove the old entry in the surrogate table
			routingTable.removeSurrogateId(surrogateId);
			routingTable.removeReplyEntry(wsaSoapMessage.getWsaRelatesTo());
			routingTable.printRoutingTable();
		}

		if (!sendMessage(wsaSoapMessage.getWsaTo(), wsaSoapMessage.getSoapMessage())) {
			logger.error("Error sending Fault message...");
		}
	}

	/**
	 * Send message.
	 *
	 * @param url the url
	 * @param payload the payload
	 * @return true when successful, false else
	 */
	private boolean sendMessage(URL url, String payload) {
		PluginManager pm = PluginManagerFactory.getPluginManager();
		PluginParams params = new PluginParams();

		params.setParam("Http method", "POST");
		Map<String, String> results = null;
		try {
			// TODO: Das sollte man eigentlich in einem Pool machen!
			results = pm.getPluginSender("situationHandler.http",
					url.toString(), payload, params).call();
		} catch (Exception e) {
			logger.error("Error when invoking Endpoint.", e);
			return false;
		}

		if (Boolean.parseBoolean(results.get("success"))) {
			logger.trace("Success invoking Endpoint. Result: "
					+ results.get("message"));
			return true;
		} else {
			logger.debug("Invoking Endpoint failed. Result: "
					+ results.get("message"));
			return false;
		}

	}

}
