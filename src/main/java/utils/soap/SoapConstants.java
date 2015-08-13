package utils.soap;

/**
 * The Class SoapConstants contains different constants used for parsing and
 * creating soap messages.
 */
class SoapConstants {

    /** The namespace-prefix used for web service addressing headers. */
    static final String WSA_PREFIX = "wsa";

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
    static final String RELATIONSHIP_TYPE_RESPONSE = WSA_PREFIX + ":Reply";

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
