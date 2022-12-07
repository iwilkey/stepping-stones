package io.github.iwilkey.teknetdevelopment;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetdevelopment.resources.PluginUtil;

public final class TeknetDevelopment extends JavaPlugin {

	private static String PLUGIN_DIR_PATH = "/home/opc/legends/plugins";
	private int version = 0;
	private Sound sx;
	@Override
	public void onEnable() {
		loadResources();
		initSyncEvents();
		getLogger().info("Ready for TeknetCore development!");
	}

	@Override
	public void onDisable() {
		getLogger().info("TeknetDevelopment utilities terminated...");
	}
	
	private void loadResources() {
		for(Sound f : Sound.values()) 
			if(f.name().contains("ORB")) 
				sx = f;
	}

	private void initSyncEvents() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			File pluginDir = new File(PLUGIN_DIR_PATH);
			Plugin target = Bukkit.getPluginManager().getPlugin("TeknetCore");
			public void run() {
				for (File f : pluginDir.listFiles()) {
					if (f.getName().equals("TeknetCore-v1.100." + (version + 1) + ".jar")) {
						target = Bukkit.getPluginManager().getPlugin("TeknetCore");
						PluginUtil.unload(target);
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						try {
							target = null;
							target = Bukkit.getPluginManager().loadPlugin(f);
						} catch (UnknownDependencyException e) {
							e.printStackTrace();
						} catch (InvalidPluginException e) {
							e.printStackTrace();
						} catch (InvalidDescriptionException e) {
							e.printStackTrace();
						}
						target.onLoad();
						Bukkit.getPluginManager().enablePlugin(target);
						File file = new File(PLUGIN_DIR_PATH + "/TeknetCore-v1.100." + version + ".jar");
						file.delete();
						version++;
						for(Player p : Bukkit.getOnlinePlayers())
							p.playSound(p.getLocation(), sx, 100.0f, 0.0f);
						Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD +
								"[" + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "TeknetDevelopment" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "]" + ChatColor.RESET + "" + ChatColor.GRAY 
								+ "" + ChatColor.ITALIC + " A new TeknetCore update is now live!" + ChatColor.RESET);
						break;
					}
				}
			}
		}, 0l, 1l);
	}
}
