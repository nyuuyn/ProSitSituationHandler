package main;

import java.util.concurrent.ExecutorService;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;

/**
 * The Class CamelUtil is a utility class to access the camel context (and other
 * camel components) in the application.
 */
public class CamelUtil {

    /** The producer template. */
    private static ProducerTemplate producerTemplate;

    /** The consumer template. */
    private static ConsumerTemplate consumerTemplate;

    /** The executor service. */
    private static ExecutorService executorService;

    /** The camel context. */
    private static CamelContext context;

    /**
     * Inits the producer template.
     *
     * @param producerTemplate
     *            the producer template
     */
    static void initProducerTemplate(ProducerTemplate producerTemplate) {
	CamelUtil.producerTemplate = producerTemplate;
    }

    /**
     * Inits the consumer template.
     *
     * @param consumerTemplate
     *            the consumer template
     */
    static void initConsumerTemplate(ConsumerTemplate consumerTemplate) {
	CamelUtil.consumerTemplate = consumerTemplate;
    }

    /**
     * Inits the executor service.
     *
     * @param executorService
     *            the executor service
     */
    static void initExecutorService(ExecutorService executorService) {
	CamelUtil.executorService = executorService;
    }

    /**
     * Inits the camel context.
     *
     * @param context
     *            the context
     */
    static void initCamelContext(CamelContext context) {
	CamelUtil.context = context;
    }

    /**
     * Gets the camel context. Do not use the context to create new templates,
     * thread pools etc. Instead use the respective methods!
     *
     * @return the camel context
     */
    public static CamelContext getCamelContext() {
	return context;
    }

    /**
     * Gets the producer template.
     *
     * @return the producer template
     */
    public static ProducerTemplate getProducerTemplate() {
	return producerTemplate;
    }

    /**
     * Gets the consumer template.
     *
     * @return the consumer template
     */
    public static ConsumerTemplate getConsumerTemplate() {
	return consumerTemplate;
    }

    /**
     * Gets the camel executor service.
     *
     * @return the camel executor service
     */
    public static ExecutorService getCamelExecutorService() {
	return executorService;
    }
}
