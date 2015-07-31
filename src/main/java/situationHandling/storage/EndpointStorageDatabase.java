package situationHandling.storage;

/**
 * This interface does just the same than {@link EndpointStorageAccess}. However it
 * is used to differentiate between classes that do plain database access and
 * classes do more than just writing and reading from the database.
 * <p>
 * So, classes that focus more on accessing the database should implement
 * {@code EndpointStorageDatabase} and classes, that focus on other tasks should
 * implement {@code EndpointStorageAccess}. Classes that implement
 * {@code EndpointStorageAccess} should use an implementation of
 * {@link EndpointStorageDatabase} to access the database.
 * 
 * 
 * @author Stefan
 */
public interface EndpointStorageDatabase extends EndpointStorageAccess {

}
