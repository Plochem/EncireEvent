package com.plochem.encireevents.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;
import com.plochem.encireevents.events.TemperatureEvent;
import com.plochem.encireevents.events.WaterdropEvent;

public class PlayerDamageEvent implements Listener{
	
	private EncireEvent plugin = EncireEvent.plugin;
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) { //player dmg player
			Event event = plugin.getEvent();
			if(event==null) return;
			if(event instanceof WaterdropEvent || event instanceof TemperatureEvent) {
				e.setCancelled(true);
				return;
			}
			if(event.isSpectator(e.getEntity().getUniqueId()) || event.isSpectator(e.getDamager().getUniqueId())) { // if one is spectator -> no dmg
				e.setCancelled(true);
			} else if(event.isPlayer(e.getEntity().getUniqueId()) && event.isPlayer(e.getDamager().getUniqueId())) { // if both are players
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
			Player p = e.getEntity();
			event.sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("player-eliminated").replaceAll("%player%", p.getName())));
			event.playerToSpecator(p.getUniqueId());
			p.spigot().respawn();
			p.teleport(event.getSpecLocation());
			e.getDrops().clear();
			if(event.lastStanding()) {
				event.end();
			}
		}
	}

}
