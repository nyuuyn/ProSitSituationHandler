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
     * The name of the operation to start a rollback of a workflow operation.
     * The operation does not have any parameters.
     */
    static final String ROLLBACK_START_OPERATION = "StartRollback";

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

}
