package main;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.soap.SOAPConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import utils.soap.SoapRequestFactory;
import utils.soap.WsaSoapMessage;

public class Test {

    public static void main(String[] args) {

	WsaSoapMessage message;

	message = SoapRequestFactory.createFaultMessageWsa("http://lcoashta", "123",
		"Unknownk error", SOAPConstants.SOAP_SENDER_FAULT);
	prettyPrintMessage(message.getSoapMessage(), 3);
	System.out.println(message.toString());

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
