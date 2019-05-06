package me.Devee1111;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Devee1111.Sqlite.SqliteMain;

public class SilkEnhancementCommandReset implements CommandExecutor {
	
	private SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	SilkEnhancementMain instance;
	public SilkEnhancementCommandReset(SilkEnhancementMain instance) {
		this.instance = instance;
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("se.command.reset")) {
			if(args.length == 1) {
				for(World w : inst.getServer().getWorlds()) {
					if(w.getName().equals(args[0])) {
						boolean deleted = SqliteMain.deleteWorld(w);
						String path = "messages.world";
						if(deleted == true) {
							path = path + "Deleted";
						} else {
							path = path + "FailedToDelete";
						}
						String tosend = inst.createMessage(path);
						tosend = tosend.replace("%world%", w.getName());
						tosend = inst.color(tosend);
						sender.sendMessage(tosend);
						return true;
					}
				}
				//World doesn't exist
				String tosend = inst.createMessage("messages.worldNotExist");
				tosend = tosend.replace("%arg%", args[0]);
				tosend = inst.color(tosend);
				sender.sendMessage(tosend);
				return true;
			} else {
				inst.sendMessage(sender, "messages.wrongArgs");
				return true;
			}
		} else {
			inst.sendMessage(sender, "nopermission");
			return true;
		}
	}
	

}
