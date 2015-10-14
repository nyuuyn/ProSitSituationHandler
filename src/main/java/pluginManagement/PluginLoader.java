package pluginManagement;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import situationHandler.plugin.Plugin;

/**
 * The Class PluginLoader is responsible to load plugins at startup and
 * dynamically. Furthermore it is able to delete plugins at runtime and it gives
 * access to loaded plugins.
 * <p>
 * It also manages the folder in which the plugins are stored, i.e. it deletes
 * jar-files that are not needed anymore and copies new jar files in the folder.
 * It can take some take until a jar file is deleted, because there might still
 * be existing objects of some of the classes. Therefore, it runs an
 * asynchronous job that tries to delete the jars from time to time.
 * <p>
 */
class PluginLoader {

    /** The logger for this class. */
    private final static Logger logger = Logger.getLogger(PluginLoader.class);

    /**
     * The path to the plugin folder. Jar-Files in this folder are loaded at
     * startup.
     */
    private String pluginFolder;

    /**
     * The path to the runtime folder. This folder is used to store jars that
     * are added at runtime. It is purged at startup. Therfore, plugins in this
     * folder will not be loaded.
     */
    private String runtimeFolder;

    /**
     * This Hashmaps contains an instance of each plugin. The instance is used
     * to create the Callable instances etc. The plugin id is used as key.
     */
    private HashMap<String, Plugin> plugins;

    /**
     * The plugin urls. The urls lead to the jar-files that are opened by the
     * class loader for each plugin. Mapping is (PluginID:Filepath)
     */
    private HashMap<String, URL> pluginUrls = new HashMap<>();

    /**
     * The deleter is responsible for deleting jars that are not needed anymore.
     * 
     */
    private FileDeleter deleter = new FileDeleter();

    /** The service loader. Uses {@link #urlClassLoader} as class loader. */
    private ServiceLoader<Plugin> serviceLoader;

    /** The class loader used for {@link #serviceLoader}. */
    private DynamicURLClassLoader urlClassLoader;

    /*
     * * TODO: Prinzipiell sollte das "unloaden funktionieren. Leider gibt es
     * irgendwo immer noch Instanzen von den Klassen, wodurch die JAR geöffnet
     * bleibt und sich nie löschen lässt. --> CleanupMethode dürfte die Lösung
     * sein, damit die Plugins auch sauber zu machen.
     */

    // TODO: Problem hier: Dadurch das der ganze Class Loader zugemacht wird,
    // könnte es auch sein das noch laufen threads verrecken (insbesondere dann
    // auch durch das Shutdown --> Lösung: Eigener Class Loader? Für jedes
    // Plugin und dann etwas gezielter Schließen und neu laden?

    /**
     * Creates a new instance of Plugin loader.
     * 
     * @param pluginFolder
     *            The path to the plugin folder. Jar-Files in this folder are
     *            loaded at startup. This can be a relative or an absolute path.
     * @param runtimeFolder
     *            The name of the runtime folder. This folder is used to store
     *            jars that are added at runtime. It is purged at startup.
     *            Therfore, plugins in this folder will not be loaded. This is
     *            only the name of a folder and NOT a filepath. The
     *            runtimeFolder will be a subdirectory of the
     *            {@code pluginFolder}.
     * 
     */
    public PluginLoader(String pluginFolder, String runtimeFolder) {
	this.runtimeFolder = runtimeFolder;
	this.pluginFolder = pluginFolder;
	clearRuntimeDir();
	plugins = new HashMap<String, Plugin>();
	searchJars();
	initLoaders();

    }

    /**
     * Inits the class loader and the service loader. Initially loads the
     * available Plugins.
     */
    private void initLoaders() {

	urlClassLoader = new DynamicURLClassLoader(
		pluginUrls.values().toArray(new URL[pluginUrls.values().size()]),
		getClass().getClassLoader());

	serviceLoader = ServiceLoader.load(Plugin.class, urlClassLoader);
	updatePluginCache();
    }

    /**
     *
     * @return the ids of all plugins that are currently loaded.
     */
    Set<String> getPluginIDs() {
	return plugins.keySet();

    }

    /**
     * Gets an instance of a plugin by id.
     *
     * @param pluginID
     *            the plugin id
     * @return an instance of this plugin's implementing class, or null if no
     *         plugin with this id exists.
     */
    Plugin getPluginByID(String pluginID) {
	return plugins.get(pluginID);
    }

