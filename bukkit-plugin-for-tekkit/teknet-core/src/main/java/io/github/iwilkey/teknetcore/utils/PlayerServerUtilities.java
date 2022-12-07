package io.github.iwilkey.teknetcore.utils;

import java.util.AbstractMap;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.cooldown.Cooldown;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;

public class PlayerServerUtilities {
	public static class Lag extends TeknetCoreCommand  {
		private static final int TARGET_TPS = 20;
		static ArrayList<AbstractMap.SimpleEntry<ChatColor, String>> lev;
		public Lag(Ranks.Rank permissions) {
			super("lag", permissions);
			lev = new ArrayList<AbstractMap.SimpleEntry<ChatColor, String>>();
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.GREEN, "perfect"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.DARK_GREEN, "very good"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.AQUA, "good"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.DARK_AQUA, "average"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.YELLOW, "slightly below average"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.GOLD, "poor"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.RED, "terrible"));
			lev.add(new AbstractMap.SimpleEntry<ChatColor, String>(ChatColor.DARK_RED, "unplayable"));
		}
		
		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			SoundUtilities.playSoundTo("NOTE_PIANO", sender);
			ChatUtilities.logTo(sender, ChatColor.WHITE + "Teknet speeds are currently rated  \"" + returnStatus() + "\" clocking in at " + ChatColor.GOLD
					+ "" + TeknetCore.SERVER_TPS + ChatColor.WHITE + " tick(s) per second. That is about " 
					+ ChatColor.GOLD + ((TeknetCore.SERVER_TPS / 20.0f) * 100.0f) 
					+ "%" + ChatColor.WHITE + " efficiency.", ChatUtilities.LogType.UTILITY);
			return true;
		}
		private static String returnStatus() {
			int index = Math.min(lev.size() - 1, Math.max(0, lev.size() - (int)((TeknetCore.SERVER_TPS / TARGET_TPS) * lev.size())));
			return ChatColor.BOLD + "" + (lev.get(index)).getKey() + (lev.get(index)).getValue() + ChatColor.RESET + ChatColor.WHITE;
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			doc.editPage(0).write(ChatColor.GOLD + "This utility is used to help one see the status" + ChatColor.RESET, 1);
			doc.editPage(0).write(ChatColor.GOLD + "    of the servers' processing efficiency." + ChatColor.RESET, 2);
			doc.editPage(0).write("Use [lag-help] to show this help page again.", 3);
			doc.editPage(0).write("Use [lag] to see the servers current TPS.", 4);
			doc.editPage(0).write("", 5);
			doc.editPage(0).write("Note: If you are lagging but the server TPS is fine, it", 6);
			doc.editPage(0).write("    is often the fault of your own internet connection.", 7);
			doc.editPage(0).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
		}
	}
	
	public static class CooldownCommand extends TeknetCoreCommand {
		public CooldownCommand(Rank permissions) {
			super("cooldown", permissions);
		}
		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			if(Cooldown.can(sender)) {
				ChatUtilities.logTo(sender, "You have not recently been given a cooldown warning. You are allowed to chat and execute commands.", 
						ChatUtilities.LogType.NOTICE);
				return true;
			}
			ChatUtilities.logTo(sender, "You have to wait " + ChatColor.GREEN + Cooldown.timeTillReset(sender) + ChatColor.GRAY +
					" (s) before you can execute a command or chat!", ChatUtilities.LogType.NOTICE);
			return true;
		}
		@Override
		protected void documentation(CommandDocumentation doc) {
			doc.editPage(0).write(ChatColor.GOLD + "This utility is used to inform a player as to" + ChatColor.RESET, 1);
			doc.editPage(0).write(ChatColor.GOLD + "  when they can use commands/chat again." + ChatColor.RESET, 2);
			doc.editPage(0).write("Use [cooldown-help] to show this help page again.", 3);
			doc.editPage(0).write("Use [cooldown] to see how long till your cooldown", 4);
			doc.editPage(0).write("     has passed.", 5);
			doc.editPage(0).write("Note: A player punished with a cooldown has been creating", 6);
			doc.editPage(0).write("  unnecessary lag and/or grief for other players.", 7);
			doc.editPage(0).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
		}
		
	}
	
}
