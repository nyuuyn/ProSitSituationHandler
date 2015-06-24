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

import org.apache.camel.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import situationHandler.plugin.Plugin;

class PluginLoader {
	final static Logger logger = Logger.getLogger(PluginLoader.class);

	private static final String PLUGIN_FOLDER = "plugins";

	private static final String RUNTIME_FOLDER = "runtime";

	private HashMap<String, Plugin> plugins;

	private HashMap<String, URL> pluginUrls = new HashMap<>();

	private LinkedList<URL> toDelete = new LinkedList<>();

	private ServiceLoader<Plugin> serviceLoader;

	private DynamicURLClassLoader urlClassLoader;

	public PluginLoader() {
		clearRuntimeDir();
		plugins = new HashMap<String, Plugin>();
		searchJars();
		initLoaders();
		startDeleterService();
	}

	private void initLoaders() {
		urlClassLoader = new DynamicURLClassLoader(pluginUrls.values().toArray(
				new URL[pluginUrls.values().size()]));

		serviceLoader = ServiceLoader.load(Plugin.class, urlClassLoader);
		updatePluginCache();
	}

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

	Set<String> getPluginIDs() {
		return plugins.keySet();

	}

	Plugin getPluginByID(String pluginID) {
		return plugins.get(pluginID);
	}

	void addPlugin(String ID, String path) {
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

	}

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

	void removePlugin(String ID) {

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

	}

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
	
	private void clearRuntimeDir(){
		File folder = new File(PLUGIN_FOLDER+ File.separator + RUNTIME_FOLDER);

		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
