package main;

import java.util.concurrent.ExecutorService;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;

public class CamelUtil {
	
	private static ProducerTemplate producerTemplate;
	private static ConsumerTemplate consumerTemplate;
	private static ExecutorService executorService;
	
	static void initProducerTemplate (ProducerTemplate producerTemplate){
		CamelUtil.producerTemplate = producerTemplate;
	}
	static void initConsumerTemplate (ConsumerTemplate consumerTemplate){
		CamelUtil.consumerTemplate = consumerTemplate;
	}
	
	static void initExecutorService (ExecutorService executorService){
	    CamelUtil.executorService = executorService;
	}
	
	
	public static ProducerTemplate getProducerTemplate(){
		return producerTemplate;
	}
	
	public static ConsumerTemplate getConsumerTemplate(){
		return consumerTemplate;
	}
	
	public static ExecutorService getCamelExecutorService(){
	    return executorService;
	}
}
