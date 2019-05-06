package me.Devee1111.Aliases;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.Devee1111.SilkEnhancementMain;
import me.Devee1111.Sqlite.SqliteMain;

public class AliasesMain {

	
	//This is our static version of the main class, use this for main class uses
	private static SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	//Setting up our instance
	public SilkEnhancementMain instance;
	public AliasesMain(SilkEnhancementMain instance) {
		this.instance = instance;
	}
	
	public static double getCost(String type, Block block, Player p) {
		double cost = 0;
		
		inst.log(type);
		
		//determing if we're using naturals or not
		Boolean natural = true;
		if(SqliteMain.checkData(block) == true) {
			natural = false;
		}
		if(inst.config.getBoolean("options.ignoreNaturalInput") == true) {
			natural = false;
		}
		
		//Little snare for free ones
		if((natural == false && inst.config.getBoolean("options.chargeForPlaced") == false)
		|| (natural == true && inst.config.getBoolean("options.chargeForNatural") == false)) {
			return cost;
		}
		
		//Getting the path of the prices
		String pricepath = "options.";
		if(natural == true) {
			pricepath = pricepath + "naturalPrices";
		} else {
			pricepath = pricepath + "placedPrices";
		}
		
		
		//We have type, and the area we're going to check analyze!
		boolean found = false;
		for(String prices : inst.config.getConfigurationSection(pricepath).getKeys(false)) {
			if(inst.config.contains("aliases."+type+".names")) {
				for(String nick : inst.config.getStringList("aliases."+type+".names")) {
					nick = nick.replace("_", " ");
					if(nick.equalsIgnoreCase(prices)
					|| nick.replace(" ", "").equalsIgnoreCase(prices)
					|| nick.replace(" ", "_").equalsIgnoreCase(prices)) {
						cost = inst.config.getDouble(pricepath+"."+prices);
						found = true;
					}
				}
			}
		}
		
		//If it gets to this point it's not listed therefore it's unknown! :D
		if(found == false) {
			if(inst.config.getBoolean("options.chargeForUnknown.enabled") == true) {
				if((natural == true)
				|| (inst.config.getBoolean("options.chargeForUnknown.evenIfPlaced") == true && natural == false)) {
					cost = inst.config.getDouble("options.chargeForUnknown.price");
				} 
			}
		}
		
		//Taking discount into consideration
		double discount = 0;
		if((natural == true && inst.config.getBoolean("options.discountForNatural") == true)
		|| (natural == false && inst.config.getBoolean("options.discountForPlaced") == true)) {
			//Getting string of permission ready for simplicity 
			String permission = "se.discount.";
			if(natural == true) { permission = permission+"natural."; } else { permission = permission + "placed.";}
			for(int i = 0; i < 100; i++) {
				if(p.hasPermission(permission+i)) {
					if(discount  < i) {
						discount = i;
					}
				}
			}
			//Checking custom ones
			for(String node : inst.config.getConfigurationSection("options.discountNodes").getKeys(false)) {
				if(p.hasPermission("se.discount.custom."+node)) {
					if(discount < inst.config.getDouble("options.discountNodes.custom."+node)) {
						discount = inst.config.getDouble("options.discountNodes.custom."+node);
					}
				}
			}
			if(discount > 100) {
				discount = 100;
			}
			discount = discount/100;
		}
		
		//Now factoring in the actual cost
		double prediscount = cost * discount;
		cost = cost - prediscount;
		
		//Making sure money isn't gained on mine
		if(cost < 0) {
			cost = 0;
		}
		
		//Finally sending the cost
		return cost;
	}
	
