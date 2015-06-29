/**
 * This package contains the classes that are used to store and access endpoints and rules for the situation handling. Rules and endpoints are stored in a persistent way. 
 * <p>
 * A rule always consists of the situation the rule applies to and an action that is executed when the situation occurs. An endpoint is usually a SOAP endpoint to which soap messages from workflows can be directed to.
 * <div>
 * Main purpose of the endpoint storage/directory is to provide an endpoint that offers an certain operation depending on the current situation. Furthermore manipulation of the list of existing endpoints is possible.
* Main purpose of the rule storage is to provide a list of actions depending on a certain situation, i.e. the rules for handling situations. Furthermore manipulation of the list of existing rules is possible.
 * 
 * 
 * <p>
 *Endpoints and rules are stored separately. Therefore, there are separate interfaces to access the storage for rules and the storage for endpoints. Instances of the implementing classes can be created using the factory ({@link StorageAccessFactory}). All interaction with the storage should be done using the interfaces {@link RuleStorageAccess} to access and store rules and {@link EndpointStorageAccess} to access and store Endpoints.<br>
 *
 *
 *
 * @author Stefan
 * @see situationHandling.storage.datatypes.Endpoint
 * @see situationHandling.storage.datatypes.Rule
 */
package situationHandling.storage;