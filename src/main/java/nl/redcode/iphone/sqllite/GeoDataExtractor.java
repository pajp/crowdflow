package nl.redcode.iphone.sqllite;

import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.io.File;

import javax.swing.SwingUtilities;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * This class opens the iPhone file as SQLLite database and extracts the geo-data.
 * 
 * @author Roy van Rijn
 * 
 */
public class GeoDataExtractor {
	public long countGeoData(File dbFile) throws Exception {
		SqlJetDb db = SqlJetDb.open(dbFile, false);
		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
		long count = 0;
		try {
			ISqlJetTable table = db.getTable("CellLocation");
			ISqlJetCursor cursor = table.open();
			count = cursor.getRowCount();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			db.commit();
		}
		return count;
	}
	
	public void extractGeoData(File dbFile, GZIPOutputStream out, Runnable updateProgressBar) throws Exception {
		SqlJetDb db = SqlJetDb.open(dbFile, false);
		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);

		try {
			extractGeoDataFromTable(db, "CdmaCellLocation", out);
			SwingUtilities.invokeLater(updateProgressBar);
			
			extractGeoDataFromTable(db, "CdmaCellLocationHarvest", out);
			SwingUtilities.invokeLater(updateProgressBar);
					
			extractGeoDataFromTable(db, "CellLocation", out);	
			SwingUtilities.invokeLater(updateProgressBar);
			
			extractGeoDataFromTable(db, "CellLocationHarvest", out);
			SwingUtilities.invokeLater(updateProgressBar);
				
			extractGeoDataFromTable(db, "WifiLocation", out);	
			SwingUtilities.invokeLater(updateProgressBar);
				
			extractGeoDataFromTable(db, "WifiLocationHarvest", out);
			SwingUtilities.invokeLater(updateProgressBar);

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			db.commit();
		}
	}

	public void extractGeoDataFromTable(SqlJetDb db, String tableName, GZIPOutputStream out) throws SqlJetException {
		ISqlJetCursor cursor = null;
		try {
			String s = "";
			out.write(("# table: "+tableName+"\n").getBytes());
			
			ISqlJetTable table = db.getTable(tableName);
			
			List<ISqlJetColumnDef> columns = table.getDefinition().getColumns();
			int n = columns.size();
			for (int i = 0; i<n; i++) s += columns.get(i).getName()+"\t";
			out.write((s+"\n").getBytes());
			
			cursor = table.open();
			
			if (!cursor.eof()) {
				do {
					s = "";
					for (int i = 0; i<n; i++) s += cursor.getValue(i).toString()+"\t";
					//for (int i = 0; i<100; i++) s += Math.random()+"\t";
					out.write((s+"\n").getBytes());
				} while (cursor.next());
			}
			cursor.close();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
		}
	}
}
