package com.plochem.encireevents;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Event {
	private List<UUID> players = new ArrayList<>();	
	private List<UUID> spectators = new ArrayList<>();	
	private Location specLoc;
	private boolean started;
	
	public List<UUID> getPlayers(){
		return players;
	}
	
	public List<UUID> getSpectators(){
		return spectators;
	}
	
	public Location getSpecLocation(){
		return specLoc;
	}
	
	public boolean hasStarted() {
		return started;
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}
	
	public void sendMessage(String msg) {
		for(UUID id : players) {
			Bukkit.getPlayer(id).sendMessage(msg);
		}
		for(UUID id : spectators) {
			Bukkit.getPlayer(id).sendMessage(msg);
		}
	}
	
	public void addPlayer(Player p) {
		players.add(p.getUniqueId());
		p.teleport(specLoc);
	}
	
	public void addSpectator(Player p) {
		spectators.add(p.getUniqueId());
		p.teleport(specLoc);
	}
	
	public abstract void start();
	public abstract void end();
	
}
