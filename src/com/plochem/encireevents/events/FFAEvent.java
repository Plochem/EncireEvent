package com.plochem.encireevents.events;

import org.bukkit.Location;

import com.plochem.encireevents.Event;

public class FFAEvent extends Event{
	
	private Location startLoc;

	public FFAEvent(String name, int maxPlayers, Location specLoc, Location startLoc) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
	}
	
	public Location getStartLoc() {
		return startLoc;
	}
}
