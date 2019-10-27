package com.plochem.encireevents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.plochem.encireevents.events.FFAEvent;
import com.plochem.encireevents.events.IslandClashEvent;


public class MenuListener implements Listener{

	private EncireEvent eventPlugin;

	public MenuListener(EncireEvent eventPlugin) {
		this.eventPlugin = eventPlugin;
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(e.getInventory().getName().equalsIgnoreCase(eventPlugin.getMenuConfig().getString("menu-name"))) {
			e.setCancelled(true);
			Player p = (Player)e.getWhoClicked();
			if(e.getClickedInventory().getName().equalsIgnoreCase(eventPlugin.getMenuConfig().getString("menu-name"))) {
				ConfigurationSection items = eventPlugin.getMenuConfig().getConfigurationSection("menu-items");
				ItemStack i = e.getCurrentItem();
				if(eventPlugin.getEvent() != null) {
					p.sendMessage(eventPlugin.msgFormat(eventPlugin.getMessageConfig().getString("event-already-exists")));
				} else {
					ConfigurationSection specLocs = eventPlugin.getEventConfig().getConfigurationSection("spectator-spawn");
					if(items.getItemStack("ffa.item").equals(i)) {
						eventPlugin.setEvent(new FFAEvent("FFA", eventPlugin.getEventConfig().getInt("player-limit-ffa"), (Location)specLocs.get("ffa")));
					} else if(items.getItemStack("waterdrop.item").equals(i)) {

					} else if(items.getItemStack("sumo.item").equals(i)) {

					} else if(items.getItemStack("temperature.item").equals(i)) {

					} else if(items.getItemStack("islandclash.item").equals(i)) {
						eventPlugin.setEvent(new IslandClashEvent("IslandClash", eventPlugin.getEventConfig().getInt("player-limit-islandclash"), (Location)specLocs.get("islandclash")));
					}
					p.sendMessage(eventPlugin.msgFormat(eventPlugin.getMessageConfig().getString("event-created")));
					Bukkit.broadcastMessage(eventPlugin.getMessageConfig().getString("event-notify-all"));
					eventPlugin.getEvent().startCountdown();
				}

			}
		}

	}

}
