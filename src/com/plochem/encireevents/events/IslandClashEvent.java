package com.plochem.encireevents.events;

import org.bukkit.Location;

import com.plochem.encireevents.Event;

public class IslandClashEvent extends Event{

	public IslandClashEvent(String name, int maxPlayers, Location specLoc) {
		super(name, maxPlayers, specLoc);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean lastStanding() {
		if(this.getPlayers().size() == 1) {
			return true;
		}
		return false;
	}

}
