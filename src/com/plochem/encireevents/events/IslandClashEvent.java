package com.plochem.encireevents.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.plochem.encireevents.EncireEvent;
import com.plochem.encireevents.Event;

public class IslandClashEvent extends Event{
	
	private List<Location> startLoc;
	private World mapWorld;

	public IslandClashEvent(String name, int maxPlayers, Location specLoc, List<Location> startLoc) {
		super(name, maxPlayers, specLoc);
		this.startLoc = startLoc;
		this.mapWorld = specLoc.getWorld();
	}

	@Override
	public void start() {
		mapWorld.setAutoSave(false);
		for(int i = 0; i < this.getPlayers().size(); i++) {
			Player p = Bukkit.getPlayer(this.getPlayers().get(i));
			p.teleport(startLoc.get(i));
		}
	}
	
	@Override
	public boolean lastStanding() {
		if(this.getPlayers().size() == 1) {
			return true;
		}
		return false;
	}
	
	
    private void unloadMap(World mapWorld){
        if(Bukkit.getServer().unloadWorld(mapWorld, false)){
            EncireEvent.plugin.getLogger().info("Successfully unloaded " + mapWorld.getName());
        }else{
        	EncireEvent.plugin.getLogger().severe("COULD NOT UNLOAD " + mapWorld.getName());
        }
    }
    
    private void loadMap(String mapname){
        Bukkit.getServer().createWorld(new WorldCreator(mapname));
    }
 
    public void rollback(){
        unloadMap(mapWorld);
        loadMap(mapWorld.getName());
    }

}
