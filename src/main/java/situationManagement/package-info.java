/**
 * 
 * This package implements the situation management component, i.e. it provides
 * the functionality, that is required for the interaction with the Stituation
 * Recognion System/SitOpt. More precisely it offers the functionality to:
 * 
 * <ol>
 * <li>Query the state of a situation from the SRS</li>
 * <li>Subscribe on situation changes</li>
 * <li>Unsubscribe from situation changes</li>
 * </ol>
 * 
 * Furthermore, it is possible to enable a cache of arbitrary size for the state
 * of situations. The cache size can be set by the user. When the cache is
 * filled a replacement candidate will be determined via the LRU-strategy. If
 * the cache is enabled, queries to the SRS can possibly be answered by the
 * cache, i.e. enabling the cache reduces the communication overhead. The cache
 * is always kept up-to-date, so the cache will not deliver outdated situations.
 * <p>
 * The situation management component will also deliver updates about situations
 * from the SRS to the situation handling/operation handling.
 * <p>
 * The situation management manages the subscriptions to situation changes in an
 * intelligent way, i.e. there won't be double subscriptions and subscriptions
 * are only deleted, when they become obsolete. Intended usage of the
 * subscription mechanism is, that a subscription is made for each {@code Rule}
 * and each {@code HandledSituation} by each {@code Endpoint}. So subscribe
 * should be called when adding new rules/handled situations and unsubscribe
 * should be called when removing them. Initially, the storage is scanned for
 * rules and handled situations and the necessary subscriptions are created.
 * 
 * <h1>Usage</h1>
 * 
 * The functionality of the Situation Management Component can be accessed via
 * the {@link situationManagement.SituationManager} Interface. To get
 * implementations of the Interface, the
 * {@link situationManagement.SituationManagerFactory} must be used. The Factory
 * can also be used to configure the component. For example the cache can be
 * controlled via the factory.
 * 
 * @author Stefan
 * 
 * @see situationManagement.SituationManager
 * @see situationManagement.SituationManagerFactory
 * 
 * @see situationHandling.storage.datatypes.Endpoint
 * @see situationHandling.storage.datatypes.Rule
 * @see situationHandling.storage.datatypes.HandledSituation
 *
 */
package situationManagement;