	public static boolean isKnown(String type, Block block, Player p) {
		boolean found = false;
		//determing if we're using naturals or not
		Boolean natural = true;
		if(SqliteMain.checkData(block) == true) {
			natural = false;
		}
		if(inst.config.getBoolean("options.ignoreNaturalInput") == true) {
			natural = false;
		}
		//Getting the path of the prices
		String pricepath = "options.";
		if(natural == true) {
			pricepath = pricepath + "naturalPrices";
		} else {
			pricepath = pricepath + "placedPrices";
		}
		//check prices / aliases of given spawner
		for(String prices : inst.config.getConfigurationSection(pricepath).getKeys(false)) {
			for(String nick : inst.config.getStringList("aliases."+type+".names")) {
				nick = nick.replace("_", " ");
				if(nick.equalsIgnoreCase(prices)
				|| nick.replace(" ", "").equalsIgnoreCase(prices)
				|| nick.replace(" ", "_").equalsIgnoreCase(prices)) {
					found = true;
				}
			}
		}
		//Send if it's found or not
		return found;
	}
	
	
	@SuppressWarnings("deprecation")
	public static boolean checkIfWorking(CommandSender sender) {
		boolean working = true; 
		String currentkey = "";
		try {
			log("========== START ==========",sender);
			for(String key : inst.config.getConfigurationSection("aliases").getKeys(false)) {
				currentkey = key;
				EntityType e = EntityType.fromName(key);
				log("Entity = "+e.getName(), sender);
				String names = "none";
				for(String name : inst.config.getStringList("aliases."+key+".names")) {
					if(names.equals("none")) {
						names = "";
					}
					names = names + name +", ";
				}
				names = names.substring(0, names.length() - 2) + ".";
				log("Names = "+names,sender);
				log("Preferred Name = " + inst.config.getString("aliases."+key+".preferredname"),sender);
				log("--------------------",sender);
			}
			log("========== END ==========",sender);
		} catch (Exception ex) {
			log("Failed! Here are the details.",sender);
			//This is a small check to see if it's not an entity
			boolean exists = false;
			try {
				EntityType e = EntityType.fromName(currentkey);
				e.getName();
				exists = true;
			} catch (Exception e) { /*We don't care it failed the try, this is so the code keeps going. */ }
			log("EntityExists = "+exists,sender);
			log("Path = " + "aliases."+currentkey,sender);
			log("Preferredname = " + inst.config.getString("aliases."+currentkey+".preferredname"),sender);
			working = false;
			log("========== END ==========",sender);
		}
		return working;
	}
	
	private static void log(String message,CommandSender sender) {
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
	
} /* Working getcost before I fucked with it
public static double getCost(String type, Block block, Player p) {
double cost = 0;

//determing if we're using naturals or not
Boolean natural = true;
if(SqliteMain.checkData(block) == true) {
	natural = false;
}
if(inst.config.getBoolean("options.ignoreNaturalInput") == true) {
	natural = false;
}

//Little snare for free ones
if((natural == false && inst.config.getBoolean("options.chargeForPlaced") == false)
|| (natural == true && inst.config.getBoolean("options.chargeForNatural") == false)) {
	return cost;
}

//Getting the path of the prices
String pricepath = "options.";
if(natural == true) {
	pricepath = pricepath + "naturalPrices";
} else {
	pricepath = pricepath + "placedPrices";
}


//We have type, and the area we're going to check analyze!
boolean found = false;
for(String prices : inst.config.getConfigurationSection(pricepath).getKeys(false)) {
	for(String nick : inst.config.getConfigurationSection("aliases."+type+".names").getKeys(false)) {
		nick = nick.replace("_", " ");
		if(nick.equalsIgnoreCase(prices)
		|| nick.replace(" ", "").equalsIgnoreCase(prices)
		|| nick.replace(" ", "_").equalsIgnoreCase(prices)) {
			cost = inst.config.getDouble(pricepath+"."+prices);
			found = true;
		}
	}
}

//If it gets to this point it's not listed therefore it's unknown! :D
if(found == false) {
	if(inst.config.getBoolean("options.chargeForUnknown.enabled") == true) {
		if((natural == true)
		|| (inst.config.getBoolean("options.chargeForUnknown.evenIfPlaced") == true && natural == false)) {
			cost = inst.config.getDouble("options.chargeForUnknown.price");
		} 
	}
}

//Taking discount into consideration
double discount = 0;
if((natural == true && inst.config.getBoolean("options.discountForNatural") == true)
|| (natural == false && inst.config.getBoolean("options.discountForPlaced") == true)) {
	//Getting string of permission ready for simplicity 
	String permission = "se.discount.";
	if(natural == true) { permission = permission+"natural."; } else { permission = permission + "placed.";}
	for(int i = 0; i < 100; i++) {
		if(p.hasPermission(permission+i)) {
			if(discount  < i) {
				discount = i;
			}
		}
	}
	//Checking custom ones
	for(String node : inst.config.getConfigurationSection("options.discountNodes").getKeys(false)) {
		if(p.hasPermission("se.discount.custom."+node)) {
			if(discount < inst.config.getDouble("options.discountNodes.custom."+node)) {
				discount = inst.config.getDouble("options.discountNodes.custom."+node);
			}
		}
	}
	if(discount > 100) {
		discount = 100;
	}
	discount = discount/100;
}

//Now factoring in the actual cost
double prediscount = cost * discount;
cost = cost - prediscount;

//Making sure money isn't gained on mine
if(cost < 0) {
	cost = 0;
}

//Finally sending the cost
return cost;
}*/