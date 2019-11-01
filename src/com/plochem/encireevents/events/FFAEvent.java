package com.plochem.encireevents.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class FFAEvent extends Event{
	
	private Location startLoc;

	public FFAEvent(String name, int maxPlayers, Location specLoc, Location startLoc) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
	}

	@Override
	public void start() {
		for(UUID id : this.getPlayers()) {
			Bukkit.getPlayer(id).teleport(startLoc);
		}
		this.sendMessage(EncireEvent.plugin.msgFormat(EncireEvent.plugin.getMessageConfig().getString("event-started")));
		this.setStarted(true);
	}

	@Override
	public void end() {
		this.setStarted(false);
		// TODO Auto-generated method stub
	}
	
	public Location getStartLoc() {
		return startLoc;
	}
}
