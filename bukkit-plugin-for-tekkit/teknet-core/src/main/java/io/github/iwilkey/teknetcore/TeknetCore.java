package io.github.iwilkey.teknetcore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.economy.Bank;
import io.github.iwilkey.teknetcore.economy.Shop;
import io.github.iwilkey.teknetcore.estate.Estate;
import io.github.iwilkey.teknetcore.eventlistener.ServerEventListener;
import io.github.iwilkey.teknetcore.location.Locations;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.PlayerServerUtilities;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation.Page;

public final class TeknetCore extends JavaPlugin {
	
	public static float SERVER_TPS = 20;
	
	static protected CommandDocumentation doc;

	@Override
	public void onEnable() {
        loadResources();
        registerCommands();
        clock();
		getLogger().info("\n _____     _               _     ___               \n"
					   + "/__   \\___| | ___ __   ___| |_  / __\\___  _ __ ___ \n"
				       + "  / /\\/ _ \\ |/ / '_ \\ / _ \\ __|/ /  / _ \\| '__/ _ \\\n"
				       + " / / |  __/   <| | | |  __/ |_/ /__| (_) | | |  __/\n"
				       + " \\/   \\___|_|\\_\\_| |_|\\___|\\__\\____/\\___/|_|  \\___|\n"
				       + "                                                   ");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("TeknetCore terminated.");
	}
	
	private void loadResources() {
		initDocumentation();
		ServerEventListener sel = new ServerEventListener();
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(sel, this);
        new Cooldown();
        new SoundUtilities();
        new Ranks();
        new Locations();
        new Bank();
        new Shop();
        new Estate();
        new SequenceUtilities();
	}
	
	private void registerCommands() {
		// PlayerServerUtilities
		getCommand("lag").setExecutor(new PlayerServerUtilities.Lag(Rank.HOBBYIST));
		getCommand("cooldown").setExecutor(new PlayerServerUtilities.CooldownCommand(Rank.HOBBYIST));
		
		// Location
		getCommand("home").setExecutor(new Locations.Home.HomeCommand(Rank.HOBBYIST));
		getCommand("sethome").setExecutor(new Locations.Home.SetHome(Rank.HOBBYIST));
		getCommand("position").setExecutor(new Locations.Positions.PositionCommand(Rank.HOBBYIST));
		
		// Economy
		getCommand("bank").setExecutor(new Bank.BankCommand(Rank.HOBBYIST));
		getCommand("shop").setExecutor(new Shop.ShopCommand(Rank.HOBBYIST));
		
		// Estate
		getCommand("estate").setExecutor(new Estate.EstateCommand(Rank.HOBBYIST));
		
		// Admin utilities
		getCommand("ranks").setExecutor(new Ranks.AdminRankUtilities(Rank.ADMIN));
	}
	
