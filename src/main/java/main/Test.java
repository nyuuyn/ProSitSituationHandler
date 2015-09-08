package main;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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

    private static void prettyPrintMessage(String message, int indent) {
	try {
	    Source xmlInput = new StreamSource(new StringReader(message));
	    StringWriter stringWriter = new StringWriter();
	    StreamResult xmlOutput = new StreamResult(stringWriter);
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    transformerFactory.setAttribute("indent-number", indent);
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.transform(xmlInput, xmlOutput);
	    System.out.println("Received message:\n" + xmlOutput.getWriter().toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
