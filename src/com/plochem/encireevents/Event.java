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
	private int maxPlayers;
	private String name;
	
	
	public Event(String name, int maxPlayers, Location specLoc) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.specLoc = specLoc;
	}
	
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
	
	public int getMaxPlayers(){
		return maxPlayers;
	}
	
	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers = maxPlayers;
	}
	
	public String getName() {
		return name;
	}
	
	public void sendMessage(String msg) {
		for(UUID id : players) {
			Bukkit.getPlayer(id).sendMessage(EncireEvent.plugin.msgFormat(msg));
		}
		for(UUID id : spectators) {
			Bukkit.getPlayer(id).sendMessage(EncireEvent.plugin.msgFormat(msg));
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
	
	public boolean isFull() {
		if(players.size() >= maxPlayers) {
			return true;
		}
		return false;
	}
	
	public void removePlayer(Player p) {
		players.remove(p.getUniqueId());
		spectators.remove(p.getUniqueId());
	}
	
	public void startCountdown() {
		Bukkit.getServer().getScheduler().runTaskLater(EncireEvent.plugin, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}, EncireEvent.plugin.getEventConfig().getLong("time"));
	}
	
	public abstract void start();
	public abstract void end();
	
}
