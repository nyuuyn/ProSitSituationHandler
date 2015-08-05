/**
 * 
 */
package situationHandling;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

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

	private SoapMessage soapMessage;

	/**
	 * @param soapMessage
	 */
	MessageRouter(SoapMessage soapMessage) {
		this.soapMessage = soapMessage;
	}

	boolean forwardRequest(URL receiverUrl) {
		URL answerRecipent = soapMessage.getWsaReplyTo();
		// set new answer address
		try {
			String ownIPAdress = InetAddress.getLocalHost().getHostAddress();
			soapMessage.setWsaReplyTo(new URL("http://" + ownIPAdress + ":"
					+ GlobalProperties.NETWORK_PORT + "/"
					+ GlobalProperties.ANSWER_ENDPOINT_PATH));
		} catch (MalformedURLException | UnknownHostException e) {
			logger.error("Could not create answer address", e);
			return false;
		}

		// set To address
		soapMessage.setWsaTo(receiverUrl);

		// send message
		if (sendMessage(receiverUrl, soapMessage.getSoapMessage())) {
			routingTable.addReplyAddress(soapMessage.getWsaMessageID(),
					answerRecipent);
			System.out.println("Reply to: " + soapMessage.getWsaReplyTo());
			return true;
		}
		return false;
	}

	boolean forwardAnswer() {
		// get receiver
		URL receiver = routingTable.getReplyAddress(soapMessage
				.getWsaRelatesTo());
		if (receiver == null) {
			System.out.println("No receiver");
			return false;
		}

		System.out.println("Receiver:" + receiver.toString());

		// set receiver
		soapMessage.setWsaTo(receiver);

		return sendMessage(receiver, soapMessage.getSoapMessage());
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
			// TODO: Das Exception Handling hier bringt nix --> die exception
			// muss schon gescheit vom plugin behandelt werden!
			results = pm.getPluginSender("situationHandler.http",
					url.toString(), payload, params).call();
			System.out.println("Results:" + results);
		} catch (Exception e) {
			logger.error("Error when invoking Endpoint.", e);
			return false;
		}

		logger.debug("Success invoking Endpoint. Result: "
				+ results.get("body"));

		return true;
	}

	/**
	 * @return the routingTable
	 */
	static OperationRoutingTable getRoutingTable() {
		return routingTable;
	}

}