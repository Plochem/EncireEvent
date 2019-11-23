package com.plochem.encireevents.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;
import com.plochem.encireevents.events.IslandClashEvent;

public class BlockEvent implements Listener{
	
	private EncireEvent plugin = EncireEvent.plugin;
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		Event event = plugin.getEvent();
		if(event==null) return;
		if(!(event instanceof IslandClashEvent)) {
			if(event.getPlayers().contains(e.getPlayer().getUniqueId()) || event.getSpectators().contains(e.getPlayer().getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

}
