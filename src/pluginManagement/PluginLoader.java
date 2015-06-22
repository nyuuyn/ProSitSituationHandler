package pluginManagement;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import situationHandler.plugin.Plugin;

class PluginLoader {

	private static final String PLUGIN_FOLDER = "plugins";

	private HashMap<String, Plugin> plugins;

	private ServiceLoader<Plugin> serviceLoader;
	private URLClassLoader urlClassLoader;

	public PluginLoader() {
		plugins = new HashMap<String, Plugin>();
		initPlugins();
	}

	Set<String> getPluginIDs() {
		return plugins.keySet();

	}

	Plugin getPluginByID(String pluginID) {
		return plugins.get(pluginID);
	}

	private void initPlugins() {

		urlClassLoader = new URLClassLoader(loadFiles());

		serviceLoader = ServiceLoader.load(Plugin.class, urlClassLoader);

		Iterator<Plugin> it = serviceLoader.iterator();

		while (it.hasNext()) {

			Plugin plugin = it.next();

			plugins.put(plugin.getID(), plugin);

			System.out.println(plugin.getName());
		}

	}

	void reload() {

		try {
			urlClassLoader.close();
			plugins.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}

		initPlugins();
	}

	private URL[] loadFiles() {
		File loc = new File(PLUGIN_FOLDER);

		File[] flist = loc.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getPath().toLowerCase().endsWith(".jar");
			}
		});
		URL[] urls = new URL[flist.length];
		for (int i = 0; i < flist.length; i++) {
			try {
				urls[i] = flist[i].toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		return urls;

	}

}
