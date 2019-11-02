package com.plochem.encireevents.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class PlayerDamageEvent implements Listener{
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		EncireEvent plugin = EncireEvent.plugin;
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) { //player dmg player
			Event event = plugin.getEvent();
			if(event.isSpectator(e.getEntity().getUniqueId()) && event.isSpectator(e.getDamager().getUniqueId())) {
				
			} else if(event.isPlayer(e.getEntity().getUniqueId()) && event.isPlayer(e.getDamager().getUniqueId())) {
				
			}
		}
	}

}
