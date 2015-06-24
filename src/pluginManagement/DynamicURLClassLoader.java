package pluginManagement;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

class DynamicURLClassLoader extends URLClassLoader {

	DynamicURLClassLoader(URL[] urls) {
		super(urls);

	}

	DynamicURLClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		// TODO Auto-generated constructor stub
	}

	DynamicURLClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addURL(URL url) {
		super.addURL(url);
	}

}
