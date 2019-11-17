package com.plochem.encireevents.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.plochem.encireevents.Event;

public class TemperatureEvent extends Event{

	private Location startLoc;
	private Location corner1;
	private Location corner2;
	private List<Location> validWoolLocations = new ArrayList<>();
	
	public TemperatureEvent(String name, int maxPlayers, Location specLoc, Location startLoc, Location corner1, Location corner2) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean lastStanding() {
		// TODO Auto-generated method stub
		return false;
	}

}
