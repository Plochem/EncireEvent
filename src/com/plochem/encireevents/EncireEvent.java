package com.plochem.encireevents;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EncireEvent extends JavaPlugin{	
	private Event event = null;
	private File messageFile = new File(this.getDataFolder(), "messages.yml");
	private YamlConfiguration messages = YamlConfiguration.loadConfiguration(messageFile);
	
	public void onEnable() {
		getLogger().info("____________________________");
		getLogger().info("Plugin developed by Plochem");
		getLogger().info("https://github.com/Plochem");
		getLogger().info("____________________________");
		registerThings();
		save(messageFile, messages);
	}
	
	public void registerThings() {
		
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
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("help")) {
						showHelp(p);
					} else if(args[0].equalsIgnoreCase("join")) {
						if(event == null) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',messages.getString("no-event-message")));
						} else {
							if(event.isFull()) {

								p.sendMessage(ChatColor.translateAlternateColorCodes('&',messages.getString("event-full-message")));
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',messages.getString("joined-event-message")));
								event.addPlayer(p);
							}
						}
					} else if(args[0].equalsIgnoreCase("leave")) {
						if(event == null || !event.getPlayers().contains(p.getUniqueId()) || !event.getSpectators().contains(p.getUniqueId())) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("player-not-in-event-message")));
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("left-event-message")));
							event.removePlayer(p);
						}
					} else if(args[0].equalsIgnoreCase("spectate")) {
						
					}
				}
			} else {
				getLogger().info("Only players can use this command");
			}
		}
		return false;
	}
	
	private void showHelp(Player p) {
		for(String s : messages.getStringList("help-message")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public Event getEvent() {
		return event;
	}
	
	public void reload() {
		
	}
}
