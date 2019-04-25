package me.Devee1111;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Devee1111.Aliases.AliasesMain;

public class SilkEnhancementCommandCheck implements CommandExecutor {

	
	//Making our main class var for use of our main class
	private SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	//Making our instance
	public SilkEnhancementMain instance;
	public SilkEnhancementCommandCheck(SilkEnhancementMain instance) {
	  this.instance = instance;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender.hasPermission("se.command.check")) {
			boolean check = AliasesMain.checkIfWorking(sender);
			if(check == true) {
				inst.sendMessage(sender,"messages.checkSuccess");
			} else {
				inst.sendMessage(sender,"messages.checkFailure");
			}
			return true;
		}
		inst.sendMessage(sender, "nopermission");
		return true;
	}

}
