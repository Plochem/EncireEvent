package com.plochem.encireevents;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.plochem.encireevents.events.IslandClashEvent;
import com.plochem.encireevents.events.TemperatureEvent;
import com.plochem.encireevents.events.WaterdropEvent;

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
		p.setMaxHealth(20.0);
		p.setHealth(20.0);
		players.remove(p.getUniqueId());
		spectators.remove(p.getUniqueId());
		if(this.lastStanding() && started) end();
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
						plugin.getEvent().sendMessage(EncireEvent.plugin.msgFormat(EncireEvent.plugin.getMessageConfig().getString("event-started")));
						setStarted(true);
					} else {
						Bukkit.broadcastMessage(plugin.msgFormat(plugin.getMessageConfig().getString("event-not-enough-players")));
						end();
					}
					this.cancel();
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
		Player player = Bukkit.getPlayer(id);
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}

	public void end() {
		EncireEvent plugin = EncireEvent.plugin;
		this.setStarted(false);
		this.sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("event-end-notify-all"))); // TODO winners (team)
		Iterator<UUID> playersIterator = players.iterator();
		while (playersIterator.hasNext()) {
			UUID id = playersIterator.next();
			spectators.add(id);
			Bukkit.getPlayer(id).getInventory().clear();
			playersIterator.remove();
		}
		for(UUID id : spectators) {
			for(String cmd : plugin.getEventConfig().getStringList("event-end-commands")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", Bukkit.getPlayer(id).getName()));
			}
			Bukkit.getPlayer(id).setMaxHealth(20.0);
			Bukkit.getPlayer(id).setHealth(20.0);
		}
		
		if(this instanceof WaterdropEvent) {
			for(Location loc : ((WaterdropEvent)this).getValidWaterLocations()) {
				loc.getBlock().setType(Material.WATER);
			}
		} else if(this instanceof TemperatureEvent) {
			for(Location loc : ((TemperatureEvent)this).getValidWoolLocations()) {
				loc.getBlock().setType(Material.WOOL);
			}
		} else if(this instanceof IslandClashEvent) {
			((IslandClashEvent)this).rollback();
		}
		plugin.setEvent(null);

	}

	public abstract void start();
	public abstract boolean lastStanding();

}
