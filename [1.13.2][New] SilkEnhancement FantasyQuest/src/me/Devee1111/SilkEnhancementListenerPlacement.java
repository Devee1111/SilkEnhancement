package me.Devee1111;



import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.Devee1111.Sqlite.SqliteMain;


public class SilkEnhancementListenerPlacement implements Listener {
	
	private SilkEnhancementMain main = SilkEnhancementMain.getInstance();
	SilkEnhancementMain inst;
	
	public SilkEnhancementListenerPlacement(SilkEnhancementMain p) {
		this.inst = p;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onSpawnerPlaced(BlockPlaceEvent e) {
		if(e.isCancelled() == false) {
			if(e.getBlock().getType().equals(Material.SPAWNER)) {
				Player p = e.getPlayer();	
				Block block = e.getBlock();
				sendSql(p,block,true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR) 
	public void onSpawnerMined(BlockBreakEvent e) {
		if(e.isCancelled() == false) {
			if(e.getBlock().getType().equals(Material.SPAWNER)) {
				Player p = e.getPlayer();
				Block block = e.getBlock();
				sendSql(p,block,false);
			}
		}
	}
	
	
	public void sendSql(Player p, Block block, Boolean createIndex) {
		String uuid = p.getUniqueId().toString();
		CreatureSpawner spawner = (CreatureSpawner) block;
		String type = spawner.getType().toString();
		main.debug(type);
		if(createIndex == true) {
			SqliteMain.addData(block, type, uuid);
		} else {
			SqliteMain.removeData(block, type, uuid);
		}
		
		
	}

}
