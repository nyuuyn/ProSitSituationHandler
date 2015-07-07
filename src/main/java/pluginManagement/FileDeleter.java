/**
 * 
 */
package pluginManagement;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * The Class FileDeleter periodically tries to delete files identified by URLs.
 * If the deletion of a file fails, it tries to delete the file later.
 * 
 * 
 * @author Stefan
 *
 */
class FileDeleter {

	/** The logger for this class. */
	private final static Logger logger = Logger.getLogger(FileDeleter.class);

	/** The file paths of the files that can be deleted. */
	private LinkedList<URL> toDelete = new LinkedList<>();

	/**
	 * The deleterScheduler schedules the delete jobs.
	 */
	private ScheduledThreadPoolExecutor deleterScheduler;

	/**
	 * Creates a new instance of {@code FileDeleter}. By doing this, it will
	 * start periodic removal attempts of the files to delete.
	 */
	public FileDeleter() {

		/*
		 * Starts an asynchronous thread that (tries) to delete the no longer
		 * required jars from time to time.
		 */
		deleterScheduler = new ScheduledThreadPoolExecutor(1);
		deleterScheduler.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				deleteFiles();
			}
		}, 10, 120, TimeUnit.SECONDS);// TODO: Einheit auf Minuten setzen
	}

	/**
	 * Tries to delete all files from {@link #toDelete}. If a file cannot be
	 * deleted, the URL identifying this file remains in the list.
	 * 
	 */
	private void deleteFiles() {
		Iterator<URL> it = toDelete.iterator();
		while (it.hasNext()) {
			File file;
			try {
				file = new File(it.next().toURI());
				// deleting a file does not work, when there are still
				// classes loaded
				if (file.delete()) {
					it.remove();
					logger.debug("Removing " + file.getName() + " successful.");
				} else {
					logger.debug("Removing " + file.getName()
							+ " failed. Trying again later");
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Tells this instance of {@code JarDeleter} to delete a file. The file is
	 * identified by an URL.
	 * <p>
	 * Note that the file will not be deleted immediately, but the next time the
	 * FileDeleter runs.
	 * 
	 * @param filePath
	 *            the path of the file to delete
	 */
	public void deleteFile(URL filePath) {
		toDelete.add(filePath);
	}

	/**
	 * Stops this instance of FileDeleter. It will not further attempt to delete
	 * the specified files. Files that couldn't be deleted so far will persist.
	 */
	public void stop() {
		deleterScheduler.shutdown();
	}

}
