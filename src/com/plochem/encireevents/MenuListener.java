package com.plochem.encireevents;

import java.util.List;

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
import com.plochem.encireevents.events.TemperatureEvent;
import com.plochem.encireevents.events.WaterdropEvent;


public class MenuListener implements Listener{

	private EncireEvent eventPlugin;

	public MenuListener(EncireEvent eventPlugin) {
		this.eventPlugin = eventPlugin;
	}

	@SuppressWarnings("unchecked")
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
						eventPlugin.setEvent(new FFAEvent("FFA", eventPlugin.getEventConfig().getInt("player-limit-ffa"), (Location)specLocs.get("ffa"), 
											(Location)eventPlugin.getEventConfig().get("ffa-startLoc")));
					} else if(items.getItemStack("waterdrop.item").equals(i)) {
						eventPlugin.setEvent(new WaterdropEvent("Waterdrop", eventPlugin.getEventConfig().getInt("player-limit-waterdrop"), 
											(Location)specLocs.get("waterdrop"), (Location)eventPlugin.getEventConfig().get("waterdrop-startLoc"),
											(Location)eventPlugin.getEventConfig().get("waterdrop-corner1"), (Location)eventPlugin.getEventConfig().get("waterdrop-corner2")));
					} else if(items.getItemStack("sumo.item").equals(i)) {
						p.sendMessage(eventPlugin.msgFormat("&cComing soon!"));
						return;
					} else if(items.getItemStack("temperature.item").equals(i)) {
						eventPlugin.setEvent(new TemperatureEvent("Temperature", eventPlugin.getEventConfig().getInt("player-limit-temperature"), 
											(Location)specLocs.get("temperature"), (Location)eventPlugin.getEventConfig().get("temperature-startLoc"),
											(Location)eventPlugin.getEventConfig().get("temperature-corner1"), (Location)eventPlugin.getEventConfig().get("temperature-corner2")));
					} else if(items.getItemStack("islandclash.item").equals(i)) {
						eventPlugin.setEvent(new IslandClashEvent("IslandClash", eventPlugin.getEventConfig().getInt("player-limit-islandclash"), 
											(Location)specLocs.get("islandclash"), (List<Location>)eventPlugin.getEventConfig().getList("islandclash-startLoc")));
					} else {
						return;
					}
					p.sendMessage(eventPlugin.msgFormat(eventPlugin.getMessageConfig().getString("event-created")));
					Bukkit.broadcastMessage(eventPlugin.getMessageConfig().getString("event-notify-all"));
					eventPlugin.getEvent().startCountdown();
				}

			}
		}

	}

}
