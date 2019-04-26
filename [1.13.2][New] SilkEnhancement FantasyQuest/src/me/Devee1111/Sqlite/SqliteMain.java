package me.Devee1111.Sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.block.Block;

import me.Devee1111.SilkEnhancementMain;

public class SqliteMain {
	
	//Storing variables and such for use
	private static SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	private static String fatalMessage = "[SilkEnhancement] A fatal error has occured, and the plugin may not work as intended.";
	
	//Making an instance of our main class
	public SilkEnhancementMain instance;
	public SqliteMain(SilkEnhancementMain p) {
		this.instance = p;
	}
	
	/*
	 * Used for other classes to get a connection and adjust file as desired.
	 */
	
	public static Connection connect() {
		String url = "jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+"/placed.db";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error connecting to SQL database!");
			inst.getLogger().log(Level.SEVERE,fatalMessage);
			ex.printStackTrace();
		}
		return conn;
	}
	
	/*
	 * This simply checks the database to see if a block is known.
	 */
	
	public static boolean checkData(Block block) {
		boolean exists = false;
		String sql = "SELECT * FROM placed WHERE world = ? AND x = ? AND y = ? AND z = ?;";
		try {
			Connection conn = connect();
			PreparedStatement pstat = conn.prepareStatement(sql);
			pstat.setString(1, block.getWorld().toString());
			pstat.setInt(2, block.getX());
			pstat.setInt(3, block.getY());
			pstat.setInt(4, block.getZ());
			ResultSet rs = pstat.executeQuery();
			if(rs.isBeforeFirst()) {//This is a neet little way of checking if a peice of data exists
				exists = true;
			}
			//releasing our sql resoureces
			rs.close();
			pstat.close();
			conn.close();
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error connecting to SQL database!");
			inst.getLogger().log(Level.SEVERE,fatalMessage);
			ex.printStackTrace();
		} 
		return exists;
	}
	
	/*
	 * Used to delete a row of data, with a given location (gathered from block object)
	 */
	public static void removeData(Block block) {
		//We're only deleting if the location is the same, player / type can change without control
		String sql = "DELETE FROM placed WHERE "
				+ "world = ? "
				+ "AND x = ? "
				+ "AND y = ? "
				+ "AND z = ?;"; 
		try {
			Connection conn = connect();
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, block.getLocation().getWorld().getName());
			stat.setInt(2, block.getLocation().getBlockX());
			stat.setInt(3, block.getLocation().getBlockY());
			stat.setInt(4, block.getLocation().getBlockZ());
			stat.executeUpdate();
			stat.close();
			conn.close();
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error adjusting the SQL database!");
			inst.getLogger().log(Level.SEVERE,fatalMessage);
			ex.printStackTrace();
		}
	}
	
	/*
	 * used to add row to our sql database, and stores some additional information for later features.
	 */
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
			stat.close();
			conn.close();
		} catch (SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] Error adjusting the SQL database!");
			inst.getLogger().log(Level.SEVERE,fatalMessage);
			ex.printStackTrace();
		}
	}
	
	/*
	 * This is called onEnabled(), it makes sure we have a file, and that the file is working.
	 */
	public static void loadSqlFile() {
		//Making sure the file exists, if not creating
		File file = new File(inst.getDataFolder().getAbsolutePath()+"/placed.db");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				inst.getLogger().log(Level.SEVERE,"[SilkEnhancement] Error occured creating SQL database!");
				inst.getLogger().log(Level.SEVERE,fatalMessage);
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
			inst.getLogger().log(Level.SEVERE,fatalMessage);
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					//Makes the file work
					createNewTable();
					conn.close();
				}
			} catch (SQLException ex) {
				inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] An error has occured while closing connection to the SQL database!");
				inst.getLogger().log(Level.SEVERE,fatalMessage);
				ex.printStackTrace();
			}
		}
	}
	
	/*
	 * Method designed for loadSql, it makes sure our placed table exists for plugin use.
	 */
	public static void createNewTable() {
		//Sql connection String
		String url = "jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+"/placed.db";
		//Our actual request to server
		String sql = "CREATE TABLE IF NOT EXISTS placed ("
			//	+ "id integer PRIMARY KEY,"
				+ "world text NOT NULL,"
				+ "x integer NOT NULL,"
				+ "y integer NOT NULL,"
				+ "z integer NOT NULL,"
				+ "type text NOT NULL,"
				+ "player text NOT NULL);";
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			stat.execute(sql);
			//releasing our sql from the ram
			stat.close();
			conn.close();
		} catch(SQLException ex) {
			inst.getLogger().log(Level.SEVERE, "[SilkEnhancement] An error has occured while adjusting the database!");
			inst.getLogger().log(Level.SEVERE,fatalMessage);
			ex.printStackTrace();
		}
	}
}
