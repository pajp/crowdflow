package nl.redcode.iphone;

import java.io.File;
import java.util.zip.GZIPOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import nl.redcode.iphone.sqllite.GeoDataExtractor;

/**
 * Main class
 * 
 * TODO: This could use a nice GUI...!
 * TODO: Also: iPhone backups are in default directories, why not check those and let the user pick one if found.
 * 
 * @author Roy van Rijn
 *
 */
public class Tracker {
	public Tracker() {
		
	}
	
	public void convert(File directory, GZIPOutputStream out, Runnable updateProgressBar) {
		
		try {	
		        File sqlLite = (new LinkedList<File>(Arrays.asList(directory.listFiles()))).contains(new File(directory, "consolidated.db")) ? new File(directory, "consolidated.db") : new FileLister(directory).getHiddenFile();
			
			GeoDataExtractor e = new GeoDataExtractor();
			e.extractGeoData(sqlLite, out, updateProgressBar);
			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			
		}
	}
}
