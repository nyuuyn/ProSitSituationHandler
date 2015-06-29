package routes;

import org.apache.camel.ProducerTemplate;

public class CamelUtil {

	//TODO: Das ganze hier ist doch noch etwas unschön
	
	private static ProducerTemplate producerTemplate;
	
	static void initProducerTemplate (ProducerTemplate producerTemplate){
		CamelUtil.producerTemplate = producerTemplate;
	}
	
	
	public static ProducerTemplate getProducerTemplate(){
		return producerTemplate;
	}
}
