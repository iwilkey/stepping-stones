package io.github.iwilkey.teknetcore.economy;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.economy.Bank.Currency;
import io.github.iwilkey.teknetcore.economy.Shop.Store.Catagory.ShopItem;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation.Page;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class Shop {
	
	public static class ShopCommand extends TeknetCoreCommand {

		public ShopCommand(Rank permissions) {
			super("shop", permissions);
			Function prices = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					if(args.length == 1) Store.showItemsForSale(sender, 1);
					else {
						int page;
						try {
							page = Integer.parseInt(args[1]);
							Store.showItemsForSale(sender, page);
						} catch(Exception e) {
							ArrayList<Integer> pp = Store.catalog.searchFor(args[1].toUpperCase());
							if(pp.size() == 0) {
								ChatUtilities.logTo(sender, 
										"TeknetCore could not find your catalog search inquiry! "
										+ ChatColor.GOLD + "Please check spelling and make sure your inquiry has no spaces or special characters.", ChatUtilities.LogType.FATAL);
							} else {
								ChatUtilities.messageTo(sender, "----------------------------------------------------", ChatColor.GRAY);
								ChatUtilities.logTo(sender, 
										"Found " + pp.size() + " result(s) for inquiry\n" + 
												ChatColor.GOLD + args[1].toUpperCase() + ChatColor.GRAY + 
												"...", ChatUtilities.LogType.SUCCESS);
								String results = "To visit pages, use [shop-catalog-<page>] with <page>: ";
								boolean toggle = false;
								int ps = 0;
								for(int i : pp) {
									results += ((toggle) ? ChatColor.BLUE : ChatColor.DARK_AQUA) + Integer.toString(i + 1);
									if(ps != pp.size() - 1) results += ChatColor.GRAY + ", ";
									toggle = !toggle;
									ps++;
								}
								ChatUtilities.messageTo(sender, results, ChatColor.GRAY);
								ChatUtilities.messageTo(sender, "----------------------------------------------------", ChatColor.GRAY);
							}
						}
					}
				}
			};
			
			Function startBuySession = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					startShopSession(sender);
				}
			};
			
			Function sell = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					// /shop sell this
					// /shop sell all
					ShopBuySession ss = getShopSessionOf(sender);
					if(ss != null) {
						ChatUtilities.logTo(sender, "You cannot use this function while in an active buy session! Done? [shop-checkout]", ChatUtilities.LogType.FATAL);
						return;
					}
					if(args[1].equals("all")) {
						Currency c = getCurrentShopSessionSubtotal(sender, true, true);
						Bank.Account a = Bank.getTeknetTrustAccount("CHECKING", sender);
						if(a == null) {
							Bank.createTeknetTrustAccount("CHECKING", sender);
							a = Bank.getTeknetTrustAccount("CHECKING", sender);
						}
						a.add(sender, c, "TC SELL POC");
						sender.getInventory().clear();
					} else if(args[1].equals("this")) {
						ItemStack holding =  sender.getInventory().getItem(sender.getInventory().getHeldItemSlot());
						if(holding == null) {
							ChatUtilities.logTo(sender, "You are not holding an item to sell!", ChatUtilities.LogType.NOTICE);
							return;
						}
						Material mat = holding.getType();
						ShopItem s = Store.getShopItem(mat.name());
						Bank.Account a = Bank.getTeknetTrustAccount("CHECKING", sender);
						if(a == null) {
							Bank.createTeknetTrustAccount("CHECKING", sender);
							a = Bank.getTeknetTrustAccount("CHECKING", sender);
						}
						a.add(sender, Bank.multiply(s.price, holding.getAmount()), "TC SELL POC");
						ChatUtilities.logTo(sender, "► You have sold " + ChatColor.GOLD + " x " 
								+ holding.getAmount() + " " + ChatColor.GOLD + mat.name() + ChatColor.GRAY + " for " 
								+ Bank.multiply(s.price, holding.getAmount()).printValueColored(), ChatUtilities.LogType.SUCCESS);
						sender.getInventory().setItem(sender.getInventory().getHeldItemSlot(), null);
					} else {
						ChatUtilities.logTo(sender, "Invalid use of [shop-sell-<this-OR-all>]", ChatUtilities.LogType.FATAL);
					}
				}
			};
			
			Function endSession = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ShopBuySession s = getShopSessionOf(sender);
					if(s == null) {
						ChatUtilities.logTo(sender, "You're not in an active shop session! Use [shop-buy] or to begin!", ChatUtilities.LogType.FATAL);
						return;
					}
					Bank.Currency total = getCurrentShopSessionSubtotal(sender, false, false);
					if(total == null) return;
					Bank.Account a = Bank.getTeknetTrustAccount("CHECKING", sender);
					if(a == null) {
						Bank.createTeknetTrustAccount("CHECKING", sender);
						a = Bank.getTeknetTrustAccount("CHECKING", sender);
					}
					ChatUtilities.messageTo(sender, 
							" ► Account chosen: " + a.name + " = " + a.amount.printValueColored(), 
							ChatColor.GRAY);
					ChatUtilities.messageTo(sender, 
							" ► Total due = " + total.printValueColored(), 
							ChatColor.GRAY);
					if(!a.subtract(sender, total, "TC BUY POC")) {
						ChatUtilities.logTo(sender, 
								" ► Payment declined! Please try a different method of payment or [shop-quit]", 
								ChatUtilities.LogType.FATAL);
						return;
					} else {

						ItemStack[] items = sender.getInventory().getContents();
						for(ItemStack i : items) {
							try {
								sender.getWorld().dropItem(sender.getLocation(), i);
							} catch(Exception e) {}
						}
					}
					stopShopSession(sender);
				}
			};
			
			Function subtotal = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					getCurrentShopSessionSubtotal(sender, true, false);
				}
			};
			
			Function quit = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					ShopBuySession s = getShopSessionOf(sender);
					if(stopShopSession(sender)) {
						sender.getInventory().setContents(s.survivalInventory);
						sender.getInventory().setArmorContents(s.survivalArmor);
					}
				}
			};
			
			Function value = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					if(args.length == 1) {
						// Value of item selected...
						ItemStack holding =  sender.getInventory().getItem(sender.getInventory().getHeldItemSlot());
						if(holding == null) {
							ChatUtilities.logTo(sender, "You are not holding an object!", ChatUtilities.LogType.NOTICE);
							return;
						}
						Material mat = holding.getType();
						ShopItem s = Store.getShopItem(mat.name());
						ChatUtilities.logTo(sender, "You are holding: " + ChatColor.GOLD + mat.name(), ChatUtilities.LogType.UTILITY);
						ChatUtilities.messageTo(sender, " ► Sell value: " + s.sellValue.printValueColored() + ChatColor.GOLD 
								+ " x " + ChatColor.WHITE + holding.getAmount() + ChatColor.GOLD
							+ " = " + Bank.multiply(s.sellValue, holding.getAmount()).printValueColored(), ChatColor.GREEN);
					} else if(args[1].equals("all")) {
						// Value of entire inventory...
						getCurrentShopSessionSubtotal(sender, true, true);
					}
				}
			};
			
			registerFunction("catalog", prices, "search", "items");
			registerFunction("buy", startBuySession, 0, "b", "start");
			registerFunction("sell", sell, 1);
			registerFunction("checkout", endSession, 0);
			registerFunction("subtotal", subtotal, 0, "sub", "tot", "s");
			registerFunction("quit", quit, 0, "q", "exit", "stop");
			registerFunction("value", value, "p", "price");
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			doc.editPage(0).write(ChatColor.GOLD + "The TeknetCore Shop is a tool for procuring and selling" + ChatColor.RESET, 1);
			doc.editPage(0).write(ChatColor.GOLD + "    every single item available in game." + ChatColor.RESET, 2);
			doc.editPage(0).write("Use [shop-catalog] to search any item's buy / sell values.", 3);
			doc.editPage(0).write("Use [shop-buy] to begin a shop buy session.", 4);
			doc.editPage(0).write("Use [shop-subtotal] to see the current amount owed.", 5);
			doc.editPage(0).write("Use [shop-checkout] to purchase the items selected.", 6);
			doc.editPage(0).write("Use [shop-quit] to exit the session without paying.", 7);
			doc.editPage(0).write(ChatColor.GRAY + "------- Next page for more commands... -------" + ChatColor.RESET, 8);
			doc.addPage(new Page());
			doc.editPage(1).write("Use [shop-sell-all] to sell all items in inventory.", 0);
			doc.editPage(1).write("Use [shop-sell-this] to sell the selected item in hotbar.", 1);
			doc.editPage(1).write("Use [shop-value] to see the value of the selected item.", 2);
			doc.editPage(1).write("[shop-subtotal] alternatives: [shop-sub] [shop-tot] [shop-s]", 3);
			doc.editPage(1).write("[shop-quit] alternatives: [shop-q] [shop-exit] [shop-stop]", 4);
			doc.editPage(1).write("[shop-buy] alternatives: [shop-b] [shop-start]", 5);
			doc.editPage(1).write("[shop-catalog] alternatives: [shop-search] [shop-items]", 6);
			doc.editPage(1).write("[shop-value] alternatives: [shop-p] [shop-price]", 7);
			doc.editPage(1).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return true;
		}
		
	}
	
	public static class ShopBuySession {
		public String playerName;
		public Location startedAt;
		public Inventory shopBasket;
		public ItemStack[] survivalInventory,
			survivalArmor;
		public ShopBuySession(Player player) {
			this.playerName = player.getName();
			startedAt = player.getLocation();
			survivalInventory = player.getInventory().getContents();
			survivalArmor = player.getInventory().getArmorContents();
			shopBasket = Bukkit.createInventory(player, InventoryType.PLAYER, "Shop Basket");
			player.getInventory().setContents(shopBasket.getContents());
			player.setGameMode(GameMode.CREATIVE);
		}
	}
	
	public static class Store {
		public final static String PATH_TO_SHOP_DATA = "shop";
		public static class Catagory {
			public static class ShopItem {
				public String materialName;
				public Catagory catagory;
				public Bank.Currency price,
					sellValue;
				public ShopItem(String materialName, Catagory catagory, Bank.Currency price, Bank.Currency sell) {
					this.materialName = materialName;
					this.catagory = catagory;
					this.price = price;
					this.sellValue = sell;
				}
			}
			public enum Value {
				MINECRAFT(3, 1),
				BUILDCRAFTCORE(1, 1),
				BUILDCRAFTTRANSPORT(2, 1),
				BUILDCRAFTSILICON(2, 1),
				BUILDCRAFTFACTORY(2, 1),
				COMPUTERCRAFT(5, 1),
				IC2(2, 1),
				FORESTRY(1, 1),
				BIGREACTORS(4, 1),
				BUILDCRAFTBUILDERS(3, 1),
				BUILDCRAFTENERGY(3, 1),
				BUILDCRAFTROBOTICS(4, 1),
				CARPENTERSBLOCKS(1, 0),
				COMPUTERCRAFTEDU(2, 0),
				RAILCRAFT(2, 1),
				COMPUTRONICS(4, 1),
				FORGEMULTIPART(1, 0),
				GENDUSTRY(3, 1),
				IMMIBISPERIPHERALS(4, 1),
				IRONCHEST(5, 1),
				JABBA(3, 1),
				NETHERORES(4, 1),
				PROJECTE(3, 2),
				QUIVERCHEVSKY(4, 2),
				REDSTONEARSENAL(3, 1),
				JAKJ_REDSTONEINMOTION(2, 1),
				SOLARFLUX(4, 1),
				TUBESTUFF(4, 1),
				ADDITIONALPIPES(4, 0),
				QMUNITYLIB(4, 1),
				BLUEPOWER(1, 1),
				CHICKENCHUNKS(9, 1),
				ENDERSTORAGE(4, 1),
				ENG_TOOLBOX(2, 1),
				IC2NUCLEARCONTROL(5, 1),
				POWERSUITS(7, 3),
				OCS(4, 1),
				POWERCONVERTERS3(4, 1),
				ZETTAINDUSTRIES(3, 1),
				LOGISTICSPIPES(2, 1),
				BUILDCRAFTCOMPAT(2, 1),
				FORGEMICROBLOCK(1, 1),
				AOBD(1, 1),
				UNKNOWN(4, 1);
				public final int value, sell;
				private Value(int value, int sell) {
					this.value = value;
					this.sell = sell;
				}
			}
			public String name;
			public Value value;
			public ArrayList<ShopItem> items;
			public Catagory(String name) {
				this.name = name;
				boolean NOT_FOUND = true;
				for(Value v : Value.values())
					if(v.name().equals(name)) {
						value = v;
						NOT_FOUND = false;
						break;
					}
				if(NOT_FOUND) value = Value.UNKNOWN;
				items = new ArrayList<>();
			}
		}
		
		private static ArrayList<Catagory> catagory;
		private static ArrayList<ShopItem> itemsForSale = new ArrayList<>();
		public Store() {
			catagory = new ArrayList<>();
			itemsForSale = new ArrayList<>();
			init();
		}
		private Catagory findCatagory(String name) {
			for(Catagory c : catagory)
				if(c.name.equals(name))
					return c;
			return null;
		}
		public static ShopItem getShopItem(String itemName) {
			for(ShopItem i : itemsForSale) 
				if(i.materialName.equals(itemName))
					return i;
			return null;
		}
		
		public static CommandDocumentation catalog = new CommandDocumentation("TeknetCore Shop Catalog");
		private void init() {
			ArrayList<String[]> lines = FileUtilities.readDataFileLines(PATH_TO_SHOP_DATA);
			Catagory cat = new Catagory("MINECRAFT");
			for(String[] line : lines) {
				if(line[0].substring(0, 2).equals("::")) {
					cat = findCatagory(line[0].substring(2, line[0].length()));
					if(cat == null) {
						catagory.add(new Catagory(line[0].substring(2, line[0].length())));
						cat = findCatagory(line[0].substring(2, line[0].length()));
					}
				} else { 
					// Minecraft items.
					if(cat.name.equals("MINECRAFT")) {
						Material mat = Material.matchMaterial(line[0]);
						String tag = "";
						if(mat.isBlock()) tag = "[BLOCK]";
						String[] iden = mat.name().split("_");
						String identity = iden[0],
								type = iden[iden.length - 1];
						switch(identity) {
							case "DIAMOND": tag = "[DIAMOND]"; break;
							case "IRON": tag = "[IRON]"; break;
							case "GOLD": tag = "[GOLD]"; break;
							case "REDSTONE": tag = "[REDSTONE]"; break;
							case "COAL": tag = "[COAL]"; break;
						}
						if(tag.equals("")) tag = "[ITEM]";
						if(mat.name().equals("ENCHANTMENT_TABLE") 
								|| mat.name().equals("BEDROCK") || mat.name().equals("TNT")) tag = "[DIAMOND]";
						int value = 0, sell = 0;
						switch(tag) {
							case "[BLOCK]": value = 1; sell = 0; break;
							case "[ITEM]": value = 1; sell = 0; break;
							case "[DIAMOND]": value = 3; sell = 2; break;
							case "[REDSTONE]": value = 1; sell = 0; break;
							case "[IRON]": value = 2; sell = 1; break;
							case "[GOLD]": value = 3; sell = 2; break;
							case "[COAL]": value = 2; sell = 1; break;
						}
						switch(type) {
							case "BLOCK": value++; sell++; break;
							case "SWORD": value++; break;
							case "PICKAXE": value++; break;
							case "AXE": value++; break;
							case "SHOVEL": value++; break;
							case "HOE": value++; break;
							case "HELMET": value++; break;
							case "CHESTPLATE": value++; break;
							case "LEGGINGS": value++; break;
							case "BOOTS": value++; break;
						}
						if(mat.name().equals("MOB_SPAWNER")) {
							value = 8; sell = 1;
						}
						cat.items.add(new ShopItem(line[0], cat, Bank.returnRandomCurrencyOfValue(value, 75.0f), 
								Bank.returnRandomCurrencyOfValue(sell, 30.0f)));
					} else {
						// Modded items.
						String[] tok = line[0].split("_");
						int value = cat.value.value + 1;
						for(String s : tok) {
							if(s.equals("COPPER") || s.equals("CABLE") 
									|| s.equals("WIRE") || s.equals("GEAR")) value = 1;
							if(s.equals("DIAMOND")) value = 3;
							if(s.equals("DM") || s.equals("ITEMPE") || s.equals("MATTER")) value = 5;
							if(s.equals("FUEL")) value = 3;
							if(tok[0].equals("PROJECTE") && s.equals("GEM")) value = 9;
						}
						cat.items.add(new ShopItem(line[0], cat, Bank.returnRandomCurrencyOfValue(value, 250.0f), 
							Bank.returnRandomCurrencyOfValue(value - 1, 200.0f)));
					}
				}
			}
			for(Catagory c : catagory) {
				for(ShopItem s : c.items) {
					itemsForSale.add(s);
				}
			}
			boolean toggle = true;
			int pages = (int)((itemsForSale.size() * 2) / 8) + 1,
					item = 0;
			catalog.editPage(0).write(ChatColor.GOLD + " ►►► Welcome to the TeknetCore Shop Catalog! ◄◄◄" + ChatColor.RESET, 0);
			catalog.editPage(0).write(" ► Use [shop-buy] or [shop-sell] to begin shopping!", 1);
			catalog.editPage(0).write(" ► To find a specific item, use the search feature!", 2);
			catalog.editPage(0).write(" ► " + ChatColor.GRAY + " Ex. [shop-catalog-diamond] " + ChatColor.GRAY + "will find all pages with", 3);
			catalog.editPage(0).write(ChatColor.GRAY + " the word or phrase " + ChatColor.GOLD + "\"DIAMOND\"" + ChatColor.GRAY + "...", 4);
			catalog.editPage(0).write(ChatColor.GRAY + " Then, simply use [shop-catalog-<page>] " + ChatColor.GRAY + "specifying one of the", 5);
			catalog.editPage(0).write(ChatColor.GRAY + " page numbers listed to find your desired buy and sell prices!", 6);
			catalog.editPage(0).write(ChatColor.RED + " Please do not use spaces or special characters in search!", 7);
			catalog.editPage(0).write("------ Search for an item! [shop-catalog-<item-name>] ------", 8);
			outter: for(int i = 1; i <= pages; i++) {
				catalog.addPage(new Page());
				for(int ii = 0; ii < 8; ii += 2) {
					if(item >= itemsForSale.size()) break outter;
					catalog.editPage(i).write(ChatColor.WHITE + " ► " + ((toggle) ? ChatColor.BLUE : ChatColor.DARK_AQUA) 
							+ itemsForSale.get(item).materialName.replace("_", " "), ii);
					catalog.editPage(i).write(ChatColor.GOLD + "    Buy: " + itemsForSale.get(item).price.printValueColored() 
							+ ChatColor.GOLD + ", Sell: " + itemsForSale.get(item).sellValue.printValueColored(), ii + 1);
					toggle = !toggle;
					item++;
				}
				catalog.editPage(i).write("----------------------------------------------------", 8);
			}
		}

		public static void showItemsForSale(Player player, int page) {
			catalog.renderPageTo(player, page - 1);
		}
	}
	
	public static ArrayList<ShopBuySession> SHOP_SESSION_STATE;
	@SuppressWarnings("unused")
	private static Store STORE;
	
	public Shop() {
		STORE = new Store();
		SHOP_SESSION_STATE = new ArrayList<>();
	}
	
	public static boolean startShopSession(Player player) {
		ShopBuySession s = getShopSessionOf(player);
		if(s != null) {
			ChatUtilities.logTo(player, "You're already in a shop session! Use [shop-checkout] to end it!", ChatUtilities.LogType.FATAL);
			return false;
		}
		SHOP_SESSION_STATE.add(new ShopBuySession(player));
		SoundUtilities.playSoundTo("CHEST_OPEN", player);
		ChatUtilities.logTo(player, "Shop session started!", ChatUtilities.LogType.SUCCESS);
		return true;
	}
	
	public static boolean stopShopSession(Player player) {
		ShopBuySession s = getShopSessionOf(player);
		if(s == null) {
			ChatUtilities.logTo(player, "You're not in an active shop session! Use [shop-buy] to begin!", ChatUtilities.LogType.FATAL);
			return false;
		}
		// Checkout function here...
		player.getInventory().setContents(s.survivalInventory);
		player.getInventory().setArmorContents(s.survivalArmor);
		player.setGameMode(GameMode.SURVIVAL);
		SHOP_SESSION_STATE.remove(s);
		SoundUtilities.playSoundTo("CHEST_CLOSE", player);
		ChatUtilities.logTo(player, "Shop session ended.", ChatUtilities.LogType.SUCCESS);
		return true;
	}
	
	public static Bank.Currency getCurrentShopSessionSubtotal(Player player, boolean verbose, boolean selling) {
		ShopBuySession s = getShopSessionOf(player);
		if(s == null && !selling) {
			ChatUtilities.logTo(player, "You are not currently in a buy session! Use [shop-buy] to begin one.", 
					ChatUtilities.LogType.FATAL);
			return null;
		}
		ItemStack[] stack = player.getInventory().getContents();
		Currency subtotal = new Currency(0);
		for(ItemStack ss : stack) {
			try {
				Material mat = ss.getType();
				ShopItem item = Store.getShopItem(mat.name());
				if(item == null) continue;
				int amount = ss.getAmount();
				Currency full = Bank.multiply(((selling) ? item.sellValue : item.price), amount);
				ChatUtilities.messageTo(player, "Item: " + mat.name() + 
						" is " + ((selling) ? item.sellValue.printValueColored() : item.price.printValueColored()) + ChatColor.GRAY + " x " + ChatColor.GOLD + amount + 
						ChatColor.GRAY + " = " + full.printValueColored(), 
						ChatColor.GRAY);
				subtotal = Bank.add(subtotal, full);
				SoundUtilities.playSoundTo("LAVA_POP", player);
			} catch (Exception e) { continue; }
		}
		if(verbose) {
			ChatUtilities.messageTo(player, ChatColor.DARK_GREEN + 
				((!selling) ? " ► Current subtotal: " : " ► Inventory sell value: ") + subtotal.printValueColored(),
				ChatColor.GRAY);
			SoundUtilities.playSoundTo("SUCCESSFUL_HIT", player);
		}
		return subtotal;
	}
	 
	public static ShopBuySession getShopSessionOf(Player player) {
		for(ShopBuySession s : SHOP_SESSION_STATE) 
			if(s.playerName.equals(player.getName()))
				return s;
		return null;
	}
}
