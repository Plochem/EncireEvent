package com.plochem.encireevents;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EncireEvent extends JavaPlugin{	
	private Event event = null;
	private File messageFile = new File(this.getDataFolder(), "messages.yml");
	private YamlConfiguration messages;
	private File hostMenuFile = new File(this.getDataFolder(), "host-menu.yml");
	private YamlConfiguration hostMenu;
	
	public void onEnable() {
		getLogger().info("____________________________");
		getLogger().info("Plugin developed by Plochem");
		getLogger().info("https://github.com/Plochem");
		getLogger().info("____________________________");
		registerThings();
		createFiles();
	}
	
	public void registerThings() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.addPermission(new Permission("events.reload"));
		pm.addPermission(new Permission("events.host"));
	}
	
	public void createFiles() {
		if(!messageFile.exists()) {
			messageFile.getParentFile().mkdirs();
			saveResource("messages.yml", false);
		}
		messages = YamlConfiguration.loadConfiguration(messageFile);
		if(!hostMenuFile.exists()) {
			hostMenuFile.getParentFile().mkdirs();
			saveResource("host-menu.yml", false);
		}
		hostMenu = YamlConfiguration.loadConfiguration(hostMenuFile);
	}
	
	public void save(File f, YamlConfiguration c) {
		try {
			c.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(command.getName().equalsIgnoreCase("events")) {
			if(sender instanceof Player) {
				Player p = (Player)sender;
				if(args.length == 0) {
					showHelp(p);
				}
				else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("help")) {
						showHelp(p);
					} else if(args[0].equalsIgnoreCase("reload")) {
						if(p.hasPermission("events.reload")) {
							reload();
							sendMsg(p, messages.getString("reloaded-message"));
						} else {
							sendMsg(p, messages.getString("no-permission-message"));
						}
					} else if(args[0].equalsIgnoreCase("host")) {
						if(p.hasPermission("events.host")) {
							openHostingMenu(p);
						} else {
							sendMsg(p, messages.getString("no-permission-message"));
						}
					} else if(args[0].equalsIgnoreCase("join")) {
						if(event == null) {
							sendMsg(p, messages.getString("no-event-message"));
						} else {
							if(event.isFull()) {
								sendMsg(p,messages.getString("event-full-message"));
							} else {
								sendMsg(p, messages.getString("joined-event-message"));
								event.addPlayer(p);
							}
						}
					} else if(args[0].equalsIgnoreCase("leave")) {
						if(event == null || !event.getPlayers().contains(p.getUniqueId()) || !event.getSpectators().contains(p.getUniqueId())) {
							sendMsg(p, messages.getString("player-not-in-event-message"));
						} else {
							sendMsg(p, messages.getString("left-event-message"));
							event.removePlayer(p);
						}
					} else if(args[0].equalsIgnoreCase("spectate")) {
						if(event == null) {
							sendMsg(p, messages.getString("no-event-message"));
						} else {
							event.addSpectator(p);
							sendMsg(p, messages.getString("spectating-event-message"));
						}
					}
				}
			} else {
				getLogger().info("Only players can use this command");
			}
		}
		return false;
	}
	
	private void openHostingMenu(Player p) {
		int size = hostMenu.getInt("menu-size");
		String name = hostMenu.getString("menu-name");
		Inventory inv = Bukkit.createInventory(p, size, name);
		ConfigurationSection itemConfig = hostMenu.getConfigurationSection("menu-items");
		for(String key : itemConfig.getKeys(false)) {	
			inv.setItem(itemConfig.getInt(key+".slot"),itemConfig.getItemStack(key+".item"));
		}		
		p.openInventory(inv);
	}

	private void showHelp(Player p) {
		for(String s : messages.getStringList("help-message")) {
			sendMsg(p, s);
		}
	}
	
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public YamlConfiguration getMenuConfig() {
		return hostMenu;
	}
	
	public YamlConfiguration getMessageonfig() {
		return messages;
	}
	
	public void reload() {
		messages = YamlConfiguration.loadConfiguration(messageFile);
		hostMenu = YamlConfiguration.loadConfiguration(hostMenuFile);
	}
	
	public void sendMsg(Player p, String s) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%event%", event.getName()));
	}
	
	
}
