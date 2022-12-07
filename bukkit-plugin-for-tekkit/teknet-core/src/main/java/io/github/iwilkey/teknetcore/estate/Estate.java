package io.github.iwilkey.teknetcore.estate;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.economy.Bank.Currency;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.MathUtilities;
import io.github.iwilkey.teknetcore.utils.PlayerUtilities;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities.Sequence;
import io.github.iwilkey.teknetcore.utils.SequenceUtilities.SequenceFunction;

import java.awt.Rectangle;

public class Estate {
	
	public static class EstateCommand extends TeknetCoreCommand {
		public EstateCommand(Rank permissions) {
			super("estate", permissions);
			
			Function estateCreate = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					if(!createEstateInstance(sender, args[1])) return;
					ChatUtilities.logTo(sender, "Estate created. Use [estate-manage-" + args[1] + "] to proceed!", 
							ChatUtilities.LogType.SUCCESS);
				}	
			};
			
			Function estateManage = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					if(args.length == 1) {
						ChatUtilities.logTo(sender, "You must specify the estate you are managing!", ChatUtilities.LogType.FATAL);
						return;
					}
					EstateInstance s = getEstateInstance(sender, args[1]);
					if(s == null) { 
						ChatUtilities.logTo(sender, "You do not own an estate by this name!", ChatUtilities.LogType.FATAL);
						return;
					}
					if(args.length == 2) {
						// Show estate properties and help here...
						printEstateInformationTo(s, sender);
						ChatUtilities.messageTo(sender, " ► Use [estate-manage-" + s.estateName + "-resize-<number>] to change the size (more rent).", ChatColor.GRAY);
						ChatUtilities.messageTo(sender, " ► Use [estate-manage-" + s.estateName + "-(un)trust-<player-name>] to (dis)allow another player use of the space.", ChatColor.GRAY);
					} else {
						switch(args[2]) {
							case "resize":
								if(args.length != 4) {
									ChatUtilities.logTo(sender, "Incorrect estate resize argument(s). Remember, it's: [estate-manage-" + s.estateName + "-resize-<number>]", ChatUtilities.LogType.FATAL);
									return;
								} 
								int size;
								try {
									size = Integer.parseInt(args[3]);
								} catch(Exception e) {
									ChatUtilities.logTo(sender, "You must supply a valid number! Remember, it's: [estate-manage-" + s.estateName + "-resize-<number>]", ChatUtilities.LogType.FATAL);
									return;
								}
								if(size < 2) {
									ChatUtilities.logTo(sender, "You must supply a number greater than or equal to 2.", ChatUtilities.LogType.FATAL);
									return;
								}
								if(!proposedEstateOverlap(s.estateName, s.centerLocation, size)) {
									ChatUtilities.logTo(sender, "You cannot resize this property to that "
											+ "value because the resulting area will overlap with an existing estate.", ChatUtilities.LogType.FATAL);
									return;
								}
								s.size = size;
								writeRegister();
								showEstateTo(s, sender);
								ChatUtilities.logTo(sender, "Region resized. You now owe\n" + s.rent.printValueColored() + " per day.", ChatUtilities.LogType.SUCCESS);
								break;
							case "trust":
								if(args.length != 4) {
									ChatUtilities.logTo(sender, "Incorrect estate trust argument(s). Remember, it's: [estate-manage-" + s.estateName + "-trust-<player-name>]", ChatUtilities.LogType.FATAL);
									return;
								}
								for(String name : s.members)
									if(name.equals(args[3])) {
										ChatUtilities.logTo(sender, "You already trust this player to interact in this region. "
												+ "If you changed your mind, use [estate-manage-" + s.estateName + 
												"-untrust-" + args[3] + "]", ChatUtilities.LogType.NOTICE);
										return;
									}
								s.members.add(args[3]);
								ChatUtilities.logTo(sender, ChatColor.DARK_GREEN + "You now trust player " + ChatColor.GOLD + args[3] + ChatColor.DARK_GREEN + " to place, destroy, and interact with blocks in this region!", ChatUtilities.LogType.SUCCESS);
								writeRegister();
								Player p = PlayerUtilities.get(args[3]);
								if(p != null) {
									ChatUtilities.logTo(sender, "The " + ChatColor.DARK_GREEN + s.estateName + ChatColor.WHITE + " "
											+ "Teknet Estate owner now trusts you to place, destroy, and "
											+ "interact with blocks in the region.", ChatUtilities.LogType.NOTICE);
								}
								break;
							case "untrust":
								if(args.length != 4) {
									ChatUtilities.logTo(sender, "Incorrect estate untrust argument(s). Remember, it's: [estate-manage-" + s.estateName + "-untrust-<player-name>]", ChatUtilities.LogType.FATAL);
									return;
								}
								boolean found = false;
								for(String name : s.members)
									if(name.equals(args[3]))
										found = true;
								if(!found) {
									ChatUtilities.logTo(sender, "You already do not trust anyone by the name " + ChatColor.GOLD + args[3] + ChatColor.GRAY + " in this region.", ChatUtilities.LogType.NOTICE);
									return;
								}
								s.members.remove(args[3]);
								writeRegister();
								ChatUtilities.logTo(sender, ChatColor.RED + "You now DO NOT trust player\n" + ChatColor.GOLD + args[3] + ChatColor.RED + " to place, "
										+ "destroy, and interact with blocks in this region!", ChatUtilities.LogType.SUCCESS);
								break;
							default:
								ChatUtilities.logTo(sender, "Not a valid estate property! Use [estate-manage-" + s.estateName + "] for help.", ChatUtilities.LogType.FATAL);
								return;
						}
					}
				}
			};
			
			Function estateDelete = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					EstateInstance s = getEstateInstance(sender, args[1]);
					if(s == null) {
						ChatUtilities.logTo(sender, "You cannot delete this estate because "
								+ "either it doesn't exist or you do not own it.", ChatUtilities.LogType.FATAL);
						return;
					}
					Estate.ESTATE_STATE.remove(s);
					writeRegister();
					ChatUtilities.logTo(sender, "Estate " + ChatColor.GOLD +  args[1] + ChatColor.GRAY + " removed.", ChatUtilities.LogType.SUCCESS);
					return;
				}
			};
			
			Function estateList = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
				}
			};
			
			Function estateRename = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ChatUtilities.logTo(sender, "This function is not currently implemented.", ChatUtilities.LogType.NOTICE);
				}
			};
			
			Function estateCurrent = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					EstateInstance s = getCurrentInstanceOf(sender);
					if(s == null) {
						ChatUtilities.logTo(sender, "You are not currently inside of an estate. "
								+ "Create one with [estate-create-<name>]", ChatUtilities.LogType.NOTICE);
						return;
					}
					printEstateInformationTo(s, sender);
				}
			};

			registerFunction("create", estateCreate, 1, "make");
			registerFunction("delete", estateDelete, 1, "remove");
			registerFunction("rename", estateRename, 1, "name");
			registerFunction("list", estateList, 1);
			registerFunction("current", estateCurrent, 0);
			registerFunction("manage", estateManage, "edit");
			
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return false;
		}
	}
	
	public static class EstateInstance {
		public String owner,
			estateName;
		public ArrayList<String> members, visitors;
		public Location centerLocation;
		public long size;
		public Currency rent;
		public EstateInstance(Player player, String name) {
			this.owner = player.getName();
			this.estateName = name;
			this.centerLocation = player.getLocation();
			members = new ArrayList<>();
			visitors = new ArrayList<>();
			members.add(owner);
			size = 3;
			rent = new Currency(10025); // Calculate rent based off size...
			Estate.showEstateTo(this, player);
		}
		public EstateInstance(String playerName, String name, Location center, int size) {
			this.owner = playerName;
			this.estateName = name;
			this.centerLocation = center;
			visitors = new ArrayList<>();
			this.size = size;
			rent = new Currency(10025); // Calculate rent based off size...
		}
		public boolean inRegion(Player player) {
			return visitors.contains(player.getName());
		}
	}
	
	public static ArrayList<EstateInstance> ESTATE_STATE;
	
	public Estate() {
		ESTATE_STATE = new ArrayList<>();
		translateRegister();
	}
	
	public static void tick() {
		for(EstateInstance e : ESTATE_STATE) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				Location l = p.getLocation();
				long x = l.getBlockX(), z = l.getBlockZ();
				if(MathUtilities.locationInEstateRegion(e.centerLocation.getBlockX(), e.centerLocation.getBlockZ(), x, z, e.size)) {
					if(!e.inRegion(p)) {
						ChatUtilities.logTo(p, ChatColor.GREEN + 
								"You have just entered the Teknet Estate \"" + ChatColor.DARK_GREEN 
								+ e.estateName.toUpperCase() + ChatColor.GREEN + "\" owned by " + ChatColor.DARK_GREEN + e.owner + ChatColor.GREEN + "! Use [estate-current] " + ChatColor.GREEN + "to see it's information.", ChatUtilities.LogType.NOTICE);
						e.visitors.add(p.getName());
					}
				} else {
					if(e.inRegion(p)) {
						ChatUtilities.logTo(p, ChatColor.GRAY + 
								"You have just left the Teknet Estate \"" + ChatColor.WHITE 
								+ e.estateName.toUpperCase() + ChatColor.GRAY + "\" owned by " + ChatColor.WHITE + e.owner + ChatColor.GRAY + "!", ChatUtilities.LogType.NOTICE);
						e.visitors.remove(p.getName());
					}
				}
			}
		}
	}
	
	public static EstateInstance getCurrentInstanceOf(Player player) {
		for(EstateInstance e : ESTATE_STATE) {
			Location l = player.getLocation();
			long x = l.getBlockX(), z = l.getBlockZ();
			if(MathUtilities.locationInEstateRegion(e.centerLocation.getBlockX(), 
					e.centerLocation.getBlockZ(), x, z, e.size))
				return e;
		}
		return null;
	}
	
	public static void printEstateInformationTo(EstateInstance s, Player sender) {
		SoundUtilities.playSoundTo("ARROW_HIT", sender);
		ChatUtilities.messageTo(sender, "------- Teknet Estates Listing -------", ChatColor.WHITE);
		ChatUtilities.messageTo(sender, " ► Estate name: " + ChatColor.GOLD + s.estateName.toUpperCase(), ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate owner: " + ChatColor.GOLD + s.owner, ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate size: " + ChatColor.GOLD + s.size + ChatColor.GRAY + " [" + ChatColor.YELLOW + ((s.size * 2) + 1) + " x " + ((s.size * 2) + 1) + ChatColor.GRAY + " (Center: " + (s.size + 1) + ", " + (s.size + 1) + ")]", ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate location (center): " + ChatColor.GOLD 
				+ "x: " + s.centerLocation.getBlockX() + ", z: " + s.centerLocation.getBlockZ() 
				+ ChatColor.GRAY + "\n    (Your location): " + ChatColor.YELLOW + "x: " + sender.getLocation().getBlockX() + ", z: " +
				sender.getLocation().getBlockZ(), ChatColor.GRAY);
		ChatUtilities.messageTo(sender, " ► Estate rent ($/day): " + s.rent.printValueColored(), ChatColor.GRAY);
		String trusted = ChatColor.GOLD + "";
		int n = 0;
		for(String name : s.members) {
			trusted += name;
			if(n != s.members.size() - 1) trusted += ChatColor.GRAY + ", " + ChatColor.GOLD;
			n++;
		}
		ChatUtilities.messageTo(sender, " ► Trusted players: " + trusted, ChatColor.GRAY);
		showEstateTo(s, sender);
	}
	
	public static void showEstateTo(EstateInstance inst, Player player) {
		SequenceFunction f = new SequenceFunction() {
			@Override
			public void onIteration(Object... objects) {
				for(long dir = -inst.size; dir <= inst.size; dir++) {
					Location lx = new Location(inst.centerLocation.getWorld(), 
							inst.centerLocation.getBlockX() + dir, player.getLocation().getBlockY() - 1, 
							inst.centerLocation.getBlockZ());
					Location lz = new Location(inst.centerLocation.getWorld(), 
							inst.centerLocation.getBlockX(), player.getLocation().getBlockY() - 1, 
							inst.centerLocation.getBlockZ() + dir);
					Location nxlb = new Location(inst.centerLocation.getWorld(), 
							inst.centerLocation.getBlockX() - dir, player.getLocation().getBlockY() - 1, 
							inst.centerLocation.getBlockZ() + dir);
					Location nzlb = new Location(inst.centerLocation.getWorld(), 
							inst.centerLocation.getBlockX() + dir, player.getLocation().getBlockY() - 1, 
							inst.centerLocation.getBlockZ() - dir);
					Location pblb = new Location(inst.centerLocation.getWorld(), 
							inst.centerLocation.getBlockX() + dir, player.getLocation().getBlockY() - 1, 
							inst.centerLocation.getBlockZ() + dir);
					Location nblb = new Location(inst.centerLocation.getWorld(), 
							inst.centerLocation.getBlockX() - dir, player.getLocation().getBlockY() - 1, 
							inst.centerLocation.getBlockZ() - dir);
					player.playEffect(lx, Effect.MOBSPAWNER_FLAMES, null);
					player.playEffect(lz, Effect.MOBSPAWNER_FLAMES, null);
					player.playEffect(nxlb, Effect.MOBSPAWNER_FLAMES, null);
					player.playEffect(nzlb, Effect.MOBSPAWNER_FLAMES, null);
					player.playEffect(pblb, Effect.MOBSPAWNER_FLAMES, null);
					player.playEffect(nblb, Effect.MOBSPAWNER_FLAMES, null);
				}
			}
		};
		Sequence show = new Sequence(10, 0.5f, f); // Show the region for thirty seconds.
		SequenceUtilities.startSequence(show);
	}
	
	public static EstateInstance getEstateInstance(Player player, String name) {
		for(EstateInstance e : ESTATE_STATE)
			if(e.estateName.equals(name) && e.owner.equals(player.getName()))
				return e;
		return null;
	}
	
	public static boolean proposedEstateOverlap(String name, Location center, long size) {
		Rectangle proposed = new Rectangle((int)(center.getBlockX() - size), 
				(int)(center.getBlockZ() - size), (int)(size * 2) + 1, (int)(size * 2) + 1);
		for(EstateInstance e : ESTATE_STATE) {
			if(e.estateName.equals(name)) continue;
			Rectangle check = new Rectangle((int)(e.centerLocation.getBlockX() - e.size), 
					(int)(e.centerLocation.getBlockZ() - e.size), (int)(e.size * 2) + 1, (int)(e.size * 2) + 1);
			if(check.intersects(proposed)) return false;
		}
		return true;
	}
	
	public static boolean createEstateInstance(Player player, String name) {
		for(EstateInstance e : ESTATE_STATE)
			if(e.estateName.equals(name)) {
				ChatUtilities.logTo(player, "There is already an estate registered by this name! Choose another.", 
						ChatUtilities.LogType.FATAL);
				return false;
			}
		// Check overlap.
		if(!proposedEstateOverlap("", player.getLocation(), 3)) {
			ChatUtilities.logTo(player, "You cannot create an estate here because it "
					+ "would overlap with an existing estate. Try somewhere else.", 
					ChatUtilities.LogType.FATAL);
			return false;
		}
		ESTATE_STATE.add(new EstateInstance(player, name));
		writeRegister();
		return true;
	}
	
	private static void translateRegister() {
		if(!FileUtilities.fileExists("estate")) FileUtilities.createDataFile("estate");
		ESTATE_STATE.clear();
		ArrayList<String[]> data = FileUtilities.readDataFileLines("estate");
		for(String[] lineDat : data) {
			EstateInstance inst = new EstateInstance(lineDat[1], lineDat[0], 
					new Location(Bukkit.getServer().getWorld(lineDat[5]), Integer.parseInt(lineDat[2]), 
							Integer.parseInt(lineDat[3]), Integer.parseInt(lineDat[4])), Integer.parseInt(lineDat[6]));
			inst.members = new ArrayList<>();
			for(int i = 7; i < lineDat.length; i++)
				inst.members.add(lineDat[i]);
			ESTATE_STATE.add(inst);
		}
			
	}
	
	private static void writeRegister() {
		FileUtilities.clearDataFile("estate");
		for(EstateInstance e : ESTATE_STATE) {
			String data = e.estateName + " " + e.owner + " " + e.centerLocation.getBlockX() + " " 
					+ e.centerLocation.getBlockY() + " " + e.centerLocation.getBlockZ() + " " + e.centerLocation.getWorld().getName() + " " + e.size + " ";
			String trusted = "";
			for(int i = 0; i < e.members.size(); i++)
				trusted += e.members.get(i) + " ";
			FileUtilities.appendDataEntryTo("estate", data + trusted);
		}
	}
}
