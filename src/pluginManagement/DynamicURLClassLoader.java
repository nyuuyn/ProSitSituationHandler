package pluginManagement;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.jar.JarFile;

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

	@Override
	public void close() throws IOException {
		
		for (URL url:getURLs()){
			JarFile jar;
			try {
				File file = new File(url.toURI());
				jar = new JarFile(file);
				jar.close();
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
				
		super.close();
	}
	


}
