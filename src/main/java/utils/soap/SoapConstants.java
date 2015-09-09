package utils.soap;

/**
 * The Class SoapConstants contains different constants used for parsing and
 * creating soap messages.
 */
class SoapConstants {

    /** The namespace-prefix used for web service addressing headers. */
    static final String DEFAULT_WSA_PREFIX = "wsa";

    /**
     * The URI used in soap messages to declare the wsa namespace.
     */
    static final String WSA_URI = "http://www.w3.org/2005/08/addressing";

    /**
     * The Uri used in wsa:ReplyTo headers to declare that there is no answer
     * required.
     */
    static final String NO_REPLY_URI = "http://www.w3.org/2005/08/addressing/none";

    /**
     * The relationship type for rollback requests. The relationship type is an
     * attribute of the wsa header "relates to".
     */
    static final String RELATIONSHIP_TYPE_ROLLBACK = "Rollback";

    /**
     * The relationship type for answers to asynchronous requests. Conform to
     * the WSA-Standard. The relationship type is an attribute of the wsa header
     * "relates to"
     */
    static final String RELATIONSHIP_TYPE_RESPONSE = DEFAULT_WSA_PREFIX + ":Reply";

    /**
     * The relationship type for rollback responses. The relationship type is an
     * attribute of the wsa header "relates to"
     */
    static final String ROLLBACK_RESPONSE_RELATIONSHIP_TYPE = "RollbackResponse";

    /**
     * The element in rollback messages that contains the id of the request that
     * rollback message relates to.
     */
    static final String ROLLBACK_MESSAGE_RELATED_ID_ELEMENT = "RelatedRequestId";

    /**
     * The name of the "main-element" in a rollback request.
     */
    static final String ROLLBACK_START_OPERATION_ELEMENT = "RollbackRequestElement";

    /**
     * The element in a Rollback response message that contains the result of
     * the rollback.
     */
    static final String ROLLBACK_MESSAGE_SUCCESS_ELEMENT = "RollbackResult";

    /**
     * The Element in a Rollback Result Message containing the response.
     */
    static final String ROLLBACK_RESPONSE_ELEMENT = "RollbackResponseElement";

    /**
     * The namespace that was used to define the rollback messages.
     * 
     */
    static final String ROLLBACK_MESSAGE_NAMESPACE = "SituationHandler/RollbackMessages/";

    /**
     * The role of the situation handler as stated in soap requests. The
     * situation Handler only processes headers with the actor attribute set to
     * this value.
     */
    static final String SITUATION_HANDLER_ROLE = "Situation_Handler";

    /**
     * The name of the soap header element that states the maximum number of
     * retries for a workflow operation.
     */
    static final String HEADER_MAX_RETRIES = "MaxRetries";

    /**
     * The name of the header in a request message, that specifies the
     * correlation id of the fault message.
     */
    static final String FAULT_CORRELATION_HEADER_NAME = "FaultCorrelationInfo";

}
