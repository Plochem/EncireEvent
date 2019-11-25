package com.plochem.encireevents.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class PlayerConnectEvent implements Listener{
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		Event event = EncireEvent.plugin.getEvent();
		if(event==null) return;
		if(event.isPlayer(e.getPlayer().getUniqueId()) || event.isSpectator(e.getPlayer().getUniqueId())){
			event.removePlayer(e.getPlayer());
		}
	}

}
