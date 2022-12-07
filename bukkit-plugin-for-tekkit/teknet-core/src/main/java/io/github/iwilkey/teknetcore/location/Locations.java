package io.github.iwilkey.teknetcore.location;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.location.Locations.Positions.PositionData.Position;
import io.github.iwilkey.teknetcore.ranks.Ranks;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation.Page;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.MathUtilities;
import io.github.iwilkey.teknetcore.utils.PlayerUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class Locations {
	
	public Locations() {
		new Home();
		new Teleport();
		new Positions();
	}
	
	public static void tick() {
		Teleport.tick();
	}
	
	/*
	 * 
	 * TELEPORT
	 * 
	 */
	public static class Teleport {
		
		private static final int[] TELEPORT_WAIT_TIMES = {
				6, // Hobbyist
				5, // Engineer
				4, // Million
				3, // Billion
				2, // Trillion
				1, // Moderator
				0, // Admin
				0  // Owner
		};
		
		public static class TeleportRequest {
			public String playerName;
			public Location destination;
			public int ticksSince,
				secLeftOld = 0,
				secLeftNew = (int)TELEPORT_WAIT_TIMES[0];
			public String messageDone;
			public float secondsSince;
			public TeleportRequest(String playerName, Location l, String messageDone) {
				this.playerName = playerName;
				this.destination = l;
				ticksSince = 0;
				secondsSince = 0.0f;
				this.messageDone = messageDone;
			}
		}
		
		private static ArrayList<TeleportRequest> TELEPORT_STATE,
			toDelete;
		
		public Teleport() {
			TELEPORT_STATE = new ArrayList<>();
			toDelete = new ArrayList<>();
		}
		
		public static boolean teleportTo(Player p, Location l, String messageDone) {
			for(TeleportRequest request : TELEPORT_STATE) {
				if(request.playerName.equals(p.getName())) {
					SoundUtilities.playSoundTo("NOTE_BASS_GUITAR", p);
					ChatUtilities.logTo(p, "You already have a teleportation in progress!", ChatUtilities.LogType.FATAL);
					return false;
				}
			}
			Rank playerRank = Ranks.getPlayerRank(p);
			SoundUtilities.playSoundTo("WOOD_CLICK", p);
			TeleportRequest request = new TeleportRequest(p.getName(), l, messageDone);
			if(request.secLeftNew > 0 && request.secLeftNew != TELEPORT_WAIT_TIMES[playerRank.level - 1] && playerRank.level < 7)
				ChatUtilities.messageTo(p, "Teleporting in " + ChatColor.GREEN + TELEPORT_WAIT_TIMES[playerRank.level - 1] + ChatColor.GOLD + " (s)...", ChatColor.GOLD);
			request.secLeftNew = TELEPORT_WAIT_TIMES[playerRank.level - 1];
			TELEPORT_STATE.add(request);
			return true;
		}
		
		public static void tick() {
			if(TELEPORT_STATE.size() == 0) return;
			if(toDelete.size() != 0) toDelete.clear();
			for(TeleportRequest request : TELEPORT_STATE) {
				Player p = PlayerUtilities.get(request.playerName);
				if(p == null) {
					toDelete.add(request);
					continue;
				}
				request.secLeftOld = request.secLeftNew;
				request.ticksSince++;
				request.secondsSince = (1.0f / TeknetCore.SERVER_TPS) * request.ticksSince;
				
				Rank playerRank = Ranks.getPlayerRank(p);
				
				request.secLeftNew = (int)(TELEPORT_WAIT_TIMES[playerRank.level - 1] - request.secondsSince) + 1;
				if(request.secLeftNew != request.secLeftOld) {
					SoundUtilities.playSoundTo("WOOD_CLICK", p);
					if(request.secLeftNew > 0 && request.secLeftNew != TELEPORT_WAIT_TIMES[playerRank.level - 1] && playerRank.level < 7)
						ChatUtilities.messageTo(p, "Teleporting in " + ChatColor.GREEN + request.secLeftNew + ChatColor.GOLD + " (s)...", ChatColor.GOLD);
				}
				if(request.secondsSince >= TELEPORT_WAIT_TIMES[playerRank.level - 1]) {
					Location l = p.getLocation();
					l.setX(request.destination.getX());
					l.setY(request.destination.getY());
					l.setZ(request.destination.getZ());
					l.setWorld(request.destination.getWorld());
					p.teleport(l);
					p.getLocation().getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 512);
					SoundUtilities.playSoundTo("LEVEL_UP", p);
					ChatUtilities.messageTo(Bukkit.getPlayer(request.playerName), request.messageDone, ChatColor.LIGHT_PURPLE);
					toDelete.add(request);
				}
			}
			if(toDelete.size() != 0) for(TeleportRequest r : toDelete) TELEPORT_STATE.remove(r);
		}
	}
	
	/*
	 * 
	 * POSITIONS
	 * 
	 */
	public static class Positions {
		
		public static final int[] MAX_POSITIONS = {
				2, // Hobbyist
				4, // Engineer
				8, // Million
				16, // Billion
				32, // Trillion
				64, // Moderator
				256, // Admin
				512  // Owner
		};
		
		public static class PositionCommand extends TeknetCoreCommand {

			public PositionCommand(Rank permissions) {
				super("position", permissions);
				
				Function saveFunction = new Function() {
					@Override
					public void func(Player sender, String[] args) {
						PositionData data = getPlayerPositionData(sender);
						if(data == null) {
							POSITION_STATE.add(new PositionData(sender.getName()));
							func(sender, args);
							return;
						}
						Rank playerRank = Ranks.getPlayerRank(sender);
						if(data.locations.size() + 1 <= MAX_POSITIONS[playerRank.level - 1]) {
							for(Position p : data.locations)
								if(p.name.equals(args[1])) {
									ChatUtilities.logTo(sender, "You have already saved a position with this name!", ChatUtilities.LogType.FATAL);
									return;
								}
							data.locations.add(new Position(args[1], sender.getLocation()));
							ChatUtilities.logTo(sender, "Position saved!", ChatUtilities.LogType.SUCCESS);
							writeRegister();
						} else ChatUtilities.logTo(sender, "You cannot create a new position because you already"
								+ " have been granted the maximum amount of allotted positions. Try [position-delete-<name>]...", ChatUtilities.LogType.FATAL);
					}
				};
						
				Function deleteFunction = new Function() {
					@Override
					public void func(Player sender, String[] args) {
						PositionData data = getPlayerPositionData(sender);
						if(data == null) {
							ChatUtilities.logTo(sender, "You have saved no positions to delete!", ChatUtilities.LogType.FATAL);
							return;
						}
						if(args[1].equals("all")) {
							ArrayList<Position> toDelete = new ArrayList<>();
							for(Position p : data.locations) 
								toDelete.add(p);
							for(Position p : toDelete)
								data.locations.remove(p);
							ChatUtilities.logTo(sender, "All positions deleted.", ChatUtilities.LogType.SUCCESS);
							writeRegister();
							return;
						}
						for(Position p : data.locations) 
							if(p.name.equals(args[1])) {
								data.locations.remove(p);
								ChatUtilities.logTo(sender, "Position deleted.", ChatUtilities.LogType.SUCCESS);
								writeRegister();
								return;
							}
						ChatUtilities.logTo(sender, "You cannot delete a position that doesn't exist!", ChatUtilities.LogType.FATAL);
					}
				};
				
				Function listFunction = new Function() {
					@Override
					public void func(Player sender, String[] args) {
						PositionData data = getPlayerPositionData(sender);
						if(data == null) {
							ChatUtilities.logTo(sender, "You have no positions to show! Use\n [position-save-<name>] to save one!", ChatUtilities.LogType.UTILITY);
							return;
						}
						if(data.locations.size() == 0) {
							ChatUtilities.logTo(sender, "You have no positions to show! Use\n [position-save-<name>] to save one!", ChatUtilities.LogType.UTILITY);
							return;
						} else ChatUtilities.logTo(sender, "Saved positions...", ChatUtilities.LogType.UTILITY);
						boolean toggle = false;
						int i = 1;
						for(Position p : data.locations) {
							String[] out = new String[3];
							ChatColor worldReflection;
							String worldName;
							if(p.position.getWorld().getName().equals("world")) {
								worldReflection = (!toggle) ? ChatColor.GREEN : ChatColor.DARK_GREEN;
								worldName = "Overworld";
							} else if(p.position.getWorld().getName().equals("DIM-1")) {
								worldReflection = (!toggle) ? ChatColor.RED : ChatColor.DARK_RED;
								worldName = "The Nether";
							} else {
								worldReflection = (!toggle) ? ChatColor.YELLOW : ChatColor.GOLD;
								worldName = "The End";
							}
							Rank playerRank = Ranks.getPlayerRank(sender);
							toggle = !toggle;
							out[0] = worldReflection + "" + ChatColor.BOLD + "◄Name► " + ChatColor.RESET + ChatColor.AQUA + p.name;
							out[1] = worldReflection + "" + ChatColor.BOLD + "◄World► " + ChatColor.RESET + ((toggle) ? ChatColor.WHITE : ChatColor.GRAY) + worldName;
							out[2] = worldReflection + "" + ChatColor.BOLD + "◄Location► " + ChatColor.RESET + ((toggle) ? ChatColor.WHITE : ChatColor.GRAY) + ChatColor.ITALIC + 
									"x: " + p.position.getBlockX() + ", y: " + p.position.getBlockY() + ", z: " + p.position.getBlockZ();
							String out1 = " " + i + " / " + MAX_POSITIONS[playerRank.level - 1] + " " + out[1],
								center1 = "", center2 = "";
							boolean c2 = false;
							for(int ii = 0; ii < out1.length(); ii++) {
								if(out1.charAt(ii) == '◄') break;
								if(out1.charAt(ii) == '/') {
									c2 = true;
									continue;
								}
								if(!c2) center1 += " ";
								else center2 += " ";
							}
							ChatUtilities.messageTo(sender, center1 + " ▲" + center2.substring(0, center2.length() - 3) + out[0], ChatColor.GRAY);
							ChatUtilities.messageTo(sender, " " + i + " / " + MAX_POSITIONS[playerRank.level - 1] + " " + out[1], ChatColor.GRAY);
							ChatUtilities.messageTo(sender, center1 + " ▼" + center2.substring(0, center2.length() - 3) + out[2], ChatColor.GRAY);
							i++;
						}
					}
				};
				
				Function goFunction = new Function() {
					@Override
					public void func(Player sender, String[] args) {
						PositionData data = getPlayerPositionData(sender);
						if(data == null) {
							ChatUtilities.logTo(sender, "You have no positions to go to!", ChatUtilities.LogType.FATAL);
							return;
						}
						for(Position p : data.locations)
							if(p.name.equals(args[1])) {
								Locations.Teleport.teleportTo(sender, p.position, "Poof!");
								return;
							}
						ChatUtilities.logTo(sender, "You do not have a saved location with this name!", ChatUtilities.LogType.FATAL);
					}
				};
				
				Function randomFunction = new Function() {
					private final int RANGE = 10000;
					@Override
					public void func(Player sender, String[] args) {
						int x = MathUtilities.randomIntBetween(sender.getLocation().getBlockX() - RANGE, 
								sender.getLocation().getBlockX() + RANGE);
					    int y = 156;
					    int z = MathUtilities.randomIntBetween(sender.getLocation().getBlockZ() - RANGE, 
					    		sender.getLocation().getBlockZ() + RANGE);
					    if(sender.getWorld().getBlockAt(x, y, z).isEmpty() 
					    		&& !sender.getWorld().getBlockAt((int)x, (int)y, (int)z).isLiquid()) {
							while(sender.getWorld().getBlockAt(x, y - 1, z).isEmpty() && y > 0) y--;
							Location lpp = sender.getLocation();
							lpp.setX(x);
							lpp.setY(y + 2);
							lpp.setZ(z);
							Teleport.teleportTo(sender, lpp, "Is this location good enough for you?");
					    }
					}
				};
				
				Function renameFunction = new Function() {
					@Override
					public void func(Player sender, String[] args) {
						Position target = returnPosition(sender, args[1], true);
						if(target == null) return;
						Position chec = returnPosition(sender, args[2], false);
						if(chec != null) {
							ChatUtilities.logTo(sender, "You cannot rename a position using a name you are already using!", ChatUtilities.LogType.FATAL);
							return;
						}
						target.name = args[2];
						ChatUtilities.logTo(sender, "Position renamed.", ChatUtilities.LogType.SUCCESS);
						writeRegister();
					}
				};
				
				registerFunction("save", saveFunction, 1, "s");
				registerFunction("delete", deleteFunction, 1, "d");
				registerFunction("list", listFunction, 0, "l");
				registerFunction("go", goFunction, 1, "g");
				registerFunction("rename", renameFunction, 2, "r");
				registerFunction("random", randomFunction, 0, "rand");
				translateRegister();
			}
			
			@Override
			protected void documentation(CommandDocumentation doc) {
				doc.editPage(0).write(ChatColor.GOLD + "This utility is used for managing saved positions." + ChatColor.RESET, 1);
				doc.editPage(0).write("Use [position-help] to show this help page again.", 2);
				doc.editPage(0).write("Use [position-save-<name>] to save and name your position!", 3);
				doc.editPage(0).write("Use [position-go-<name>] to teleport to a saved position.", 4);
				doc.editPage(0).write("Use [position-delete-<name-OR-'all'>] to delete an existing", 5);
				doc.editPage(0).write("    (or all) position(s).", 6);
				doc.editPage(0).write("Use [position-list] to show all current saved positions.", 7);
				doc.editPage(0).write(ChatColor.GRAY + "------- More commands on next page... -------" + ChatColor.RESET, 8);
				doc.addPage(new Page());
				doc.editPage(1).write("Use [position-rename-<target>-<new-name>] to rename an", 0);
				doc.editPage(1).write("    existing position.", 1);
				doc.editPage(1).write("Use [position-random] to teleport to a random position.", 2);
				doc.editPage(1).write("", 3);
				doc.editPage(1).write("", 4);
				doc.editPage(1).write("", 5);
				doc.editPage(1).write("", 6);
				doc.editPage(1).write("", 7);
				doc.editPage(1).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
			}

			@Override
			public boolean logic(Player sender, Command command, String label, String[] args) {
				return true;
			}
		}
		
		public static class PositionData {
			
			public static class Position {
				String name;
				Location position;
				public Position(String name, Location position) {
					this.name = name;
					this.position = position;
				}
			}
			
			String playerName;
			ArrayList<Position> locations;
			public PositionData(String playerName) {
				this.playerName = playerName;
				locations = new ArrayList<>();
			}
			
		}
		
		private static ArrayList<PositionData> POSITION_STATE;
		
		public Positions() {
			POSITION_STATE = new ArrayList<>();
		}
		
		private static PositionData getPlayerPositionData(Player player) {
			for(PositionData data : POSITION_STATE) 
				if(data.playerName.equals(player.getName()))
					return data;
			return null;
		}
		
		private static Position returnPosition(Player player, String name, boolean checking) {
			PositionData data = getPlayerPositionData(player);
			if(data == null) {
				ChatUtilities.logTo(player, "You have no positions to rename!", ChatUtilities.LogType.FATAL);
				return null;
			}
			for(Position p : data.locations) 
				if(p.name.equals(name))
					return p;
			if(checking) ChatUtilities.logTo(player, "You do not have a saved position by this name!", ChatUtilities.LogType.FATAL);
			return null;
		}
		
		private static void translateRegister() {
			if(!FileUtilities.fileExists("positions")) FileUtilities.createDataFile("positions");
			POSITION_STATE.clear();
			ArrayList<String[]> data = FileUtilities.readDataFileLines("positions");
			for(String[] lineDat : data) {
				PositionData pd = new PositionData(lineDat[0]);
				for(int i = 1; i < lineDat.length; i++) {
					String[] loc = lineDat[i].split(";");
					Location l = new Location(Bukkit.getServer().getWorld(loc[1]), Float.parseFloat(loc[2]), Float.parseFloat(loc[3]), Float.parseFloat(loc[4]));
					pd.locations.add(new Position(loc[0], l));
				}
				POSITION_STATE.add(pd);
			}
		}
		
		private static void writeRegister() {
			FileUtilities.clearDataFile("positions");
			for(PositionData dat : POSITION_STATE) {
				String data = dat.playerName + " ";
				for(Position p : dat.locations)
					data += p.name + ";" + p.position.getWorld().getName() + ";" + p.position.getX() + ";" + p.position.getY() + ";" + p.position.getZ() + " ";
				FileUtilities.appendDataEntryTo("positions", data);
			}
		}
	}
	
	/*
	 * 
	 * HOME
	 * 
	 */
	public static class Home {
		
		public static class SetHome extends TeknetCoreCommand {
			public SetHome(Rank permissions) {
				super("sethome", permissions);
			}
			@Override
			public boolean logic(Player sender, Command command, String label, String[] args) {
				setHome(sender);
				return true;
			}
			@Override
			protected void documentation(CommandDocumentation doc) {
				doc.editPage(0).write(ChatColor.GOLD + "This utility is used to help you save" + ChatColor.RESET, 1);
				doc.editPage(0).write(ChatColor.GOLD + "    your base location." + ChatColor.RESET, 2);
				doc.editPage(0).write("Use [sethome-help] to show this help page again.", 3);
				doc.editPage(0).write("Use [sethome] to save your current position as your home.", 4);
				doc.editPage(0).write("Use [home] to teleport to your home.", 5);
				doc.editPage(0).write("", 6);
				doc.editPage(0).write("", 7);
				doc.editPage(0).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
			}
		}
		
		public static class HomeCommand extends TeknetCoreCommand {
			public HomeCommand(Rank permissions) {
				super("home", permissions);
			}
			@Override
			public boolean logic(Player sender, Command command, String label, String[] args) {
				Location home = returnHome(sender);
				if(home == null) {
					ChatUtilities.logTo(sender, "You do not have a home to go to! Use [sethome] to set one.", ChatUtilities.LogType.FATAL);
					return true;
				}
				Teleport.teleportTo(sender, home, "Welcome home.");
				return true;
			}
			@Override
			protected void documentation(CommandDocumentation doc) {
				doc.editPage(0).write(ChatColor.GOLD + "This utility is used to help you save" + ChatColor.RESET, 1);
				doc.editPage(0).write(ChatColor.GOLD + "    your base location." + ChatColor.RESET, 2);
				doc.editPage(0).write("Use [sethome-help] to show this help page again.", 3);
				doc.editPage(0).write("Use [sethome] to save your current position as your home.", 5);
				doc.editPage(0).write("Use [home] to teleport to your home.", 4);
				doc.editPage(0).write("", 6);
				doc.editPage(0).write("", 7);
				doc.editPage(0).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
			}
			
		}
		
		public Home() {
			HOME_STATE = new ArrayList<>();
			translateRegister();
		}
		
		public static class HomeData {
			public String playerName;
			public Location home;
			public HomeData(String playerName) {
				this.playerName = playerName;
			}
		}
		
		private static ArrayList<HomeData> HOME_STATE;
		
		public static void setHome(Player player) {
			for(HomeData data : HOME_STATE) 
				if(data.playerName.equals(player.getName())) {
					data.home = player.getLocation();
					SoundUtilities.playSoundTo("ANVIL_LAND", player);
					ChatUtilities.logTo(player, "Home set.", ChatUtilities.LogType.SUCCESS);
					writeRegister();
					return;
				}
			HOME_STATE.add(new HomeData(player.getName()));
			setHome(player);
		}
		
		public static Location returnHome(Player p) {
			for(HomeData data : HOME_STATE) 
				if(data.playerName.equals(p.getName()))
					return data.home;
			return null;
		}
		
		private static void translateRegister() {
			if(!FileUtilities.fileExists("register")) FileUtilities.createDataFile("register");
			HOME_STATE.clear();
			ArrayList<String[]> data = FileUtilities.readDataFileLines("register");
			for(String[] lineDat : data) {
				HomeData create = new HomeData(lineDat[0]);
				create.home = new Location(Bukkit.getServer().getWorld(lineDat[1]), Float.parseFloat(lineDat[2]), 
						Float.parseFloat(lineDat[3]), Float.parseFloat(lineDat[4]));
				HOME_STATE.add(create);
			}
		}
		
		private static void writeRegister() {
			FileUtilities.clearDataFile("register");
			for(HomeData entry : HOME_STATE) {
				String data = entry.playerName + " " + Bukkit.getServer().getWorld(entry.home.getWorld().getName()).getName()
						+ " " + entry.home.getX() + " " + entry.home.getY() + " " + entry.home.getZ();
				FileUtilities.appendDataEntryTo("register", data);
			}
		}
	}
}