    /**
     * Adds a new plugin with the specified id. Loads the jar that contains the
     * plugin from the specified path. It is not required that the plugin also
     * stays on this path.
     *
     * @param ID
     *            the id of the plugin. The id should be globally unique
     * @param path
     *            the path of the jar file that contains the plugin.
     * @param deleteJar
     *            if true, the file at {@code path} is deleted after loading the
     *            jar
     * @return true, if plugin was successfully loaded. False else, especially
     *         when there is already a plugin with the same name loaded.
     */
    boolean addPlugin(String ID, String path, boolean deleteJar) {

	// check if plugin already loaded
	if (plugins.containsKey(ID)) {
	    logger.debug("Plugin: " + ID + " already exists. Nothing was added");
	    // jar not needed anymore
	    if (deleteJar) {
		try {
		    Files.delete(Paths.get(path));
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    return false;
	}

	// create folder for runtime plugins if necessary
	logger.debug("Adding new plugin: " + ID + " at " + path);
	File file = new File(pluginFolder + File.separator + runtimeFolder);
	if (!file.exists()) {
	    file.mkdir();
	}

	File targetPath = buildTargetPath(ID);

	// copy jar file in plugin directory
	try {
	    Files.copy(Paths.get(path), Paths.get(targetPath.getPath()),
		    StandardCopyOption.REPLACE_EXISTING);
	    if (deleteJar) {
		Files.delete(Paths.get(path));
	    }
	    urlClassLoader.addURL(targetPath.toURI().toURL());
	    pluginUrls.put(ID, targetPath.toURI().toURL());
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	// reload jars
	serviceLoader.reload();
	updatePluginCache();
	return true;

    }

    /**
     * Convenience method. Creates the path, under which jars that are loaded at
     * runtime are stored. Checks for conflicts with already existing files and
     * creates a fileName without conflicts.
     *
     * @param ID
     *            the id of the plugin. Is used as file name.
     * @return a file with a unique filename in the plugin folder.
     */
    private File buildTargetPath(String ID) {

	String folderName = pluginFolder + File.separator + runtimeFolder + File.separator;
	String fileName = ID + ".jar";

	File targetFile = new File(folderName + fileName);

	// add "_" to the filename, if file already exists
	while (targetFile.exists()) {
	    fileName = "_" + fileName;
	    targetFile = new File(folderName + fileName);
	}
	return targetFile;

    }

    /**
     * Removes the plugin. After removing, the plugin can no longer be used.
     * Initiates removal of the containing jar file.
     *
     * @param ID
     *            the id of the plugin to remove
     * @return true, if successful, false if no plugin with the specified id
     *         exists.
     */
    boolean removePlugin(String ID) {

	if (!plugins.containsKey(ID)) {
	    logger.debug("Plugin: " + ID + " does not exist. Nothing was removed");
	    return false;
	}

	logger.debug("Removing plugin " + ID);

	deleter.deleteFile(pluginUrls.get(ID));
	// remove mappings (and shutdown)
	pluginUrls.remove(ID);
	plugins.remove(ID).shutdown();
	// TODO: Reicht es nur diesen zu shutten oder müssten auch alle anderen
	// geshutted werden? Die Frage ist ober bei der Reiniitierung ein völlig
	// neues Objekt erstellt wird oder ob das immer noch irgendwie dasselbe
	// ist. Wenn es nicht dasselbe ist, kann man auf dieses nämlich nicht
	// mehr zugreifen um eventuelle Hintergrundthreads zu schließen usw.
	// (Mit einem Multiclassloader sollte diese Frage ausgeräumt sein)

	// a classloader cannot "unload" a class, therefore a new classloader is
	// created, that does not load the removed plugin. The old class loader
	// will be garbage collected (as soon as there aren't any instances of
	// classes loaded by this loader left)
	try {
	    urlClassLoader.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	initLoaders();

	return true;
    }

    /**
     * Updates the plugin cache, i.e. stores instances of each available plugin
     * in the cache.
     */
    private void updatePluginCache() {
	Iterator<Plugin> it = serviceLoader.iterator();
	try {
	    while (it.hasNext()) {
		Plugin plugin = it.next();
		plugins.put(plugin.getID(), plugin);
		logger.debug("Found Plugin " + plugin.getName());
	    }
	} catch (ServiceConfigurationError e) {
	    logger.warn("Could not load plugin. " + e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * Searches the {@code PLUGIN_FOLDER} for jar files. Stores the URLs of the
     * jars in the HashMap using the filename as plugin id. Therefore the naming
     * conventions for plugin files should be followed in this folder.
     * Convention is "PluginID".jar.
     * 
     */
    private void searchJars() {

	File folder = new File(pluginFolder);

	if (!folder.exists()) {
	    logger.debug("Plugin Folder Created: " + folder.mkdirs());
	}

	File[] jarList = folder.listFiles(new FileFilter() {
	    public boolean accept(File file) {
		return file.getPath().toLowerCase().endsWith(".jar");
	    }
	});

	for (int i = 0; i < jarList.length; i++) {
	    try {
		String pluginID = jarList[i].getName().substring(0,
			jarList[i].getName().lastIndexOf('.'));
		pluginUrls.put(pluginID, jarList[i].toURI().toURL());
	    } catch (MalformedURLException e) {
		e.printStackTrace();
	    }
	}

    }

    /**
     * Deletes all jars in the runtime directory for plugins.
     */
    private void clearRuntimeDir() {
	File folder = new File(pluginFolder + File.separator + runtimeFolder);

	try {
	    FileUtils.deleteDirectory(folder);
	} catch (IOException e) {
	    logger.error("Could not clear Runtime directory.");
	}
    }

    /**
     * Cleans up, when the Plugin loader is not needed anymore. Releases all
     * resources.
     * 
     */
    void shutdown() {
	plugins.values().forEach(plugin -> plugin.shutdown());
	plugins.clear();
	try {
	    urlClassLoader.close();
	} catch (IOException e) {
	    logger.error("Could not close class loader.", e);
	}
	deleter.stop();
	clearRuntimeDir();
    }
}