	private void clock() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, 
			new Runnable() {
				long now, last, delta = 0;
	            int ticks;
				public void run() {
					last = now;
					now = System.nanoTime();
					delta += now - last;
					ticks++;
					if(delta >= 1000000000) {
						SERVER_TPS = ticks;
						delta = 0;
						ticks = 0;
					}
					Cooldown.tick();
					Locations.tick();
					Estate.tick();
					SequenceUtilities.tick();
				}
			}, 0l, 1l);
	}
	
	private static void initDocumentation() {
		int page = 0;
		doc = new CommandDocumentation("TeknetCore");
		doc.editPage(0).write(ChatColor.GRAY + "Use [help-n] " + ChatColor.GRAY + "to see page 'n' of TeknetCore help." + ChatColor.RESET, 0);
		doc.editPage(0).write(ChatColor.GOLD + "TeknetCore © 2022 Ian Wilkey (iwilkey)" + ChatColor.RESET, 1);
		doc.editPage(0).write(ChatColor.GRAY + "TeknetCore is a Java Plugin designed by American" + ChatColor.RESET, 2);
		doc.editPage(0).write(ChatColor.GRAY + "software engineer Ian Wilkey. It is a suite of tools" + ChatColor.RESET, 3);
		doc.editPage(0).write(ChatColor.GRAY + "intended to enhance the experiance of the Teknet Server" + ChatColor.RESET, 4);
		doc.editPage(0).write(ChatColor.GRAY + "Network members and staff." + ChatColor.RESET, 5);
		doc.editPage(0).write(ChatColor.DARK_AQUA + "Please use this manual to get started with its usage!" + ChatColor.RESET, 6);
		doc.editPage(0).write(ChatColor.RED + "If you encounter a bug, please alert an admin right away." + ChatColor.RESET, 7);
		doc.editPage(0).write(ChatColor.GRAY + "------- Commands on following pages... -------" + ChatColor.RESET, 8);
		doc.addPage(new Page());
		page = 1;
		doc.editPage(page).write(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "  Server Utilities", 0);
		doc.editPage(page).write("► [lag] (no arguments) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 1);
		doc.editPage(page).write(ChatColor.GOLD + "  See the current TPS of the server. [lag-help]", 2);
		doc.editPage(page).write("► [cooldown] (no arguments) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 3);
		doc.editPage(page).write(ChatColor.GOLD + "  Use when punished with a cooldown. [cooldown-help]", 4);
		doc.editPage(page).write("", 5);
		doc.editPage(page).write("", 6);
		doc.editPage(page).write("", 7);
		doc.editPage(page).write(ChatColor.GRAY + "------- Commands on following pages... -------" + ChatColor.RESET, 8);
		doc.addPage(new Page());
		page = 2;
		doc.editPage(page).write(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "  Location Utilities", 0);
		doc.editPage(page).write("► [home] (no arguments) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 1);
		doc.editPage(page).write(ChatColor.GOLD + "  Teleport back to your set home. [home-help]", 2);
		doc.editPage(page).write("► [sethome] (no arguments) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 3);
		doc.editPage(page).write(ChatColor.GOLD + "  Set your location as your home. [sethome-help]", 4);
		doc.editPage(page).write("► [position] (command) (argument(s)) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 5);
		doc.editPage(page).write(ChatColor.GOLD + "  Teleport to personally saved (or random) locations.", 6);
		doc.editPage(page).write(ChatColor.GOLD + "    Start with [position-help]", 7);
		doc.editPage(page).write(ChatColor.GRAY + "------- Commands on following pages... -------" + ChatColor.RESET, 8);
		doc.addPage(new Page());
		page = 3;
		doc.editPage(page).write(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "  Economic Utilities", 0);
		doc.editPage(page).write("► [bank] (command) (argument(s)) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 1);
		doc.editPage(page).write(ChatColor.GOLD + "  Manage your TeknetTrust bank account.", 2);
		doc.editPage(page).write(ChatColor.GOLD + "    Start with [bank-help]", 3);
		doc.editPage(page).write("► [shop] (command) (argument(s)) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 4);
		doc.editPage(page).write(ChatColor.GOLD + "  Buy and sell all items available in Tekkit Legends.", 5);
		doc.editPage(page).write(ChatColor.GOLD + "    Start with [shop-help]", 6);
		doc.editPage(page).write("", 7);
		doc.editPage(page).write(ChatColor.GRAY + "------- Commands on following pages... -------" + ChatColor.RESET, 8);
		doc.addPage(new Page());
		page = 4;
		doc.editPage(page).write(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "  Estate Utilities", 0);
		doc.editPage(page).write("► [estate] (command) (argument(s)) " + Ranks.introduce(Ranks.getRankFromLevel(1)) + ChatColor.RESET, 1);
		doc.editPage(page).write(ChatColor.GOLD + "  Manage your server estates! (Land protection).", 2);
		doc.editPage(page).write(ChatColor.GOLD + "    Start with [estate-help]", 3);
		doc.editPage(page).write("", 4);
		doc.editPage(page).write("", 5);
		doc.editPage(page).write("", 6);
		doc.editPage(page).write("", 7);
		doc.editPage(page).write(ChatColor.GRAY + "------- Commands on following pages... -------" + ChatColor.RESET, 8);
		
		// doc.editPage(1).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
	}
	
	public static void printHelp(Player player, int page) {
		doc.renderPageTo(player, page - 1);
	}
	
	public static void printAllHelp(Player player) {
		for(int i = doc.pages.size() - 1; i >= 0; i--)
			doc.renderPageTo(player, i);
	}
}
