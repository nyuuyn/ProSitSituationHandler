package main;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;

public class CamelUtil {

	//TODO: Das ganze hier ist doch noch etwas unsch�n
	
	private static ProducerTemplate producerTemplate;
	private static ConsumerTemplate consumerTemplate;
	
	static void initProducerTemplate (ProducerTemplate producerTemplate){
		CamelUtil.producerTemplate = producerTemplate;
	}
	static void initConsumerTemplate (ConsumerTemplate consumerTemplate){
		CamelUtil.consumerTemplate = consumerTemplate;
	}
	
	
	public static ProducerTemplate getProducerTemplate(){
		return producerTemplate;
	}
	
	public static ConsumerTemplate getConsumerTemplate(){
		return consumerTemplate;
	}
}
