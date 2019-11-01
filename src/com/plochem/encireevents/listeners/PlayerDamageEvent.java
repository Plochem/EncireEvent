package com.plochem.encireevents.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.plochem.encireevents.EncireEvent;

public class PlayerDamageEvent implements Listener{
	@EventHandler
	public void onDamage(PlayerDamageEvent e) {
		EncireEvent plugin = EncireEvent.plugin;
	}

}
