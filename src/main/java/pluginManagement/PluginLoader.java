package pluginManagement;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import situationHandler.plugin.Plugin;

//TODO: Javadoc fertig

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

	/** The logger. */
	private final static Logger logger = Logger.getLogger(PluginLoader.class);

	/**
	 * The path to the plugin folder. Jar-Files in this folder are loaded at
	 * startup.
	 */
	private static final String PLUGIN_FOLDER = "plugins";

	/**
	 * The path to the runtime folder. This folder is used to store jars that
	 * are added at runtime. It is purged at startup. Therfore, plugins in this
	 * folder will not be loaded.
	 */
	private static final String RUNTIME_FOLDER = "runtime";

	/**
	 * This Hashmaps contains an instance of each plugin. The instance is used
	 * to create the Callable instances etc. The plugin id is used as key.
	 */
	private HashMap<String, Plugin> plugins;

	/** The plugin urls. The urls lead to */
	private HashMap<String, URL> pluginUrls = new HashMap<>();

	/** The to delete. */
	private LinkedList<URL> toDelete = new LinkedList<>();

	/** The service loader. */
	private ServiceLoader<Plugin> serviceLoader;

	/** The url class loader. */
	private DynamicURLClassLoader urlClassLoader;

	/**
	 * 
	 * TODO: Prinzipiell sollte das "unloaden funktionieren. Leider gibt es
	 * irgendwo immer noch Instanzen von den Klassen, wodurch die JAR geöffnet
	 * bleibt und sich nie löschen lässt.
	 */

	public PluginLoader() {
		clearRuntimeDir();
		plugins = new HashMap<String, Plugin>();
		searchJars();
		initLoaders();
		startDeleterService();
	}

	/**
	 * Inits the loaders.
	 */
	private void initLoaders() {
		urlClassLoader = new DynamicURLClassLoader(pluginUrls.values().toArray(
				new URL[pluginUrls.values().size()]));

		serviceLoader = ServiceLoader.load(Plugin.class, urlClassLoader);
		updatePluginCache();
	}

	/**
	 * Start deleter service.
	 */
	private void startDeleterService() {
		ScheduledThreadPoolExecutor deleter = new ScheduledThreadPoolExecutor(1);
		deleter.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				System.gc();
				// TODO: alternativ könnte man hier auch probieren einfach alle
				// Jars im Runtime ordner zu löschen --> das würde dann quasi
				// noch ein cleanup am start geben
				Iterator<URL> it = toDelete.iterator();
				while (it.hasNext()) {
					File file;
					try {
						file = new File(it.next().toURI());
						if (file.delete()) {
							it.remove();
							logger.debug("Removing " + file.getName()
									+ " successfull.");
						} else {
							logger.debug("Removing " + file.getName()
									+ " failed. Trying again later");
						}
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}

			}
		}, 10, 5, TimeUnit.SECONDS);// TODO: Einheit auf Minuten setzen

	}

	/**
	 * Gets the plugin i ds.
	 *
	 * @return the plugin i ds
	 */
	Set<String> getPluginIDs() {
		return plugins.keySet();

	}

	/**
	 * Gets the plugin by id.
	 *
	 * @param pluginID
	 *            the plugin id
	 * @return the plugin by id
	 */
	Plugin getPluginByID(String pluginID) {
		return plugins.get(pluginID);
	}

	/**
	 * Adds the plugin.
	 *
	 * @param ID
	 *            the id
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	boolean addPlugin(String ID, String path) {

		if (plugins.containsKey(ID)) {
			logger.debug("Plugin: " + ID + " already exists. Nothing was added");
			return false;
		}

		logger.debug("Adding new plugin: " + ID + " at " + path);
		File file = new File(PLUGIN_FOLDER + File.separator + RUNTIME_FOLDER);
		if (!file.exists()) {
			file.mkdir();
		}

		File targetPath = buildTargetPath(ID);

		try {
			Files.copy(Paths.get(path), Paths.get(targetPath.getPath()),
					StandardCopyOption.REPLACE_EXISTING);
			urlClassLoader.addURL(targetPath.toURI().toURL());
			pluginUrls.put(ID, targetPath.toURI().toURL());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		serviceLoader.reload();
		updatePluginCache();
		return true;

	}

	/**
	 * Builds the target path.
	 *
	 * @param ID
	 *            the id
	 * @return the file
	 */
	private File buildTargetPath(String ID) {

		String folderName = PLUGIN_FOLDER + File.separator + RUNTIME_FOLDER
				+ File.separator;
		String fileName = ID + ".jar";

		File targetFile = new File(folderName + fileName);

		while (targetFile.exists()) {
			fileName = "_" + fileName;
			targetFile = new File(folderName + fileName);
		}
		return targetFile;

	}

	/**
	 * Removes the plugin.
	 *
	 * @param ID
	 *            the id
	 * @return true, if successful
	 */
	boolean removePlugin(String ID) {

		if (!plugins.containsKey(ID)) {
			logger.debug("Plugin: " + ID
					+ " does not exist. Nothing was removed");
			return false;
		}

		logger.debug("Removing plugin " + ID);

		toDelete.add(pluginUrls.get(ID));

		pluginUrls.remove(ID);

		plugins.remove(ID);
		try {
			urlClassLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		initLoaders();

		return true;
	}

	/**
	 * Update plugin cache.
	 */
	private void updatePluginCache() {
		Iterator<Plugin> it = serviceLoader.iterator();
		while (it.hasNext()) {
			Plugin plugin = it.next();
			plugins.put(plugin.getID(), plugin);
			logger.debug("Found Plugin " + plugin.getName());
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

		File folder = new File(PLUGIN_FOLDER);

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
	 * Clear runtime dir.
	 */
	private void clearRuntimeDir() {
		File folder = new File(PLUGIN_FOLDER + File.separator + RUNTIME_FOLDER);

		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
