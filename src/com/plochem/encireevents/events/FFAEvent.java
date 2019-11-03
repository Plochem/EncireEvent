package com.plochem.encireevents.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.plochem.encireevents.Event;

public class FFAEvent extends Event{
	
	private Location startLoc;

	public FFAEvent(String name, int maxPlayers, Location specLoc, Location startLoc) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
	}

	@Override
	public void start() {
		for(UUID id : this.getPlayers()) {
			Player p = Bukkit.getPlayer(id);
			p.teleport(startLoc);
			p.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.IRON_BOOTS), 
															 	new ItemStack(Material.IRON_LEGGINGS), 
															 	new ItemStack(Material.IRON_CHESTPLATE),
															 	new ItemStack(Material.IRON_HELMET),});
			p.getInventory().addItem(new ItemStack(Material.IRON_SWORD), 
									new ItemStack(Material.BOW), 
									new ItemStack(Material.GOLDEN_APPLE, 10),
									new ItemStack(Material.ARROW, 16));
		}
		
	}

	@Override
	public void end() {
		this.setStarted(false);
		
	}
	
	public Location getStartLoc() {
		return startLoc;
	}
}
