package utils.soap;

class SoapConstants {
	
	static final String WSA_PREFIX = "wsa";

	static final String RELATIONSHIP_TYPE_ROLLBACK = "Rollback";
	static final String RELATIONSHIP_TYPE_RESPONSE = WSA_PREFIX + ":Reply";
	
	static final String ROLLBACK_RESPONSE_RELATIONSHIP_TYPE = "RollbackResponse";
	static final String ROLLBACK_START_OPERATION = "StartRollback";
	
	static final String SITUATION_HANDLER_ROLE = "Situation_Handler";
	static final String HEADER_MAX_RETRIES = "MaxRetries";

}
