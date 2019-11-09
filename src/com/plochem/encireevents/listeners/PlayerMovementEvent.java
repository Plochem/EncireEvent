package com.plochem.encireevents.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;
import com.plochem.encireevents.events.WaterdropEvent;

public class PlayerMovementEvent implements Listener {
	
	private EncireEvent plugin = EncireEvent.plugin;
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Event event = plugin.getEvent();
		if(event==null) return;
		if(event instanceof WaterdropEvent && event.isPlayer(e.getPlayer().getUniqueId())) {
			Material curr = e.getPlayer().getLocation().getBlock().getType();
			if(curr == Material.WATER || curr == Material.STATIONARY_WATER) {
				((WaterdropEvent)event).addPassedPlayer(e.getPlayer().getUniqueId());
				e.getPlayer().sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("waterdrop-sucess")));
			}
		}
	}

}
