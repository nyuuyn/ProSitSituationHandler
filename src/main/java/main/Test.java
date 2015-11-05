package main;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Port;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;

public class Test {

    // TODO Remove

    public static void main(String[] args) {
	WSDLParser parser = new WSDLParser();

	Definitions defs = parser.parse("http://asdsadasd:8080/ode/processes/TargetWorkflow1?wsdl");

	String portAddress = null;

	for (Service service : defs.getServices()) {
	    for (Port port : service.getPorts()) {
		portAddress = port.getAddress().getLocation();
		break;
	    }
	}
	System.out.println("Port Address: " + portAddress);

    }

}
