package com.plochem.encireevents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.plochem.encireevents.listeners.PlayerMovementEvent;

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
		pm.registerEvents(new PlayerMovementEvent(), this);
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

	@SuppressWarnings("unchecked")
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
							if(event.hasStarted()) {
								p.sendMessage(msgFormat(messages.getString("event-already-started-message")));
							} else if(event.isFull()) {
								p.sendMessage(msgFormat(messages.getString("event-full-message")));
							} else if(p.getInventory().getContents().length != 0){
								p.sendMessage(msgFormat(messages.getString("inventory-not-empty-message")));						
							}  else {
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
						if(p.hasPermission("events.gamespawn")) {
							if(args.length >= 2) {
								if(args[1].equalsIgnoreCase("ffa") || args[1].equalsIgnoreCase("waterdrop") || args[1].equalsIgnoreCase("temperature")) {
									eventConfig.set("ffa-startLoc", p.getLocation());
									save(eventFile, eventConfig);
									p.sendMessage(msgFormat("&cYou set the location of the game spawn for the " + args[1] + " event to your current position."));	
								} else if(args[1].equalsIgnoreCase("islandclash") || args[1].equalsIgnoreCase("sumo")) { //TODO
									if(args.length == 3) { // /events gamespawn islandclash 1
										if(StringUtils.isNumeric(args[2]) && Integer.parseInt(args[2]) > 0) { // loc idx
											int idx = Integer.parseInt(args[2]);
											List<Location> locs = (List<Location>)eventConfig.getList(args[1] + "-startLoc");
											if(locs == null) locs = new ArrayList<>();
											if(locs.size() >= idx) {
												locs.set(idx-1, p.getLocation());
												p.sendMessage(msgFormat("&cYou set the location of game spawn #" + idx + " for the " + args[1] + " event to your current position."));		
											} else {
												locs.add(p.getLocation());	
												p.sendMessage(msgFormat("&cYou're trying to set game spawn #" + idx + ", but the previous ones have not been created. So you just set the location of game spawn #" + locs.size() + " for the Island Clash event to your current position."));											 
											}
											eventConfig.set(args[1] + "-startLoc", locs);
											save(eventFile, eventConfig);
										} else {
											p.sendMessage(msgFormat("&cUse a positive integer to specify the game spawn."));
										}
									} else {
										p.sendMessage(msgFormat("&cYou need to specify an which game spawn you want to set."));
									}
								} else {
									p.sendMessage(msgFormat("&ePossible choices: ffa, temperature, islandclash, sumo, or waterdrop"));	
								}
								
							} else {
								p.sendMessage(msgFormat("&cYou need to specify which event you want to modify the game spawns."));
							}
						} else {
							p.sendMessage(msgFormat(messages.getString("no-permission-message")));	
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
