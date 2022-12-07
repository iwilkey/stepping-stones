package io.github.iwilkey.teknetcore.eventlistener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.economy.Shop;
import io.github.iwilkey.teknetcore.economy.Shop.ShopBuySession;
import io.github.iwilkey.teknetcore.estate.Estate;
import io.github.iwilkey.teknetcore.estate.Estate.EstateInstance;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.MathUtilities;
import io.github.iwilkey.teknetcore.utils.PlayerUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class ServerEventListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public static void onPlayerChat(AsyncPlayerChatEvent e) { 
		Cooldown.registerActivity(e.getPlayer(), "CHAT_EVENT");
		if(!Cooldown.can(e.getPlayer())) {
			ChatUtilities.logTo(e.getPlayer(), "You need to wait " + ChatColor.GREEN + Cooldown.timeTillReset(e.getPlayer()) + 
					ChatColor.GRAY + " (s) before you can send more chats! Use "
							+ "[cooldown] to see how much time you have left to wait.", ChatUtilities.LogType.FATAL);
			e.setCancelled(true);
			return;
		}
		String out = Ranks.tag(e.getPlayer()) + ChatColor.RESET + e.getPlayer().getName() + ": " + ChatUtilities.highlightCommands(e.getMessage(), ChatColor.WHITE);
		Bukkit.broadcastMessage(out);
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public static void onPlayerCommandRequest(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().substring(0, 5).equals("/stop")) return;
		if(e.getMessage().equals("/help") || e.getMessage().equals("/teknetcore help")) {
			TeknetCore.printAllHelp(e.getPlayer());
			e.setCancelled(true);
			return;
		}
		if(e.getMessage().equals("/cooldown")) return;
		Cooldown.registerActivity(e.getPlayer(), e.getMessage());
		if(!Cooldown.can(e.getPlayer())) {
			ChatUtilities.logTo(e.getPlayer(), "You need to wait " + ChatColor.GREEN + Cooldown.timeTillReset(e.getPlayer()) + 
					ChatColor.GRAY + " (s) before you can execute more commands! Use [cooldown] to see how much time you have left to wait.", ChatUtilities.LogType.FATAL);
			e.setCancelled(true);
			return;
		}
	}
	
	// Shop related events.
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerMove(PlayerMoveEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) {
			if(e.getPlayer().getLocation().getX() != s.startedAt.getX() ||
					e.getPlayer().getLocation().getY() != s.startedAt.getY() ||
					e.getPlayer().getLocation().getZ() != s.startedAt.getZ()) {
				e.getPlayer().teleport(s.startedAt);
				SoundUtilities.playSoundTo("NOTE_BASS", e.getPlayer());
				ChatUtilities.messageTo(e.getPlayer(), 
						"Done shopping? [shop-checkout]", ChatColor.GRAY);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerItemDrop(PlayerDropItemEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) {
			SoundUtilities.playSoundTo("NOTE_BASS", e.getPlayer());
			ChatUtilities.messageTo(e.getPlayer(), "Done shopping? [shop-checkout]", ChatColor.GRAY);
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerInteraction(PlayerInteractEvent e) {
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) {
			SoundUtilities.playSoundTo("NOTE_BASS", e.getPlayer());
			ChatUtilities.messageTo(e.getPlayer(), "Done shopping? [shop-checkout]", 
					ChatColor.GRAY);
			e.setCancelled(true);
		}
		Location interactionLocation = e.getClickedBlock().getLocation();
		for(EstateInstance ss : Estate.ESTATE_STATE) {
			if(MathUtilities.locationInEstateRegion(ss.centerLocation.getBlockX(), ss.centerLocation.getBlockZ(), interactionLocation.getBlockX(), interactionLocation.getBlockZ(), ss.size)) {
				if(!ss.members.contains(e.getPlayer().getName())) {
					SoundUtilities.playSoundTo("NOTE_BASS", e.getPlayer());
					ChatUtilities.messageTo(e.getPlayer(), "You do not have permission to interact "
							+ "with anything inside this estate!", 
							ChatColor.RED);
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerLogin(PlayerLoginEvent e) {
		ChatUtilities.messageOnline("Welcome to Teknet, " + e.getPlayer().getName(), ChatColor.DARK_PURPLE);
		SoundUtilities.playSoundToOnline("FIREWORK_BLAST");
		Ranks.setRank(e.getPlayer(), Rank.HOBBYIST, true);
		e.getPlayer().setPlayerListName(Rank.HOBBYIST.color + e.getPlayer().getName());
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerJoin(PlayerJoinEvent e) {
		Rank rank = Ranks.getPlayerRank(e.getPlayer());
		e.getPlayer().setPlayerListName(rank.color + e.getPlayer().getName());
		SoundUtilities.playSoundToOnline("DOOR_OPEN");
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerKick(PlayerKickEvent e) {
		Ranks.setRank(e.getPlayer(), Rank.HOBBYIST, false);
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerLeave(PlayerQuitEvent e) {
		SoundUtilities.playSoundToOnline("DOOR_CLOSE");
		ShopBuySession s = Shop.getShopSessionOf(e.getPlayer());
		if(s != null) Shop.stopShopSession(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPluginDisable(PluginDisableEvent e) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			ShopBuySession s = Shop.getShopSessionOf(p);
			if(s != null) {
				Shop.stopShopSession(p);
				SoundUtilities.playSoundTo("GLASS", PlayerUtilities.get(s.playerName));
				ChatUtilities.logTo(p, ChatColor.GOLD + "To prevent from an undesirable or "
						+ "unintended occurance during routine TeknetCore maintenance, your "
						+ "shop session has been safely ended. Please wait about 10 (s) and try again.", 
						ChatUtilities.LogType.NOTICE);
			}
		}
	}
}
