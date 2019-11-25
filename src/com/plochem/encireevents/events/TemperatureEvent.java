package com.plochem.encireevents.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class TemperatureEvent extends Event{

	private Location startLoc;
	private Location corner1;
	private Location corner2;
	private List<Location> validWoolLocations = new ArrayList<>();
	private List<Location> changedWoolLocations = new ArrayList<>();
	private List<Location> copyValid;

	public TemperatureEvent(String name, int maxPlayers, Location specLoc, Location startLoc, Location corner1, Location corner2) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
		this.corner1 = corner1;
		this.corner2 = corner2;
		setValidWoolLocations();
	}

	@Override
	public void start() {
		for(UUID id : this.getPlayers()) {
			Player p = Bukkit.getPlayer(id);
			p.teleport(startLoc);
			p.setMaxHealth(2.0);
		}
		copyValid = new ArrayList<>(validWoolLocations);
		Collections.shuffle(copyValid);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!hasStarted() || EncireEvent.plugin.getEvent() == null) {
					this.cancel(); // cancels runnable when end() is called in another listener
					for(Location loc : validWoolLocations) {
						loc.getBlock().setType(Material.WOOL); // regen wool blocks
					}
				} else {
					setRandomBlocks();
				}
			}
		}.runTaskTimer(EncireEvent.plugin,0,100); // run every 5 seconds
	}

	@Override
	public boolean lastStanding() {
		if(this.getPlayers().size() == 1) {
			return true;
		}
		return false;
	}
	
	private void setRandomBlocks() {
		if(copyValid.size() > 0) {
			Random ran = new Random();
			int lim = (int) (ran.nextInt(copyValid.size()) * 0.40) + 1;
			changedWoolLocations.addAll(copyValid.subList(0, lim));
			copyValid.removeAll(copyValid.subList(0, lim));
		}
		for(int i = 0; i < changedWoolLocations.size(); i++) {
			BlockState state = changedWoolLocations.get(i).getBlock().getState();
			MaterialData md = state.getData();
			if(md instanceof Wool) {
				Wool wool = (Wool)md;
				DyeColor dye = wool.getColor();
				if(dye == DyeColor.WHITE) {
					wool.setColor(DyeColor.YELLOW);
				} else if(dye == DyeColor.YELLOW) {
					wool.setColor(DyeColor.ORANGE);
				} else if(dye == DyeColor.ORANGE) {
					wool.setColor(DyeColor.RED);
				} else if(dye == DyeColor.RED) {
					changedWoolLocations.get(i).getBlock().setType(Material.AIR); // remove block
					continue;
				}
				state.setData(wool);
				state.update(true, true);
			}
		}
	}
	
	private void setValidWoolLocations() {
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
					if(mat == Material.WOOL) {
						validWoolLocations.add(new Location(startLoc.getWorld(), x, y, z));
					}
				}
			}
		}
	}
	
	public List<Location> getValidWoolLocations(){
		return validWoolLocations;
	}
}
