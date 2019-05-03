package me.Devee1111;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.Devee1111.Aliases.AliasesMain;
import me.Devee1111.Vault.SEVaultMain;

public class SilkEnhancementListenerSpawners implements Listener {
	
	//Getting our main class for use
	private SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	
	//Making instance of our main class, vault, Aliases, and registering listener
	SilkEnhancementMain instance;
	SEVaultMain vault;
	public SilkEnhancementListenerSpawners(SilkEnhancementMain p) {
		p.getServer().getPluginManager().registerEvents(this, p);
		this.instance = p;
	}
	
	/* Handling the mine event */
	@EventHandler 
	public void onSpawnerMine(BlockBreakEvent e) {
		if(e.getBlock().getType().equals(Material.SPAWNER)) {
			if(e.isCancelled() == false) {
				Player p = e.getPlayer();
				CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
				double cost = AliasesMain.getCost(spawner.getSpawnedType().toString(), e.getBlock(), e.getPlayer());
				Object o = AliasesMain.getCostObject(spawner.getSpawnedType().toString(), e.getBlock(), e.getPlayer());
			    System.out.println(o.toString());
				//Snare if it's free
				if(cost == 0) {
					if(inst.config.getBoolean("options.sendMessageIfFree") == true) {
						inst.sendCustomMessage(p, spawner,"spawnerMinedForFree",cost);
					}
					return;
				}
				//Now if they have enough, charge, if not, don't
				if(SEVaultMain.hasEnough(p, cost)) {
					SEVaultMain.takeMoney(cost, p);
					inst.sendCustomMessage(p, spawner, "spawnerMined", cost);
				} else {
					inst.sendCustomMessage(p, spawner, "notEnoughMoney", cost);
					e.setCancelled(true);
				}
				
			}
		}
	}
}
