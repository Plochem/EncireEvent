package com.plochem.encireevents.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class WaterdropEvent extends Event{
	
	private Location startLoc;
	private List<UUID> passed = new ArrayList<>();
	private EncireEvent plugin = EncireEvent.plugin;
	private Location corner1;
	private Location corner2;
	
	public WaterdropEvent(String name, int maxPlayers, Location specLoc, Location startLoc, Location corner1, Location corner2) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}


	@Override
	public void start() {
		for(UUID id : this.getPlayers()) {
			Player p = Bukkit.getPlayer(id);
			p.teleport(startLoc);
			p.setMaxHealth(2.0);
		}
		new BukkitRunnable() {
			int time = 60;
			@Override
			public void run() {
				if(!hasStarted()) this.cancel(); // cancels runnable when end() is called in another listener
				if(time == 60) {
					sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("new-waterdrop-round")));
				} else if(time == 0) {
					for(UUID id : getPlayers()) {
						if(passed.contains(id)) {
							Bukkit.getPlayer(id).teleport(startLoc);
						} else { // stayed at spawn
							playerToSpecator(id);
							sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("player-eliminated").replaceAll("%player%", Bukkit.getPlayer(id).getName())));
						}
					}
					if(lastStanding()) {
						this.cancel();
						end();
					}
					passed.clear();
					time = 60;
				}
				time--;
				
			}
		}.runTaskTimer(EncireEvent.plugin,0,20); // run every second
	}

	@Override
	public boolean lastStanding() {
		if(this.getPlayers().size() == 1) {
			return true;
		}
		return false;
	}
	
	public void addPassedPlayer(UUID id) {
		passed.add(id);
	}

}
