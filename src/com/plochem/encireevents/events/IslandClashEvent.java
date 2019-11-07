package com.plochem.encireevents.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.plochem.encireevents.Event;

public class IslandClashEvent extends Event{
	
	private List<Location> startLoc;

	public IslandClashEvent(String name, int maxPlayers, Location specLoc, List<Location> startLoc) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
	}

	@Override
	public void start() {
		for(int i = 0; i < this.getPlayers().size(); i++) {
			Player p = Bukkit.getPlayer(this.getPlayers().get(i));
			p.teleport(startLoc.get(i));
		}
	}
	
	@Override
	public boolean lastStanding() {
		if(this.getPlayers().size() == 1) {
			return true;
		}
		return false;
	}

}
