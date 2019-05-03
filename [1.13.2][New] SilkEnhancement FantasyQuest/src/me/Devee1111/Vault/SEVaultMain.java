package me.Devee1111.Vault;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.Devee1111.SilkEnhancementMain;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class SEVaultMain {
	
	private static SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	//Making instance of our main class
	SilkEnhancementMain instance;
	public SEVaultMain(SilkEnhancementMain instance) {
		this.instance = instance;
	}
	
	//Easier for code to remove and do all checks of removing balance here, also sends message if desired
	/* Might rework the boolean to be a string, which is a path in the config. */
	public static boolean takeMoney(double amount, Player p, boolean sendMessage) {
		Economy econ = SilkEnhancementMain.getEconomy();
		OfflinePlayer op = (OfflinePlayer) p;
		if(econ.has(op, amount)) {
			EconomyResponse er = econ.withdrawPlayer(op, amount);
			if(er.transactionSuccess()) {
				String tosend = inst.createMessage("moneyWithdrawn");
				tosend = tosend.replace("%cost%", Double.toString(amount));
				tosend = tosend.replace("%balance%", Double.toString(econ.getBalance(op)));
				tosend = inst.color(tosend);
				if(sendMessage == true) {
					p.sendMessage(tosend);
				}
				return true;
			}
		} else {
			String tosend = inst.createMessage("notEnoughMoney");
			tosend = tosend.replace("%balance%", Double.toString(econ.getBalance(op)));
			tosend = tosend.replace("%cost%", Double.toString(amount));
			Double cost = amount - econ.getBalance(op);
			tosend = tosend.replace("%need%", Double.toString(cost));
			tosend = inst.color(tosend);
			p.sendMessage(tosend);
		}
		return false;
	}
	
	/* Simpler method than the one above that just takes the money */
	public static boolean takeMoney(Double amount, Player p) {
		Economy econ = SilkEnhancementMain.getEconomy();
		OfflinePlayer op = (OfflinePlayer) p;
		boolean success = false;
		if(econ.has(op, amount)) {
			EconomyResponse er = econ.withdrawPlayer(op, amount);
			if(er.transactionSuccess() == true) {
				success = true;
			}
		}
		return success;
	}
	
	//Check if player has enough
	public static boolean hasEnough(Player p, double amount) {
		Economy econ = SilkEnhancementMain.getEconomy();
		boolean hasenough = false;
		OfflinePlayer op = (OfflinePlayer) p;
		if(econ.has(op, amount)) {
			hasenough = true;
		}
		return hasenough;
	}
	
	//Get the balance of player
	public static double getBalance(Player p) {
		Economy econ = SilkEnhancementMain.getEconomy();
		OfflinePlayer op = (OfflinePlayer) p;
		return econ.getBalance(op);
	}
}
