package com.plochem.encireevents.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class PlayerDamageEvent implements Listener{
	
	private EncireEvent plugin = EncireEvent.plugin;
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) { //player dmg player
			Event event = plugin.getEvent();
			if(event==null) return;
			if(event.isSpectator(e.getEntity().getUniqueId()) && event.isSpectator(e.getDamager().getUniqueId())) { // if both spectator -> no dmg
				e.setCancelled(true);
			} else if(event.isPlayer(e.getEntity().getUniqueId()) && event.isPlayer(e.getDamager().getUniqueId())) { // if both player in event
				if(!event.hasStarted()) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(PlayerDeathEvent e) {
		Event event = plugin.getEvent();
		if(event==null) return;
		if(event.isPlayer(e.getEntity().getUniqueId())) {
			e.getEntity().teleport(event.getSpecLocation());
			event.playerToSpecator(e.getEntity().getUniqueId());
			event.sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("player-eliminated").replaceAll("%player%", e.getEntity().getName())));
		}
	}

}
