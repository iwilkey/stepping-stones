package io.github.iwilkey.teknetcore.ranks;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCoreCommand.AdminTeknetCoreCommand;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.PlayerUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class Ranks {
	
	public static class AdminRankUtilities extends AdminTeknetCoreCommand {
		public AdminRankUtilities(Rank permissions) {
			super("ranks", permissions);
			
			Function setFunction = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					Player p = PlayerUtilities.get(args[1]);
					if(p == null) return;
					try {
						int index = Integer.parseInt(args[2]);
						Rank rank = getRankFromLevel(index);
						if(rank == Ranks.Rank.OWNER && !sender.getName().equals("iwilkey")) {
							SoundUtilities.playSoundTo("VILLAGER_NO", sender);
							ChatUtilities.logTo(sender, "You are not allowed to set yourself as owner.", ChatUtilities.LogType.FATAL);
							return;
						}
						setRank(p, getRankFromLevel(index), true);
					} catch (Exception e) {
						Rank rank = getRankFromName(args[2]);
						if(rank == Ranks.Rank.OWNER && !sender.getName().equals("iwilkey")) {
							SoundUtilities.playSoundTo("VILLAGER_NO", sender);
							ChatUtilities.logTo(sender, "You are not allowed to set yourself as owner.", ChatUtilities.LogType.FATAL);
							return ;
						}
						setRank(p, getRankFromName(args[2]), true);
					}
					ChatUtilities.logTo(sender, "Player rank successfully set.", ChatUtilities.LogType.SUCCESS);
				}
			};
			
			Function listFunction = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ChatUtilities.logTo(sender, "TeknetCore current available ranks...", ChatUtilities.LogType.ADMIN_UTILITY);
					for(Rank r : Rank.values()) {
						String log = r.color + r.title + ChatColor.GRAY + ", level " + r.level + ChatColor.RESET;
						ChatUtilities.messageTo(sender, log, ChatColor.GRAY);
					}		
				}
			};
			
			registerFunction("set", setFunction, 2);
			registerFunction("list", listFunction, 0);
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return true;
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			doc.editPage(0).write(ChatColor.RED + "This admin utility is used to help you administrate" + ChatColor.RESET, 1);
			doc.editPage(0).write(ChatColor.RED + "    players' ranks manually." + ChatColor.RESET, 2);
			doc.editPage(0).write("Use [ranks-set-<name>-<rank-name>] to set an ONLINE players'", 3);
			doc.editPage(0).write("    rank with the name.", 4);
			doc.editPage(0).write("Use [ranks-set-<name>-<rank-level>] to set an ONLINE players'", 5);
			doc.editPage(0).write("    rank with the level.", 6);
			doc.editPage(0).write("Use [ranks-list] to see information about available ranks.", 7);
			doc.editPage(0).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
		}
	}
	
	private static ArrayList<RankEntry> RANK_STATE;
	
	public Ranks() {
		RANK_STATE = new ArrayList<RankEntry>();
		translateRegister();
	}
	
	public static class RankEntry {
		public String playerName;
		public Rank rank;
		public int level;
		public RankEntry(String playerName) {
			this.playerName = playerName;
			this.level = 1;
			rank = Rank.HOBBYIST;
		}
	}
	
	public enum Rank {
		OWNER("Owner", 8, ChatColor.DARK_RED, ChatColor.RED),
		ADMIN("Admin", 7, ChatColor.RED, ChatColor.LIGHT_PURPLE),
		MODERATOR("Moderator", 6, ChatColor.BLUE, ChatColor.DARK_BLUE),
		TRILLIONAIRE("Trillionaire", 5, ChatColor.DARK_GREEN, ChatColor.GREEN),
		BILLIONAIRE("Billionaire", 4, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE),
		MILLIONAIRE("Millionaire", 3, ChatColor.GREEN, ChatColor.DARK_AQUA),
		ENGINEER("Engineer", 2, ChatColor.YELLOW, ChatColor.GOLD),
		HOBBYIST("Hobbyist", 1, ChatColor.GRAY, ChatColor.WHITE);
		public final ChatColor color, brackets;
		public final String title;
		public final int level;
	    private Rank(String title, int level, ChatColor color, ChatColor brackets) {
	        this.color = color;
	        this.title = title;
	        this.level = level;
	        this.brackets = brackets;
	    }
	}
	
	public static Rank getRankFromName(String name) {
		for(Rank r : Rank.values())
			if(r.title.equals(name))
				return r;
		return Rank.HOBBYIST;
	}
	
	public static String introduce(Rank rank) {
		return ChatColor.BOLD + "" + rank.brackets + "[" + rank.color + 
				ChatColor.BOLD + rank.title + ChatColor.RESET + ChatColor.BOLD + rank.brackets + "] " + ChatColor.RESET;
	}
	
	public static String introShort(Rank rank) {
		return ChatColor.BOLD + "" + rank.brackets + "[" + rank.color + 
				ChatColor.BOLD + rank.title.charAt(0) + ChatColor.RESET + ChatColor.BOLD + rank.brackets + "] " + ChatColor.RESET;
	}

	public static Rank getRankFromLevel(int level) {
		for(Rank r : Rank.values())
			if(r.level == level)
				return r;
		return Rank.HOBBYIST;
	}
	
	public static void setRank(Player player, Rank rank, boolean alert) {
		for(RankEntry e : RANK_STATE) {
			if(e.playerName.equals(player.getName())) {
				e.rank = rank;
				e.level = rank.level;
				if(alert) {
					SoundUtilities.playSoundToOnline("FIREWORK_TWINKLE");
					ChatUtilities.messageOnline(ChatColor.GRAY + "Player " + player.getName() + " is now rank " + 
						rank.color + rank.title + ChatColor.RESET + ChatColor.GRAY + "!", ChatColor.AQUA);
				}
				player.setPlayerListName(rank.color + player.getName());
				writeRegister();
				return;
			}
		}
		RANK_STATE.add(new RankEntry(player.getName()));
		setRank(player, rank, alert);
	}
	
	public static void setRank(String playerName, Rank rank, boolean alert) {
		for(RankEntry e : RANK_STATE) {
			if(e.playerName.equals(playerName)) {
				e.rank = rank;
				e.level = rank.level;
				if(alert) {
					SoundUtilities.playSoundToOnline("FIREWORK_TWINKLE");
					ChatUtilities.messageOnline(ChatColor.GRAY + "Player " + playerName + " is now rank " + 
							rank.color + rank.title + ChatColor.RESET + ChatColor.GRAY + "!", ChatColor.AQUA);
				}
				try {
					PlayerUtilities.get(playerName).setPlayerListName(rank.color + playerName);
				} catch(Exception ee) { return; }
				writeRegister();
				return;
			}
		}
		RANK_STATE.add(new RankEntry(playerName));
		setRank(playerName, rank, alert);
	}
	
	public static Rank getPlayerRank(Player player) {
		for(RankEntry e : RANK_STATE) 
			if(e.playerName.equals(player.getName()))
				return e.rank;
		setRank(player, Rank.HOBBYIST, false);
		return Rank.HOBBYIST;
	}
	
	public static boolean canUseFeature(Player player, Rank permission) {
		return getPlayerRank(player).level >= permission.level;
	}
	
	public static String tag(Player player) {
		Rank pR = getPlayerRank(player);
		return ChatColor.BOLD + "" + pR.brackets + "[" + pR.color + 
				ChatColor.BOLD + pR.title + ChatColor.RESET + ChatColor.BOLD + pR.brackets + "] " + ChatColor.RESET;
	}
	
	private static void translateRegister() {
		if(!FileUtilities.fileExists("ranks")) FileUtilities.createDataFile("ranks");
		RANK_STATE.clear();
		ArrayList<String[]> data = FileUtilities.readDataFileLines("ranks");
		for(String[] lineDat : data) {
			RANK_STATE.add(new RankEntry(lineDat[0]));
			setRank(lineDat[0], getRankFromLevel(Integer.parseInt(lineDat[1])), false);
		}
	}
	
	private static void writeRegister() {
		FileUtilities.clearDataFile("ranks");
		for(RankEntry entry : RANK_STATE) {
			String data = entry.playerName + " " + entry.rank.level;
			FileUtilities.appendDataEntryTo("ranks", data);
		}
	}
}
