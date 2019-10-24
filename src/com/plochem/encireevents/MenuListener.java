package com.plochem.encireevents;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.plochem.encireevents.events.FFAEvent;


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
					eventPlugin.sendMsg(p, eventPlugin.getMessageonfig().getString("event-already-exists"));
				} else {
					if(items.getItemStack("ffa.item").equals(i)) {
						eventPlugin.setEvent(new FFAEvent());
					} else if(items.getItemStack("waterdrop.item").equals(i)) {

					} else if(items.getItemStack("sumo.item").equals(i)) {

					} else if(items.getItemStack("temperature.item").equals(i)) {

					} else if(items.getItemStack("islandclash.item").equals(i)) {

					}
					eventPlugin.sendMsg(p, eventPlugin.getMessageonfig().getString("event-created"));
				}

			}
		}

	}

}
