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
	    message = new WsaSoapMessage(
		    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rol=\"SituationHandler/RollbackMessages/\">\r\n\t<soapenv:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\r\n\t\t<wsa:MessageID>required</wsa:MessageID>\r\n\t   \t<wsa:Action>situationHandler.bpelDemo.targetWorkflow1Artifacts/StartRollbackCallback</wsa:Action>\r\n\t   \t<wsa:To>http://template</wsa:To>\r\n\t   \t<wsa:RelatesTo RelationshipType = \"RollbackResponse\">required</wsa:RelatesTo>\r\n\t   \t<odesession:session xmlns:odesession=\"http://www.apache.org/ode/type/session\">required</odesession:session>\r\n    \t<intalio:session xmlns:intalio=\"http://www.intalio.com/type/session\">required</intalio:session>\r\n\t</soapenv:Header>\r\n   <soapenv:Body>\r\n      <rol:RollbackResponseELement>\r\n         <RelatedRequestId>(not used)</RelatedRequestId>\r\n         <RollbackResult>true</RollbackResult>\r\n      </rol:RollbackResponseELement>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>\r\n");
	    prettyPrintMessage(message.getSoapMessage(), 3);
	    System.out.println(message.toString());
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
