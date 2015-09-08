package main;

import javax.xml.soap.SOAPException;

import utils.soap.WsaSoapMessage;

public class Test {

    public static void main(String[] args) {

	WsaSoapMessage message;

	try {
	    message = new WsaSoapMessage("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sit=\"situationHandler.bpelDemo.Common\">\r\n   <soapenv:Header/>\r\n   <soapenv:Body>\r\n      <sit:doSomething>\r\n         <sit:in>?</sit:in>\r\n      </sit:doSomething>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>");
	System.out.println("Message: " + message.toString());
	} catch (SOAPException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	
    }

}
