/**
 * This package provides the implementation of the workflow operation handling.
 * <p>
 * The workflow operations are forwarded to a situation dependent endpoint. Only
 * endpoints that were stored before are considered. When a situation changes
 * while an operation is still in progress, a rollback is initiated and another
 * endpoint is chosen. The maximum number of retries can be specified with the
 * request from the workflow. When this number is reached or another fault
 * occurs, a fault is sent back to the requesting workflow.
 * <p>
 * It is assumed, that the workflow uses soap messages. All communication is
 * assumed to be asynchronous. WS-Adressing is used to relate messages to other
 * messages, i.e. to realize a request-reply pattern.
 * 
 * <h1>Usage</h1>
 * 
 * The Interface {@link situationHandling.workflowOperations.OperationHandler}
 * exposes the functionality of this component. It allows to submit workflow
 * requests, answers and situation changes. An instance of the component can be
 * retrieved using
 * {@link situationHandling.workflowOperations.OperationHandlerFactory}.
 * <p>
 * The number of maximum retries after a rollback can be specified using a
 * custom soap header: <br>
 * &lt;MaxRetries soapenv:actor = &quot;Situation_Handler&quot;&gt;
 * Number&lt;/MaxRetries&gt; <br>
 * The actor MUST be set to Situation_Handler. "Number" can be any number >=0
 * 
 * @author Stefan
 *
 */
package situationHandling.workflowOperations;