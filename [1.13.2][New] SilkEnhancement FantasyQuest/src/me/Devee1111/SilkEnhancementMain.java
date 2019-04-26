package me.Devee1111;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
		
		//Making our Listener classes
		new SilkEnhancementListenerPlacement(this);
		new SilkEnhancementListenerDebug(this);
		
		//Making our command classes 
		getCommand("secheck").setExecutor(new SilkEnhancementCommandCheck(this));
		
		//Getting our SQL ready
		SqliteMain.loadSqlFile();
	}

	
	@Override
	public void onDisable() {
		
	}
	
	//Core commands that are vital to use, therefore are stored on the main thread.	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("sereload")) {
			if(sender.hasPermission("se.reload")) {
				reloadConfiguration();
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
	
	/*
	 * Messaging system - complete
	 */
	
	//Most messages don't have anything more than the %prefix% placeholder, making this useful.
	public void sendMessage(CommandSender sender, String path) {
		//If check to make no permission sends easier
		if(path.equalsIgnoreCase("nopermission")) {
			path = "messages.nopermission";
		}
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", sender.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		sender.sendMessage(tosend);
	}
	//Used to color a given string (change color codes), often used after createmessage EX: inst.color(createmessage(path).replacePlaceholders); 
	public String color(String tosend) {
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	//If a message calls for more than one placeholder, we can this method instead to start it off, and then add them
	public String createMessage(String path) {
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix", config.getString("options.prefix"));
		return tosend;
	}
	//Method that extends the capability of creating messages, allowing for a boolean that says if needs to make a non configged message 
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
			String tosend = config.getString(message); 
			return tosend;
		}
	}
	
	/*
	 * onEnable methods - complete
	 */
	
	//it setups up the instance of the main class for getInstance to give.
	public void setInstance(SilkEnhancementMain instance) {
		SilkEnhancementMain.instance = instance;
	}
	//Makes sure everything config related is good to go
	public void loadConfig() {
		//Making sure we have a config, if one doesn't exist, it's created
		saveDefaultConfig();
		//Setting our config
		config = getConfig();
		//Option that allows for a config reset whithout deleting, useful I guess
		if(!config.contains("options.resetConfig")) {
			config.set("options.resetConfig", false);
			saveConfig();
		}
		if(config.getBoolean("options.resetConfig") == true) {
			saveResource("config.yml", true);
		}
		//Getting our default configuration
		InputStream in = getResource("config.yml");
		InputStreamReader isr = new InputStreamReader(in);
		FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(isr);
		//If theres a new version of the config, save it.
		if(config.contains("version")) {
			if(config.getDouble("version") != defaultConfig.getDouble("version")) {
				saveResource("config.yml",true);
			}
		} else { //They touched my fish, RESET THE CONFIG!
			saveResource("config.yml", true);
		}
	}
	
	/*
	 * Assisting system - incomplete
	 */
	
	//It's not uncommon for use to enable/disable debug, so we have this to manage it, and to allow other classes an easier time
	public void debug(String message) {
		if(config.getBoolean("options.debug") == true) {
			Level desire = Level.INFO;
			if(config.contains("options.level")) {
				try { 
					desire = Level.parse(config.getString("options.debug.level"));
				} catch (Exception e) {/*Don't care it failed, yet*/}
			}
			getLogger().log(desire,message);
		}
	}
	/*this allows us to send debug messages to players with options and all the works.*/
	public void debug(String message,Player p) {
		//generally for optimization we check this before it's called, but this allows random calls to be made safely
		if(config.getBoolean("options.debug") == true) {
			if(p.hasPermission("se.debug")) {
				//Prefixing the message
				if(config.contains("options.prefix")) {
					//Making sure if a space is needed after prefix, it's added
					String spaceadjust = "";
					if(!config.getString("options.prefix").endsWith(" ")) {
						spaceadjust = " ";
					}
					message = config.getString("options.prefix")+ spaceadjust +"&b"+ message;
				}
				//setting default color for the messages
				message = message.replace("=", "&7=");
				//sending message
				p.sendMessage(color(message));
				//Logging to console, and alerting of the player
				debug(p.getName() + " - " + message);
			}
			
		}
	}
	//Our instance, retrieved from other classes to access our methods
	public static SilkEnhancementMain getInstance() {
		return instance;
	}	
	/*At the moment they're not needed, but in the future, it will perform checks on the changes and then fix them before they're permanent. */
	//Saves config to disk
	public void saveConfiguration() {
		saveConfig();
	}
	//Reloads the config
	public void reloadConfiguration() {
		if(config.getBoolean("options.resetConfig") == true) {
			saveResource("config.yml", true);
		}
		
		reloadConfig();
	}
	//This is used to log information to console, for general uses
	public void log(String message) {
		getLogger().log(Level.WARNING,message);
	}
	/*Not working, causes nullpointerexception when used with others exceptions*/
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
}
