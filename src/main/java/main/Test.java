package main;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Test {

    public static void main(String[] args) {

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
			    System.out.println("Headers:" + exchange.getIn().getHeaders().toString());
			}
		    });
		}
	    });
	    JndiRegistry registry = context.getRegistry(JndiRegistry.class);
	    ProducerTemplate template = context.createProducerTemplate();
	    context.start();

	    File file = new File("C:\\HelloWorldBPEL.zip");

	    Part[] parts = { new StringPart("comment", "A binary file of some kind"),
		    new FilePart("file", file) };

	    MultipartRequestEntity asfasf = new MultipartRequestEntity(parts,
		    new HttpMethodParams());

	    String asdasd = template.requestBody("http://0.0.0.0:8083/httptest?proxyHost=localhost&proxyPort=8888", asfasf,
		    String.class);
	    System.out.println("Answer: " + asdasd);
	} catch (Exception e1) {
	    e1.printStackTrace();
	}

	System.out.println("asgasgasgasg");
	MultipartRequestEntity asdasd;
    }

}
