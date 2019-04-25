package me.Devee1111;



import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.Devee1111.Sqlite.SqliteMain;



public class SilkEnhancementMain extends JavaPlugin {
	
	private static SilkEnhancementMain instance;
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		//Create instance of our main class for other classes
		setInstance(this);
		
		
		//Load configuration for use // manages default
		loadConfig();
		
		
		//This Listener will listen to spawner placements // minings for our placed file
		new SilkEnhancementListenerPlacement(this);
		
		
		//Here is what's working and connecting
		/*Removed as loadSqlFile() does this now*/
		//createPlacementFile();
		
		//connect
		SqliteMain.loadSqlFile();
		
		
		
		
		
	}

	
	@Override
	public void onDisable() {
		
	}
	
	
	
	//Mkaing sure the actual file exists
	public void createPlacementFile() {
		File placedFile = new File(getDataFolder().getAbsolutePath()+"+placed.db");
		if(!placedFile.exists() == false) {
			try {
				placedFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("sereload")) {
			if(sender.hasPermission("se.reload")) {
				reloadConfig();
				sendMessage(sender, "messages.reloadedConfig");
				return true;
			}
			sendMessage(sender,"messages.nopermission");
		}
		if(cmd.getName().equalsIgnoreCase("sedebug")) {
			if(sender.hasPermission("se.debug")) {
				if(config.getBoolean("debug") == true) {
					config.set("debug", false);
					sender.sendMessage(color(createMessage("%prefix% &cYou've disabled debugging!",false)));
				} else {
					config.set("debug", true);
					sender.sendMessage(color(createMessage("%prefix% &aYou've enabled debugging!",false)));
				}
				saveConfig();
				return true;
			}
			sendMessage(sender,"messages.nopermission");
		}
		return false;
	}
	 
	public void sendMessage(CommandSender sender, String path) {
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", sender.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		sender.sendMessage(tosend);
	}
	public void debug(String message) {
		if(config.getBoolean("options.debug") == true) {
			Level desire = Level.INFO;
			if(config.contains("options.debug.level")) {
				desire = Level.parse(config.getString("options.debug.level"));
				if(desire == null) {
					desire = Level.INFO;
				}
			}
			getLogger().log(desire,message);
		}
	}
	public static SilkEnhancementMain getInstance() {
		return instance;
	}	
	public void setInstance(SilkEnhancementMain instance) {
		SilkEnhancementMain.instance = instance;
	}
	public void saveConfiguration() {
		saveConfig();
	}
	public void reloadConfiguration() {
		reloadConfig();
	}
	/*
	 * needs work
	 */
	public void loadConfig() {
		//If file doesn't exist, make one
		saveDefaultConfig();
		//Getting our default configuration
//		InputStream in = getResource("config.yml");
//		InputStreamReader isr = new InputStreamReader(in);
//		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(isr);
		//If theres a new version of the config, save it.
//		if(config.getDouble("version") != defaultConfig.getDouble("version")) {
//			saveResource("config.yml",true);
//		}
		//Setting our config 
		config = getConfig();
	}
	public String color(String tosend) {
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	public String createMessage(String path) {
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix", config.getString("options.prefix"));
		return tosend;
	}
	
	public void log(String message) {
		getLogger().log(Level.WARNING,message);
	}
	
	//causes error, nullpointer for some reason
	public void error(Exception e) {
		getLogger().log(Level.WARNING,"################################################");
		getLogger().log(Level.WARNING,"# An error has occured withen SilkEnhancement! #");
		getLogger().log(Level.WARNING,"# The Following details have been given:       #");
		getLogger().log(Level.WARNING,"#                   >Cause<                    #");
		getLogger().log(Level.WARNING,e.getCause().toString());
		getLogger().log(Level.WARNING,"#                 >Stacktrace<                 #");
		e.printStackTrace();
		getLogger().log(Level.WARNING,"################################################");
	}
	public String createMessage(String message, boolean isConfig) {
		if(isConfig == false) {
			String tosend = message;
			if(config.contains("options.prefix")) {
				tosend = tosend.replace("%prefix%", config.getString("options.debug"));
			} else {
				tosend = tosend.replace("%prefix%", "&8(&3Spawners&8)");
			}
			return message;
		} else {
			String tosend = createMessage(message); 
			return tosend;
		}
	}
}
