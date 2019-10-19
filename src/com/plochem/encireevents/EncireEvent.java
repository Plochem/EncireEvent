package com.plochem.encireevents;

import java.io.File;

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
		getLogger().info("Plugin developed by Plochem");
		getLogger().info("https://github.com/Plochem");
		registerThings();
	}
	
	public void registerThings() {
		
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
					}
				}
			} else {
				getLogger().info("[Events] Only players can use this command");
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
}
