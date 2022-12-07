package io.github.iwilkey.teknetcore.cooldown;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import io.github.iwilkey.teknetcore.TeknetCore;
import io.github.iwilkey.teknetcore.cooldown.Cooldown.PlayerServerInteractionActivity.ActivityEntry;
import io.github.iwilkey.teknetcore.utils.SoundUtilities;

public class Cooldown {
	
	private static final float COOLDOWN_RESET = 45.0f; // The cooldown resets every...
	private static final int COOLDOWN_MAX_REQUESTS = 10;
	
	// Players have a command log with time since they last invoked or chatted.
	public static class PlayerServerInteractionActivity {
		public static class ActivityEntry {
			public String sent;
			public int ticksSince;
			public float secondsSince;
			public ActivityEntry(String sent) {
				this.sent = sent;
				this.secondsSince = 0;
			}
		}
		public String name;
		public  ArrayList<ActivityEntry> activity;
		public PlayerServerInteractionActivity(String name) {
			this.name = name;
			activity = new ArrayList<>();
		}
	}	
	
	public static ArrayList<PlayerServerInteractionActivity> log;
	private static ArrayList<ActivityEntry> toDelete;
	
	public Cooldown() {
		log = new ArrayList<>();
		toDelete = new ArrayList<>();
	}
	
	public static void registerActivity(Player player, String sent) {
		for(PlayerServerInteractionActivity a : log)
			if(a.name.equals(player.getName())) {
				a.activity.add(new ActivityEntry(sent));
				return;
			}
		log.add(new PlayerServerInteractionActivity(player.getName()));
		registerActivity(player, sent);
	}
	
	public static void tick() {
		if(log.size() == 0) return;
		for(PlayerServerInteractionActivity entry : log) {
			for(ActivityEntry act : entry.activity) {
				act.ticksSince++;
				act.secondsSince = (1.0f / TeknetCore.SERVER_TPS) * act.ticksSince;
				if(act.secondsSince >= COOLDOWN_RESET) 
					toDelete.add(act);
			}
			for(ActivityEntry ent : toDelete)
				entry.activity.remove(ent);
			toDelete.clear();
		}
	}
	
	public static boolean can(Player player) {
		for(PlayerServerInteractionActivity entry : log) 
			if(entry.name.equals(player.getName()))
				if(entry.activity.size() > COOLDOWN_MAX_REQUESTS) {
					SoundUtilities.playSoundTo("CLICK", player);
					return false;
				}
				else return true;
		return true;
	}
	
	// A player that has sent too many requests, this calculates the minimum time till they can chat / command again.
	public static float timeTillReset(Player player) {
		for(PlayerServerInteractionActivity entry : log) 
			if(entry.name.equals(player.getName()))
				return COOLDOWN_RESET - entry.activity.get(entry.activity.size() - 1).secondsSince;
		return 0.0f;
	}
}
