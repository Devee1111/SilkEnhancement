package me.Devee1111;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.util.SilkUtil;

public class SilkEnhancementListenerDebug implements Listener {
	
	//Getting our main class for use
	private SilkEnhancementMain inst = SilkEnhancementMain.getInstance();
	//Getting SilkSpawnersApi for use
	@SuppressWarnings("unused")
	private SilkUtil su = inst.getsu();
	//Createing instance of our main class, and registering class
	SilkEnhancementMain instance;
	public SilkEnhancementListenerDebug(SilkEnhancementMain p) {
		this.instance = p;
		p.getServer().getPluginManager().registerEvents(this, p);
	}
	
	
	/* Gets the entity name for admins to know how to adjust config aliases */	
	@EventHandler (priority = EventPriority.HIGHEST) 
	public void onEntityHit(EntityDamageByEntityEvent e) {
		if(inst.config.getBoolean("options.debug") == true) {
			if(e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				e.setCancelled(true);
				inst.debug("Event cancelled due to debug! Disable with /sedebug.",p);
				inst.debug("Entity = "+e.getEntity().getType().toString(),p);
			}
		}
	}
	
	/* Gets the spawner name for admins to know how to adjust config aliases */
	@EventHandler (priority = EventPriority.HIGHEST) 
	public void onSpawnerHit(SilkSpawnersSpawnerBreakEvent e) {
		if(e.getBlock().getType().equals(Material.SPAWNER)) {
			if(inst.config.getBoolean("options.debug") == true) {
				if(e.getPlayer().hasPermission("se.debug")) {
					e.setCancelled(true);
					CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
					inst.debug("Event cancelled due to debug! Disable with /sedebug.",e.getPlayer());
					inst.debug("SpawnerType = "+spawner.getSpawnedType().toString(), e.getPlayer());
				}
			}
		}
	}
	
	/* Gets the spawner name for admins to know how to adjust config aliases */
	@EventHandler  
	public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent e) {
		if(e.getBlock().getType().equals(Material.SPAWNER)) {
			if(inst.config.getBoolean("options.debug") == true) {
				if(e.getPlayer().hasPermission("se.debug")) {
					e.setCancelled(true);
					CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
					inst.debug("Event cancelled due to debug! Disable with /sedebug.",e.getPlayer());
					inst.debug("SpawnerType = "+spawner.getSpawnedType().toString(), e.getPlayer());
				}
			}
		}
	}
	
	/* Warning to admins about debug being on to be aware of potential spam or problems they may receive */
	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasPermission("se.debug")) {
			if(inst.config.getBoolean("options.debug") == true) {
				//Warning about debug, since it's possible config is dead/broken, we do checks, but no matter what a message is sent
				String tosend = "";
				if(inst.config.contains("messages.debugWarning")) {
					tosend = inst.config.getString("messages.debugWarning");
				} else {
					tosend = "%prefix% &cWARNING! &bDebug mode is enabled, this will cause spam. Disable with /sedebug";
				}
				if(inst.config.contains("options.prefix")) {
					tosend = tosend.replace("%prefix%", inst.config.getString("options.prefix"));
				} else {
					tosend.replace("%prefix%", "&8(&3Spawners&8)");
				}
				p.sendMessage(inst.color(tosend));
			}
		}
	}

}
