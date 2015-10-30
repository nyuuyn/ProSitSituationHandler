package main;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpOperationFailedException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Test {

    // TODO: Properties
    private static final String deploymentAddress = "192.168.209.247:8080";

    private static ProducerTemplate template;

    public static void main(String[] args) {

	// TODO: Proxy Raus

	// http://grokbase.com/t/camel/users/146c2rnzrw/camel-2-12-1-how-to-send-http-post-with-an-attachment-using-producertemplate

	CamelContext context = new DefaultCamelContext();
	try {
	    context.addRoutes(new RouteBuilder() {

		@Override
		public void configure() throws Exception {
		    from("jetty:http://0.0.0.0:8083/httptest").to("stream:out")
			    .process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
			    System.out.println("processing");
			    System.out
				    .println("Headers:" + exchange.getIn().getHeaders().toString());
			}
		    });
		}
	    });

	    JndiRegistry registry = context.getRegistry(JndiRegistry.class);
	    template = context.createProducerTemplate();
	    context.start();

	    // Start packing
	    File fragment = new File("C:\\HelloWorldBPEL.zip");
	    String location = packFragment(fragment);

	    waitForCompletion(location);

	    String archiveDownloadAddress = location + "/download";
	    System.out.println("Download Address: " + archiveDownloadAddress);

	} catch (Exception e1) {
	    e1.printStackTrace();
	}
    }

    private static String packFragment(File fragment) {
	try {
	    Part[] parts = {
		    // TODO: Properties
		    new StringPart("artifactType",
			    "{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPEL"),
		    new FilePart("file", fragment) };
	    MultipartRequestEntity multipartRequest = new MultipartRequestEntity(parts,
		    new HttpMethodParams());

	    template.requestBody(
		    "http://" + deploymentAddress
			    + "/XaaSPackager/package?proxyHost=localhost&proxyPort=8888",
		    multipartRequest, Object.class);

	} catch (FileNotFoundException e) {
	    // TODO
	    System.out.println("File not found");
	    return null;
	} catch (CamelExecutionException e) {
	    Throwable cause = e.getCause();
	    if (cause instanceof HttpOperationFailedException) {
		// TODO: nutzlosen mist raus
		HttpOperationFailedException httpException = (HttpOperationFailedException) cause;
		System.out.println("Header: " + httpException.getResponseHeaders().toString());
		String location = httpException.getResponseHeaders().get("Location");
		System.out.println("Location: " + location);
		return location;
	    } else {
		e.printStackTrace();
		System.out.println("Klasse" + e.getClass());
	    }

	}
	return null;
    }

    private static void waitForCompletion(String location) {
	// query packaging status
	String answer = "notPolled";
	while (!answer.equalsIgnoreCase("PACKAGED")) {
	    try {
		Thread.sleep(10_000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    answer = template.requestBody(location + "/state", null, String.class);
	    System.out.println("Answer: " + answer);
	}

	System.out.println("Packaging completed..");
    }

    private static void deployFragment(String csarAddress) {
	String containerAddress = "http://localhost:1337/containerapi";
	String sshPrivateKeyPath = "C:\\stefankallecollabo.pem";
    }

}
