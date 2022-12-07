package io.github.iwilkey.teknetcore.economy;

import java.math.BigInteger;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCoreCommand;
import io.github.iwilkey.teknetcore.ranks.Ranks.Rank;
import io.github.iwilkey.teknetcore.utils.ChatUtilities;
import io.github.iwilkey.teknetcore.utils.FileUtilities;
import io.github.iwilkey.teknetcore.utils.ChatUtilities.CommandDocumentation;
import io.github.iwilkey.teknetcore.utils.MathUtilities;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class Bank {
	
	public static class Currency {
		
		private BigInteger amount;
		
		public Currency(BigInteger amount) {
			this.amount = amount;
		}
		
		public Currency(long amount) {
			this.amount = BigInteger.valueOf(amount);
		}
		
		public BigInteger getDollars() {
	        return amount.divide(BigInteger.valueOf(100));
	    }

	    public BigInteger getCents() {
	    	return amount.mod(BigInteger.valueOf(100));
	    }
	    
	    public BigInteger get() {
	    	return amount;
	    }
		
		public String printValueColored() {
			String dol = getDollars().toString();
			if(getDollars().compareTo(BigInteger.valueOf(1000)) >= 0) dol = dollarCommas();
			return ChatColor.GREEN + "$" + dol + "." + displayCents() + ChatColor.RESET;
		}
		public String printValue() {
			String dol = getDollars().toString();
			if(getDollars().compareTo(BigInteger.valueOf(1000)) >= 0) dol = dollarCommas();
			return "$" + dol + "." + displayCents();
		}
		private String displayCents() {
			if(getCents().compareTo(BigInteger.valueOf(9)) <= 0) return "0" + getCents().toString();
			return getCents().toString();
		}
		private String dollarCommas() {
			String dol = getDollars().toString();
			StringBuffer str = new StringBuffer(dol);
			int c = 0;
			for(int i = dol.length() - 1; i >= 0; i--) {
				c++;
				if(c % 3 == 0) {
					if(i == 0) break;
					str.insert(i, ',');
					c = 0;
				}
			}
			return str.toString();
		}
	}
	
	public static Currency returnRandomCurrencyOfValue(int value, float variance) {
		// Base = 10 ^ value
		// Variance += random in range(0, variance)%
		long base = (long)Math.pow(10, value + 1);
		double var = MathUtilities.randomDoubleBetween(0, variance);
		base += (base * (var / 100.0f));
		return new Currency(BigInteger.valueOf(base));
	}
	
	public static Currency add(Currency one, Currency two) {
		return new Currency(one.get().add(two.get()));
	}
	
	public static Currency subtract(Currency one, Currency two) {
		return new Currency(one.get().subtract(two.get()));
	}
	
	public static Currency multiply(Currency one, long scalar) {
		return new Currency(one.get().multiply(BigInteger.valueOf(scalar)));
	}
	
	// TeknetTrust...
	
	private static ArrayList<Account> TEKNET_TRUST_STATE;
	
	public static class BankCommand extends TeknetCoreCommand {

		public BankCommand(Rank permissions) {
			super("bank", permissions);
			
			Function seeAccount = new Function() {
				@Override
				public void func(Player sender, String[] args) {
					Account a = null;
					if(args.length == 1) {
						a = getTeknetTrustAccount("CHECKING", sender);
						if(a == null) {
							createTeknetTrustAccount("CHECKING", sender);
							a = getTeknetTrustAccount("CHECKING", sender);
						}
					} else {
						a = getTeknetTrustAccount(args[1], sender);
						if(a == null) {
							ChatUtilities.logTo(sender, "The multiple account feature is still under construction.", ChatUtilities.LogType.FATAL);
							return;
						}
					}
					a.printStatus(sender);
				};
				
			};
			
			registerFunction("account", seeAccount);
		}

		@Override
		protected void documentation(CommandDocumentation doc) {
			doc.editPage(0).write(ChatColor.GOLD + "The TeknetCore Bank is a tool for managing " + ChatColor.RESET, 1);
			doc.editPage(0).write(ChatColor.GOLD + "    your funds available in game." + ChatColor.RESET, 2);
			doc.editPage(0).write("Use [bank-help] to see this manual again.", 3);
			doc.editPage(0).write("Use [bank-account] to see your checking account balance.", 4);
			doc.editPage(0).write("", 5);
			doc.editPage(0).write("", 6);
			doc.editPage(0).write("", 7);
			doc.editPage(0).write(ChatColor.GRAY + "------- End of TeknetCore manual. -------" + ChatColor.RESET, 8);
		}

		@Override
		public boolean logic(Player sender, Command command, String label, String[] args) {
			return true;
		}
		
	}
	
	public Bank() {
		TEKNET_TRUST_STATE = new ArrayList<>();
		translateRegister();
	}
	
	public static class Account {
		public String name,
			playerName;
		public Currency amount;
		public Account(String name, String playerName) {
			this.playerName = playerName;
			this.name = name;
			amount = new Currency(0L);
		}
		public Account(String name, String playerName, Currency amount) {
			this.playerName = playerName;
			this.name = name;
			this.amount = amount;
		}
		public void printStatus(Player player) {
			ChatUtilities.tagAndMessageTo(player, "TeknetTrust", 
					"► " + ChatColor.GOLD + name + ": " + amount.printValueColored(),
					ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GRAY);
			SoundUtilities.playSoundTo("DIG_WOOL", player);
		}
		
		public boolean add(Player player, Currency second, String reason) {
			amount = Bank.add(amount, second);
			if(second.get() == BigInteger.valueOf(0)) return false;
			ChatUtilities.tagAndMessageTo(player, "TeknetTrust", 
					"" + ChatColor.GREEN + " +++$ " + ChatColor.GOLD + "" 
			+ ChatColor.GOLD + name + ChatColor.GOLD + " for " + ChatColor.DARK_AQUA + reason, 
					ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GOLD);
			writeRegister();
			SoundUtilities.playSoundTo("LEVEL_UP", player);
			return true;
		}
		
		public boolean subtract(Player player, Currency second, String reason) {
			Currency buffer = Bank.subtract(amount, second);
			if(second.get() == BigInteger.valueOf(0)) return true;
			if(buffer.get().compareTo(BigInteger.valueOf(0)) < 0) return false;
			amount = buffer;
			ChatUtilities.tagAndMessageTo(player, "TeknetTrust", 
					"" + ChatColor.DARK_RED + " ---$ " + ChatColor.GOLD + "" 
			+ ChatColor.GOLD + name + ChatColor.GOLD + " for " + ChatColor.DARK_AQUA + reason, 
					ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.GOLD);
			writeRegister();
			SoundUtilities.playSoundTo("NOTE_BASS_GUITAR", player);
			return true;
		}
		
	}
	
	public static Account getTeknetTrustAccount(String name, Player player) {
		for(Account a : TEKNET_TRUST_STATE)
			if(a.name.equals(name) && a.playerName.equals(player.getName()))
				return a;
		return null;
	}
	
	public static boolean createTeknetTrustAccount(String name, Player player) {
		for(Account a : TEKNET_TRUST_STATE)
			if(a.name.equals(name) && a.playerName.equals(player.getName()))
				return false;
		TEKNET_TRUST_STATE.add(new Account(name, player.getName()));
		return true;
	}
	
	private static void translateRegister() {
		if(!FileUtilities.fileExists("trust")) FileUtilities.createDataFile("trust");
		TEKNET_TRUST_STATE.clear();
		ArrayList<String[]> data = FileUtilities.readDataFileLines("trust");
		for(String[] lineDat : data)
			TEKNET_TRUST_STATE.add(new Account(lineDat[2], lineDat[0], new Currency(new BigInteger(lineDat[1], 10))));
	}
	
	private static void writeRegister() {
		FileUtilities.clearDataFile("trust");
		for(Account a : TEKNET_TRUST_STATE) {
			String data = a.playerName + " " + a.amount.get().toString(10) + " " + a.name;
			FileUtilities.appendDataEntryTo("trust", data);
		}
	}
}
