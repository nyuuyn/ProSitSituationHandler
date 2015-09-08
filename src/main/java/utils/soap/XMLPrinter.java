package utils.soap;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Utility Class to pretty print xml messages.
 * 
 * @author Stefan
 *
 */
public class XMLPrinter {

    /**
     * Prints a xml message to console. Considers correct indentation and so on.
     * 
     * 
     * @param message
     *            The message to print
     * @param indent
     *            the number of spaces used for indentation.
     */
    public static void prettyPrintMessage(String message, int indent) {
	System.out.println("XML message:\n" + getPrettyXMLString(message, indent));
    }

    /**
     * Transforms a xml string to a nicely readable string, good for printing,
     * logs ...
     * 
     * @param message
     *            the xml string to transform
     * @param indent
     *            the number of spaces used for indentation
     * @return a nicely readable String
     */
    public static String getPrettyXMLString(String message, int indent) {
	try {
	    Source xmlInput = new StreamSource(new StringReader(message));
	    StringWriter stringWriter = new StringWriter();
	    StreamResult xmlOutput = new StreamResult(stringWriter);
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    transformerFactory.setAttribute("indent-number", indent);
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.transform(xmlInput, xmlOutput);
	    return xmlOutput.getWriter().toString();
	} catch (TransformerException e) {
	    e.printStackTrace();
	    return null;
	}
    }

}
