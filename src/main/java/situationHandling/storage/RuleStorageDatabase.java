package situationHandling.storage;

/**
 * This interface does just the same than {@link RuleStorageAccess}. However it
 * is used to differentiate between classes that do plain database access and
 * classes do more than just writing and reading from the database.
 * <p>
 * So, classes that focus more on accessing the database should implement
 * {@code RuleStorageDatabase} and classes, that focus on other tasks should
 * implement {@code RuleStorageAccess}. Classes that implement
 * {@code RuleStorageAccess} should use an implementation of
 * {@link RuleStorageDatabase} to access the database.
 * 
 * 
 * @author Stefan
 *
 */
interface RuleStorageDatabase extends RuleStorageAccess {

}
