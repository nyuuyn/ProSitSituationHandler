package restApiImpl;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;

import situationHandling.storage.HistoryAccess;
import situationHandling.storage.StorageAccessFactory;



/**
 * The Class HistoryAPI implements the Api that provides history information.
 *
 * @author Stefan
 */
public class HistoryAPI {

	/** The access to the history information. */
	private HistoryAccess historyAccess;

	/**
	 * Instantiates a new history api.
	 */
	public HistoryAPI() {
		historyAccess = StorageAccessFactory.getHistoryAccess();
	}

	/**
	 * Gets the history. Allows to specify the number of entries to get.
	 *
	 * @param exchange
	 *            the exchange that contains the received message. Also serves
	 *            as container for the answer. Must contain the parameters
	 *            {@code offset} and {@code entries}
	 * @return the history in the specified range. A 400 error if illegal
	 *         arguments were passed to the method.
	 */
	public void getHistory(Exchange exchange) {
		try {
			int offset = (int) exchange.getIn().getHeader("offset", Integer.class);
			int numberOfEntries = (int) exchange.getIn().getHeader("entries",
					Integer.class);

			exchange.getIn().setBody(
					historyAccess.getHistory(offset, numberOfEntries));
			exchange.getIn().setHeader("history_size", historyAccess.getHistorySize());
		} catch (TypeConversionException | IllegalArgumentException e) {
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			exchange.getIn().setBody(e.getMessage(), String.class);
		}
	}
}
