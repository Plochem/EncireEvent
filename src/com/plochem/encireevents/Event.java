package com.plochem.encireevents;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
		new BukkitRunnable() {
			EncireEvent plugin = EncireEvent.plugin;
			int[] broadcastTimes = Arrays.asList(plugin.getEventConfig().getString("broadcast-times").split(",")).stream().mapToInt(Integer::parseInt).toArray();
			long time = plugin.getEventConfig().getLong("time");
			public void run() {
				for(int t : broadcastTimes) {
					if(time == t) {
						Bukkit.broadcastMessage(plugin.msgFormat(plugin.getMessageConfig().getString("countdown-message")).replaceAll("%seconds%", String.valueOf(time)));
					}
				}
				if(time == 0) {
					if(players.size() >= 2) {
						start();
						sendMessage(EncireEvent.plugin.msgFormat(EncireEvent.plugin.getMessageConfig().getString("event-started")));
						setStarted(true);
					} else {
						end();
						Bukkit.broadcastMessage(plugin.msgFormat(plugin.getMessageConfig().getString("event-not-enough-players")));
						setStarted(false);
					}
				}
				time--;
			}
		}.runTaskTimer(EncireEvent.plugin,0,20); // run every second
	}
	
	public boolean isPlayer(UUID uuid) {
		if(players.contains(uuid)) {
			return true;
		}
		return false;
	}
	
	public boolean isSpectator(UUID uuid) {
		if(spectators.contains(uuid)) {
			return true;
		}
		return false;
	}
	
	public void playerToSpecator(UUID id) {
		players.remove(id);
		spectators.add(id);
		Bukkit.getPlayer(id).teleport(this.getSpecLocation());
	}
	
	public void end() {
		EncireEvent plugin = EncireEvent.plugin;
		this.setStarted(false);
		this.sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("event-end-notify-all")));
		for(UUID id : players) {
			
		}
		for(UUID id : spectators) {
			
		}
		plugin.setEvent(null);
		
	}

	public abstract void start();
	public abstract boolean lastStanding();

}
