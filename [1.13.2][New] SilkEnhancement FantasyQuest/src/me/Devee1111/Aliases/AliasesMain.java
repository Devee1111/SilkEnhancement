package me.Devee1111.Aliases;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;

import me.Devee1111.SilkEnhancementMain;

public class AliasesMain {

	
	//This is our static version of the main class, use this for main class uses
	private static SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	//Setting up our instance
	public SilkEnhancementMain instance;
	public AliasesMain(SilkEnhancementMain instance) {
		this.instance = instance;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static boolean checkIfWorking(CommandSender sender) {
		boolean working = true; 
		String currentkey = "";
		String lastpreferredname = "";
		try {
			log("========== START ==========",sender);
			for(String key : inst.config.getConfigurationSection("aliases").getKeys(false)) {
				currentkey = key;
				EntityType e = EntityType.fromName(key);
				log("Entity = "+e.getName(), sender);
				String names = "none";
				for(String name : inst.config.getStringList("aliases."+key+".names")) {
					names = names + name +", ";
				}
				names = names.substring(0, names.length() - 3) + ".";
				log("Names = "+names,sender);
				log("Preferred Name = " + inst.config.getString("aliases."+key+".preferredname"),sender);
				log("--------------------",sender);
			}
			log("========== END ==========",sender);
		} catch (Exception ex) {
			log("Failed! Here are the details.",sender);
			log("Key = " + currentkey,sender);
			log("Preferredname = " + lastpreferredname,sender);
			working = false;
		}
		return working;
	}
	
	
	public static void log(String message,CommandSender sender) {
		//Since this is a questionable as they're testing config add a check
		String prefix = "&8(&3Spawners&8)";
		if(inst.config.contains("options.prefix")) {
			prefix = inst.config.getString("options.prefix");
		}
		//Since normally messages handles the space, here we make sure it has it
		if(!prefix.endsWith(" ")) {
			prefix = prefix + " ";
		}
		//Getting the message ready for console // sender
		String tolog = prefix + message;
		tolog = ChatColor.translateAlternateColorCodes('&', tolog);
		//If it's not console, send it to sender as well
		if(sender instanceof ConsoleCommandSender == false) {
			sender.sendMessage(tolog);
		}
		
		inst.getLogger().log(Level.INFO,tolog);
		
	}
	
}
