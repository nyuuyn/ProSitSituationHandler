package utils.soap;

import javax.xml.soap.SOAPException;

public class WsaRollbackMessage extends WsaSoapMessage {

    public WsaRollbackMessage(String soapString, String blubb) throws SOAPException {
	super(soapString);
    }

}
