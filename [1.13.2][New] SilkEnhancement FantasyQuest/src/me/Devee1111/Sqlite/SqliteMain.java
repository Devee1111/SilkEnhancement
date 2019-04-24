package me.Devee1111.Sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.block.Block;

import me.Devee1111.SilkEnhancementMain;

public class SqliteMain {
	
	private static SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	public SilkEnhancementMain instance;
	
	public SqliteMain(SilkEnhancementMain p) {
		this.instance = p;
	}
	
	public static Connection connect() {
		String url = "jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+"/placed.db";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error connecting to SQL database!");
			inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A error fatal has occured, and the plugin will not work as expected.");
			ex.printStackTrace();
		}
		return conn;
	}
	
	
	//Beginnings of checking if data exists method
	public static boolean checkData(Block block, String uuid) {
		boolean exists = false;
		String sql = "";
		
		try {
			Connection conn = connect();
			conn.prepareStatement(sql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return exists;
	}
	
	
	/*
	 * Needs testing.
	 */
	
	public static void removeData(Block block, String type, String uuid) {
		//Maybe works
		//String sql = "DELETE FROM placed WHERE (?,?,?,?,?,?)";
		
		String sql = "DELETE FROM placed WHERE"
				+ "world = ?"
				+ "AND x = ?"
				+ "AND y = ?"
				+ "AND z = ?"
				+ "AND type = ?"
				+ "AND player = ?"; 
		
		try {
			Connection conn = connect();
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, block.getLocation().getWorld().getName());
			stat.setInt(2, block.getLocation().getBlockX());
			stat.setInt(3, block.getLocation().getBlockY());
			stat.setInt(4, block.getLocation().getBlockZ());
			stat.setString(5, type);
			stat.setString(6, uuid);
			stat.executeUpdate();
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error adjusting the SQL database!");
			inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A error fatal has occured, and the plugin will not work as expected.");
			ex.printStackTrace();
		}
	}
	
	public static void addData(Block block,String type,String uuid) {
		//Our prepared statement
		String sql = "INSERT INTO placed(world,x,y,z,type,player) VALUES(?,?,?,?,?,?)";
		
		try {
			Connection conn = connect();
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, block.getLocation().getWorld().getName());
			stat.setInt(2, block.getLocation().getBlockX());
			stat.setInt(3, block.getLocation().getBlockY());
			stat.setInt(4, block.getLocation().getBlockZ());
			stat.setString(5, type);
			stat.setString(6, uuid);
			stat.executeUpdate();
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error adjusting the SQL database!");
			inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A error fatal has occured, and the plugin will not work as expected.");
			ex.printStackTrace();
		}
	}
	
	public static void loadSqlFile() {
		//Making sure the file exists, if not creating
		File file = new File(inst.getDataFolder().getAbsolutePath()+"/placed.db");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] Error occured creating SQL database!");
				inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A fatal error has occured, and the plugin will not work as expected.");
				ex.printStackTrace();
			}
		}
		//Connecting to the file, to make sure it works.
		Connection conn = null;
		try {
			String url = "jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+"/placed.db";
			conn = DriverManager.getConnection(url);
			inst.getLogger().log(Level.INFO,"Connection to SQL database has been established.");
			
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error connecting to SQL database!");
			inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A error fatal has occured, and the plugin will not work as expected.");
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					//Making sure we're good datatable wise
					createNewTable();
					conn.close();
				}
			} catch (SQLException ex) {
				inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] An error has occured while closing connection to the SQL database!");
				inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A fatal has occured, and the plugin will not work as expected.");
				ex.printStackTrace();
			}
		}
	}
	
	public static void createNewTable() {
		//Sql connection String
		String url = "jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+"/placed.db";
		//Our actual request to server
		String sql = "CREATE TABLE IF NOT EXISTS placed ("
				+ "id integer PRIMARY KEY,"
				+ "world text NOT NULL,"
				+ "x integer NOT NULL,"
				+ "y integer NOT NULL,"
				+ "type text NOT NULL,"
				+ "player text NOT NULL"
				+ "capacity real);";
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			stat.execute(sql);
		} catch(SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] An error has occured while adjusting the database!");
			inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] A fatal has occured, and the plugin will not work as expected.");
			ex.printStackTrace();
		}
	}
	
	//("Please look into 'loadSqlFile' instead.)"
	@Deprecated 
	public static void checkConnection() {
		Connection conn = null;
		try {
			String url = "jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+"/placed.db";
			conn = DriverManager.getConnection(url);
			inst.getLogger().log(Level.INFO,"Connection to SQL database has been established.");
			
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "Error connecting to SQL database!");
			inst.getLogger().log(Level.SEVERE,"A error fatal has occured, and the plugin will not work as expected.");
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				inst.getLogger().log(Level.SEVERE, "An error has occured while closing connection to the SQL database!");
				inst.getLogger().log(Level.SEVERE,"A fatal has occured, and the plugin will not work as expected.");
				ex.printStackTrace();
			}
		}
	}

}
