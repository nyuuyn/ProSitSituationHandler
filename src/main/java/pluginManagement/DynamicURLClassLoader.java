package pluginManagement;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * The Class DynamicURLClassLoader is an extension of {@link URLClassLoader}. It
 * does exactly the same, but allows to dynamically add urls.
 */
class DynamicURLClassLoader extends URLClassLoader {

    /**
     * Instantiates a new dynamic url class loader, loading jars from the
     * specified URLs.
     * <p>
     * Does the same than {@link URLClassLoader#URLClassLoader(URL[])}
     *
     * @param urls
     *            the urls to load.
     */
    DynamicURLClassLoader(URL[] urls) {
	super(urls);

    }

    /**
     * Instantiates a new dynamic url class loader, loading jars from the
     * specified URLs.
     * <p>
     * Does the same than {@link URLClassLoader#URLClassLoader(URL[], ClassLoader))}
     *
     * 
     * @param urls the urls to load
     * @param parent the parent class loader
     */
    public DynamicURLClassLoader(URL[] urls, ClassLoader parent) {
	super(urls, parent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.net.URLClassLoader#addURL(java.net.URL)
     */
    @Override
    protected void addURL(URL url) {
	super.addURL(url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.net.URLClassLoader#close()
     */
    @Override
    public void close() throws IOException {

	for (URL url : getURLs()) {
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
