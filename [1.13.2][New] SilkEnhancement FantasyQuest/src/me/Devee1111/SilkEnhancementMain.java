package me.Devee1111;


//Java imports
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
//General bukkit immports
import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
//Sql support import
import me.Devee1111.Sqlite.SqliteMain;
import me.Devee1111.Vault.SEVaultMain;
/*
 * Our API imports
 * Economy - https://github.com/MilkBowl/VaultAPI
 * SilkSpawners - https://dev.bukkit.org/projects/silkspawners/pages/api
 */
import de.dustplanet.util.SilkUtil;
import net.milkbowl.vault.economy.Economy;



public class SilkEnhancementMain extends JavaPlugin {
	
	private static SilkEnhancementMain instance;
	private static Economy econ = null;
	private static SilkUtil su = null;
	private boolean returnMessages = false;
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		//Create instance of our main class for other classes
		setInstance(this);
		
		//Load configuration for use // manages default
		loadConfig();
		
		//Setting our apis
		if(!setupEconomy()) {
			getLogger().log(Level.SEVERE,"[SilkEnhancement] Failed to hook into economy! Plugin will now disable.");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		
		//Making our Listener classes
		new SilkEnhancementListenerPlacement(this);
		new SilkEnhancementListenerDebug(this);
		new SilkEnhancementListenerSpawners(this);
		
		//hooking into SilkSpawner api
		su = SilkUtil.hookIntoSilkSpanwers();
		setupSilkSpawnerFeatures();
		//Making our command classes 
		getCommand("secheck").setExecutor(new SilkEnhancementCommandCheck(this));
		getCommand("sereset").setExecutor(new SilkEnhancementCommandReset(this));
		
		//Getting our SQL ready
		SqliteMain.loadSqlFile();
	}

	
	@Override
	public void onDisable() {
		if(returnMessages == true) {
			File ssLangFile = new File(getDataFolder().getAbsolutePath().replace("/SilkEnhancement", "/SilkSpawners")+"localization.yml");
			YamlConfiguration ssLang = YamlConfiguration.loadConfiguration(ssLangFile);			
			ssLang.set("spawnerBroken", config.getString("options.silk_spawners.old_messages.mine"));;
			ssLang.set("spawnerPlaced", config.getString("options.silk_spawners.old_messages.place"));
			returnMessages = false; //Not needed but I like it
		}
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
				if(config.getBoolean("options.debug") == true) {
					config.set("options.debug", false);
					sender.sendMessage(color(createMessage("%prefix% &cYou've disabled debugging!",false)));
				} else {
					config.set("options.debug", true);
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
	 * This are methods that are used for accessing our api
	 * Api System - incomplete
	 */
	//Sets up the economy api called onenable
	private boolean setupEconomy() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
		
	}
	
	//Disables features players not like for silkspawners
	private void setupSilkSpawnerFeatures() {
		boolean needToReload = false;
		if(config.getBoolean("options.silk_spawners.removeSpam") == true) {
			File ssConfigFile = new File(getDataFolder().getAbsolutePath().replace("/SilkEnhancement", "/SilkSpawners")+"/config.yml");
			YamlConfiguration ssConfig = YamlConfiguration.loadConfiguration(ssConfigFile);
			//Setting the features that could be considered 'annoying' or glitchy
			if(ssConfig.get("defaultCreature").equals("90")) {
				ssConfig.set("defaultCreature","PIG");
			}
			ssConfig.set("notifyOnClick", false);
			ssConfig.set("notifyOnHold", false);
			ssConfig.set("barAPI.enable", false);
			ssConfig.set("vanillaBossBar.enable", false);
			ssConfig.set("verboseConfig",false);
			//Saving config back to disk
			try {
				ssConfig.save(ssConfigFile);
			} catch (IOException e) {log("Failed to execute removeSpam for SilkSpawners!","SEVERE");}
			needToReload = true;
		}
		
		if(config.getBoolean("options.silk_spawners.removeSpam") == true) {
			File ssLangFile = new File(getDataFolder().getAbsolutePath().replace("/SilkEnhancement", "/SilkSpawners")+"/localization.yml");
			System.out.println("ssLangFilePath = "+getDataFolder().getAbsolutePath().replace("/SilkEnhancement", "/SilkSpawners")+"/localization.yml");
			YamlConfiguration ssLang = YamlConfiguration.loadConfiguration(ssLangFile);
			config.set("options.silk_spawners.old_messages.mine", ssLang.getString("spawnerBroken"));
			ssLang.set("spawnerBroken", "");
			config.set("options.silk_spawners.old_messages.place", ssLang.getString("spawnerPlaced"));
			ssLang.set("spawnerPlaced", "");
			try {
				ssLang.save(ssLangFile);
			} catch (IOException e) {log("Failed to execute removeSpam for SilkSpawners!","SEVERE");}
			returnMessages = true;
			needToReload = true;
		}
		
		if(needToReload == true) {
			//Not the best way to do it, but it works for now
			Bukkit.dispatchCommand(getServer().getConsoleSender(), "ss reload");
		}
	}
	
	//gets vault economy api
	public static Economy getEconomy() {
		return econ;
	}
	//gets our silkspawner api
	public SilkUtil getsu() {
		return su;
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
	//It's the sameas the other sendMessage method, but allows a player object
	public void sendMessage(Player p, String path) {
		//If check to make no permission sends easier
		if(path.equalsIgnoreCase("nopermission")) {
			path = "messages.nopermission";
		}
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", p.getName());
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		p.sendMessage(tosend);
	}
	//custom message to this plugin, relating to spawners
	public void sendCustomMessage(Player p, CreatureSpawner spawner, String path, Double cost) {
		String tosend = config.getString("messages."+path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		tosend = tosend.replace("%player%", p.getName());
		String name = config.getString("aliases."+spawner.getSpawnedType().toString()+".preferredname");
		tosend = tosend.replace("%type%", name);
		tosend = tosend.replace("%balance%", Double.toString(SEVaultMain.getBalance(p)));
		tosend = tosend.replace("%cost%", Double.toString(cost));
		if(SEVaultMain.hasEnough(p, cost) == false) {
			double need = cost - SEVaultMain.getBalance(p);
			tosend = tosend.replace("%need%", Double.toString(need));
		}
		p.sendMessage(color(tosend));

	}
	//Used to color a given string (change color codes), often used after createmessage EX: inst.color(createmessage(path).replacePlaceholders); 
	public String color(String tosend) {
		tosend = ChatColor.translateAlternateColorCodes('&', tosend);
		return tosend;
	}
	//If a message calls for more than one placeholder, we can this method instead to start it off, and then add them
	public String createMessage(String path) {
		String tosend = config.getString(path);
		tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
		return tosend;
	}
	//Method that extends the capability of creating messages, allowing for a boolean that says if needs to make a non configged message 
	public String createMessage(String message, boolean isConfig) {
		if(isConfig == false) {
			String tosend = message;
			if(config.contains("options.prefix")) {
				tosend = tosend.replace("%prefix%", config.getString("options.prefix"));
			} else {
				tosend = tosend.replace("%prefix%", "&8(&3Spawners&8)");
			}
			return tosend;
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
			reloadConfig();
		}
		//Getting our default configuration
		InputStream in = getResource("config.yml");
		Reader isr = new InputStreamReader(in);
		FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(isr);
		//If theres a new version of the config, save it.
		if(config.contains("version")) {
			if(config.getDouble("version") != defaultConfig.getDouble("version")) {
				saveResource("config.yml",true);
			}
		} else { //They touched my fish, RESET THE CONFIG! AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
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
		//If it got somehow deleted, restore it
		File configFile = new File(getDataFolder().getAbsolutePath()+"/config.yml");
		if(!configFile.exists()) {
			saveResource("config.yml",true);
		}
		
		reloadConfig();
	}
	//This is used to log information to console, for general uses
	public void log(String message) {
		getLogger().log(Level.WARNING,message);
	}
	//extra option to choose log level
	public void log(String message, String level) {
		Level desire = Level.WARNING;
		desire = Level.parse(level);
		getLogger().log(desire,message);
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
