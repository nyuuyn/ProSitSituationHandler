/**
 * 
 */
package situationHandling;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import routes.GlobalProperties;
import situationHandler.plugin.PluginParams;

/**
 * @author Stefan
 *
 */
class MessageRouter {

	private static OperationRoutingTable routingTable = new OperationRoutingTable();

	private static final Logger logger = Logger.getLogger(MessageRouter.class);

	private WsaSoapMessage wsaSoapMessage;

	/**
	 * @param wsaSoapMessage
	 */
	MessageRouter(WsaSoapMessage wsaSoapMessage) {//TODO: Das ist komisch, wenn man verschiedene Nachrichten forwarden will..
		this.wsaSoapMessage = wsaSoapMessage;
	}

	boolean forwardRequest(URL receiverUrl) {
		URL answerRecipent = wsaSoapMessage.getWsaReplyTo();
		// set new answer address
		try {
			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
			wsaSoapMessage.setWsaReplyTo(new URL("http://" + ownIPAdress + ":"
					+ GlobalProperties.NETWORK_PORT + "/"
					+ GlobalProperties.ANSWER_ENDPOINT_PATH));
		} catch (MalformedURLException | UnknownHostException e) {
			logger.error("Could not create answer address", e);
			return false;
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
			return true;
		}
		return false;
	}

	boolean forwardAnswer() {
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

		return sendMessage(receiver, wsaSoapMessage.getSoapMessage());
	}

	boolean forwardRollbackRequest(){
		
		routingTable.removeSurrogateId(wsaSoapMessage.getWsaRelatesTo());
		
		return false;
	}

	void rollbackResponseReceived(String messageId) {
		routingTable.removeSurrogateId(messageId);
	}

	/**
	 * 
	 * @param endpoint
	 * @param payload
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
			logger.debug("Success invoking Endpoint. Result: "
					+ results.get("message"));
			return true;
		} else {
			logger.debug("Invoking Endpoint failed. Result: "
					+ results.get("message"));
			return false;
		}

	}

	// /**
	// * @return the routingTable
	// */
	// static OperationRoutingTable getRoutingTable() {
	// return routingTable;
	// }

}
