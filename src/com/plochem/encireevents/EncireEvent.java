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
	public static EncireEvent plugin;
	private Event event = null;
	private File messageFile = new File(this.getDataFolder(), "messages.yml");
	private YamlConfiguration messages;
	private File hostMenuFile = new File(this.getDataFolder(), "host-menu.yml");
	private YamlConfiguration hostMenu;
	private File eventFile = new File(this.getDataFolder(), "event-config.yml");
	private YamlConfiguration eventConfig;
	
	public void onEnable() {
		plugin = this;
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
		pm.addPermission(new Permission("events.spectatorspawn"));
		pm.addPermission(new Permission("events.gamespawn"));
		
		pm.registerEvents(new MenuListener(this), this);
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
		if(!eventFile.exists()) {
			eventFile.getParentFile().mkdirs();
			saveResource("event-config.yml", false);
		}
		eventConfig = YamlConfiguration.loadConfiguration(eventFile);
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
				} else {
					if(args[0].equalsIgnoreCase("help")) {
						showHelp(p);
					} else if(args[0].equalsIgnoreCase("reload")) {
						if(p.hasPermission("events.reload")) {
							reload();
							p.sendMessage(msgFormat(messages.getString("reloaded-message")));
						} else {
							p.sendMessage(msgFormat(messages.getString("no-permission-message")));
						}
					} else if(args[0].equalsIgnoreCase("host")) {
						if(p.hasPermission("events.host")) {
							openHostingMenu(p);
						} else {
							p.sendMessage(msgFormat(messages.getString("no-permission-message")));
						}
					} else if(args[0].equalsIgnoreCase("join")) {
						if(event == null) {
							p.sendMessage(msgFormat(messages.getString("no-event-message")));
						} else {
							if(event.isFull()) {
								p.sendMessage(msgFormat(messages.getString("event-full-message")));
							} else {
								p.sendMessage(msgFormat(messages.getString("joined-event-message")));
								event.addPlayer(p);
							}
						}
					} else if(args[0].equalsIgnoreCase("leave")) {
						if(event == null || !event.getPlayers().contains(p.getUniqueId()) || !event.getSpectators().contains(p.getUniqueId())) {
							p.sendMessage(msgFormat(messages.getString("player-not-in-event-message")));
						} else {
							p.sendMessage(msgFormat(messages.getString("left-event-message")));
							event.removePlayer(p);
						}
					} else if(args[0].equalsIgnoreCase("spectate")) {
						if(event == null) {
							p.sendMessage(msgFormat(messages.getString("no-event-message")));
						} else {
							event.addSpectator(p);
							p.sendMessage(msgFormat(messages.getString("spectating-event-message")));
						}
					} else if(args[0].equalsIgnoreCase("spectatorspawn")) {
						if(p.hasPermission("events.spectatorspawn")) {
							if(args.length == 2) {
								if(eventConfig.getConfigurationSection("spectator-spawn").contains(args[1].toLowerCase())) {
									eventConfig.getConfigurationSection("spectator-spawn").set(args[1].toLowerCase(), p.getLocation());
									save(eventFile, eventConfig);
									p.sendMessage(messages.getString("set-new-spectator-spawn")); 
								} else {
									p.sendMessage(messages.getString("no-such-event"));
									p.sendMessage(msgFormat("&ePossible choices: ffa, temperature, islandclash, sumo, or waterdrop"));
								}
							}
						} else {
							p.sendMessage(msgFormat(messages.getString("no-permission-message")));	
						}
					} else if(args[0].equalsIgnoreCase("gamespawn")) {
						if(args[1].equalsIgnoreCase("ffa")) {
							
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
			p.sendMessage(msgFormat(s));
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
	
	public YamlConfiguration getMessageConfig() {
		return messages;
	}	
	
	public YamlConfiguration getEventConfig() {
		return eventConfig;
	}
	
	
	public void reload() {
		messages = YamlConfiguration.loadConfiguration(messageFile);
		hostMenu = YamlConfiguration.loadConfiguration(hostMenuFile);
		eventConfig = YamlConfiguration.loadConfiguration(eventFile);
	}
	
	public String msgFormat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s).replaceAll("%event%", event.getName());
	}
	
	
}
