package io.github.iwilkey.teknetcore.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtilities {
	
	public static class CommandDocumentation {
		public static class Page {
			public String[] lines;
			public Page() {
				lines = new String[9];
				for(int i = 0; i < 9; i++) lines[i] = " ";
			}
			public void write(String content, int line) {
				if(line >= 9) return;
				// if(content.length() >= 75) content = content.substring(0, 75);
				lines[line] = highlightCommands(content, ChatColor.WHITE);
			}
			public void renderTo(Player player) {
				SoundUtilities.playSoundTo("CLICK", player);
				for(int i = 0; i < 9; i++) 
					messageTo(player, lines[i], ChatColor.WHITE);
			}
			public boolean searchFor(String word) {
				for(String line : lines) {
					String[] words = line.split(" ");
					for(String w : words) {
						if(w.replaceAll("[^A-Za-z]+", "").contains(word))
							return true;
					}
				}
				return false;
			}
		}
		// 10 lines to show.
		// <=60 characters per line
		// A document can have up to several pages.
		public String name;
		public ArrayList<Page> pages;
		public CommandDocumentation(String name) {
			this.name = name;
			pages = new ArrayList<>();
			Page page = new Page();
			page.write(ChatColor.GRAY + "Use [" + name.toLowerCase() + "-help-(n-OR-'all')] " + ChatColor.GRAY + " for page n (or all)." + ChatColor.RESET, 0);
			pages.add(page);
		}
		public void addPage(Page page) {
			pages.add(page);
		}
		public Page editPage(int page) {
			if(page >= pages.size()) return null;
			return pages.get(page);
		}
		public void renderPageTo(Player player, int page) {
			if(page >= pages.size() || page < 0)  {
				logTo(player, "This manual does not contain the page you have entered!", LogType.FATAL);
				return;
			}
			messageTo(player, "------- " + name + ": Index (" + (page + 1) + "/" + pages.size() + ") -------", ChatColor.WHITE);
			pages.get(page).renderTo(player);
		}
		public ArrayList<Integer> searchFor(String word) {
			ArrayList<Integer> found = new ArrayList<>();
			int pp = 0;
			for(Page p : pages) {
				if(p.searchFor(word))
					found.add(pp);
				pp++;
			}
			return found;
		}
	}
	
	public enum LogType {
		SUCCESS, NOTICE, FATAL, UTILITY, ADMIN_UTILITY
	}
	
	static final ChatColor r = ChatColor.RESET,
		b = ChatColor.BOLD;
	
	public static void tagAndMessageTo(Player player, String tag, String message, 
			ChatColor body, ChatColor outline, ChatColor messageColor) {
		String m = outline + "" + b + "[" + r + body + b + tag + r + outline + b + "] " + r + messageColor 
				+ highlightCommands(message, messageColor) + r;
		player.sendMessage(m);
	}
	
	public static void tagAndMessageOnline(String tag, String message, ChatColor body, 
			ChatColor outline, ChatColor messageColor) {
		String m = outline + "" + b + "[" + r + body + b + tag + r + outline + b + "] " + r + messageColor 
				+ highlightCommands(message, messageColor) + r;
		for(Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(m);
	}
	
	public static void messageTo(Player player, String message, ChatColor textColor) {
		String m = textColor + highlightCommands(message, textColor) + r;
		player.sendMessage(m);
	}
	
	public static void messageOnline(String message, ChatColor textColor) {
		String m = textColor + highlightCommands(message, textColor) + r;
		for(Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(m);
	}
	
	public static void logTo(Player player, String message, LogType type) {
		String m = highlightCommands(message, ChatColor.GRAY);
		switch(type) {
			case SUCCESS: ChatUtilities.tagAndMessageTo(player, "TeknetCore Success", m, ChatColor.GREEN, 
					ChatColor.DARK_GREEN, ChatColor.GRAY); 
					SoundUtilities.playSoundTo("NOTE_PLING", player); 
					break;
			case NOTICE: ChatUtilities.tagAndMessageTo(player, "TeknetCore Notice", m, ChatColor.YELLOW, 
					ChatColor.GOLD, ChatColor.GRAY); 
					SoundUtilities.playSoundTo("NOTE_SNARE_DRUM", player); 
					break;
			case FATAL: ChatUtilities.tagAndMessageTo(player, "TeknetCore Error", m, ChatColor.RED, 
					ChatColor.DARK_RED, ChatColor.GRAY);
					SoundUtilities.playSoundTo("NOTE_BASS", player); 
					break;
			case UTILITY: ChatUtilities.tagAndMessageTo(player, "TeknetCore Utilities", m, ChatColor.BLUE, 
					ChatColor.DARK_BLUE, ChatColor.GRAY); 
					SoundUtilities.playSoundTo("NOTE_STICKS", player); 		
					break;
			case ADMIN_UTILITY: ChatUtilities.tagAndMessageTo(player, "TeknetCore Admin Utilities", m, ChatColor.LIGHT_PURPLE, 
					ChatColor.DARK_PURPLE, ChatColor.GRAY); 
					SoundUtilities.playSoundTo("PISTON_EXTEND", player);
					break;
		}
	}
	
	public static String highlightCommands(String message, ChatColor reset) {
		String ret = "";
		String[] tok = message.split(" ");
		for(String t : tok) {
			if(t.length() == 0) {
				ret += " ";
				continue;
			}
			if(t.charAt(0) == '[' && t.charAt(t.length() - 1) == ']') {
				t = ChatColor.AQUA + "/" + t.substring(1, t.length() - 1) + reset;
				t = t.replace('-', ' ');
				t = t.replace('_', ' ');
			}
			ret += t + " ";
		}
		return ret;
	}
}
