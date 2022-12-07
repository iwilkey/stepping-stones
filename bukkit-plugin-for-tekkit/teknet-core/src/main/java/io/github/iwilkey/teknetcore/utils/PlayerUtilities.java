package io.github.iwilkey.teknetcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtilities {
	
	public static Player get(String name) {
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.getName().equals(name))
				return p;
		return null;
	}
	
}
