package dev.iwilkey.terrafort.world;

import dev.iwilkey.terrafort.scene.WorldScene;
import dev.iwilkey.terrafort.tile.Tile;

public abstract class Space {
	public static int SIZE;
	public WorldScene scene;
	public byte[][] TILES;
	
	public Space(WorldScene scene, int size) {
		SIZE = size;
		this.scene = scene;
	}
	
	public Space(int size) {
		SIZE = size;
		this.scene = null; // (ONLY FOR ABSTRACT)
	}
	// Renderer 
	public int xStart, xEnd, yStart, yEnd;
	
	public byte tileIDAt(int x, int y) {
		if(this.TILES == null) return -1;
		return TILES[x][y];
	}
	
	public Tile tileAt(int x, int y) {
		if(this.TILES == null) return null;
		for(Tile t : Tile.values()) 
			if(t.getID() == TILES[x][y]) return t;
		return null;
	}
	
	public boolean isSolidAtPixel(int x, int y) {
		if(this.TILES == null) return false;
		for(Tile t : Tile.values()) 
			if(TILES[x / Tile.TILE_SIZE][y / Tile.TILE_SIZE] == t.getID())
				return t.isSolid();
		return false;
	}
}
