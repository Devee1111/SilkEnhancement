package me.Devee1111;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Devee1111.Aliases.AliasesMain;

public class SilkEnhancementCommandCheck implements CommandExecutor {

	
	private SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	
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
