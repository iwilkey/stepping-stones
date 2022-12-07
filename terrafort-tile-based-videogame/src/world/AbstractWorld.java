package dev.iwilkey.terrafort.world;

import java.io.Serializable;
import java.util.ArrayList;

import dev.iwilkey.terrafort.entity.creature.AbstractPlayer;

// For server
public class AbstractWorld extends Space implements Serializable {
	private static final long serialVersionUID = 1L;

	public ArrayList<AbstractPlayer> players;
	
	public AbstractWorld(int size) {
		super(size);
		players = new ArrayList<>();
		TILES = new byte[size][size];
	}
	
	public boolean addPlayer(AbstractPlayer player) {
		
		for(AbstractPlayer p : players) 
			if(p == player) return false;
			
		players.add(player);
		return true;
	}
	
	public boolean removePlayer(int ID) {
		for(AbstractPlayer p : players) 
			if(p.ID == ID) {
				players.remove(p);
				return true;
			}
		return false;
	}

}
