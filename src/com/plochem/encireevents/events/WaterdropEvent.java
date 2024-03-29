package com.plochem.encireevents.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
	private List<Location> validWaterLocations = new ArrayList<>();
	private double[] percents = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
	private int[] broadcastTimes = {60, 30, 10, 5, 4, 3, 2, 1};
	
	public WaterdropEvent(String name, int maxPlayers, Location specLoc, Location startLoc, Location corner1, Location corner2) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
		this.corner1 = corner1;
		this.corner2 = corner2;
		populateWaterLocations();
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
				if(!hasStarted() || plugin.getEvent() == null) this.cancel(); // cancels runnable when event is over
				if(time == 60) {
					sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("new-waterdrop-round")));
					setRandomBlocks();
				} else if(time == 1) {
					Iterator<UUID> playersIterator = getPlayers().iterator();
					while (playersIterator.hasNext()) {
						UUID id = playersIterator.next();
						if(passed.contains(id)) {
							Bukkit.getPlayer(id).teleport(startLoc);
						} else { // stayed at spawn
							getSpectators().add(id);
							Bukkit.getPlayer(id).teleport(getSpecLocation());
							sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("player-eliminated").replaceAll("%player%", Bukkit.getPlayer(id).getName())));
							playersIterator.remove();
						}
					}
					passed.clear();
					time = 61;
				}
				for(int t : broadcastTimes) {
					if(time == t) {
						sendMessage(plugin.msgFormat(plugin.getMessageConfig().getString("waterdrop-countdown").replaceAll("%time%", String.valueOf(time))));
					}
				}
				time--;
				
			}
		}.runTaskTimer(EncireEvent.plugin,0,20); // run every second
	}
	
	private void setRandomBlocks() {
		List<Location> copy = new ArrayList<>(validWaterLocations);
		for(Location loc : copy) {
			loc.getBlock().setType(Material.WATER);
		}
		Collections.shuffle(copy);
		Random ran = new Random();
		double percent = percents[ran.nextInt(percents.length)];
		int lim = (int) (copy.size() * percent);
		for(int i = 0; i <= lim; i++) {
			copy.get(i).getBlock().setType(Material.BRICK);
		}
		
	}
	
	private void populateWaterLocations() {
		int smallX = corner1.getBlockX() < corner2.getBlockX() ? corner1.getBlockX() : corner2.getBlockX();
		int smallY = corner1.getBlockY() < corner2.getBlockY() ? corner1.getBlockY() : corner2.getBlockY();
		int smallZ = corner1.getBlockZ() < corner2.getBlockZ() ? corner1.getBlockZ() : corner2.getBlockZ();
		
		int largeX = corner1.getBlockX() > corner2.getBlockX() ? corner1.getBlockX() : corner2.getBlockX();
		int largeY = corner1.getBlockY() > corner2.getBlockY() ? corner1.getBlockY() : corner2.getBlockY();
		int largeZ = corner1.getBlockZ() > corner2.getBlockZ() ? corner1.getBlockZ() : corner2.getBlockZ();
		
		for(int x = smallX; x <= largeX; x++) {
			for(int y = smallY; y <= largeY; y++) {
				for(int z = smallZ; z <= largeZ; z++) {
					Material mat = startLoc.getWorld().getBlockAt(x,y,z).getType();
					if(mat == Material.WATER || mat  == Material.STATIONARY_WATER) {
						validWaterLocations.add(new Location(startLoc.getWorld(), x, y, z));
					}
				}
			}
		}
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
	
	public List<UUID> getPassed(){
		return passed;
	}
	
	public List<Location> getValidWaterLocations(){
		return validWaterLocations;
	}

}
