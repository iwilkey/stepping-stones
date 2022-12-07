package dev.iwilkey.terrafort.tile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.clock.SequencedEvent;
import dev.iwilkey.terrafort.clock.ThreadRequest;

public enum Tile {
	
	WATER (Assets.WATER[0], TileType.WATER, 0, false),
	SAND  (Assets.SAND, TileType.SAND, 1, false),
	GRASS (Assets.GRASS[0], TileType.GRASS, 2, false),
	STONE (Assets.STONE[0], TileType.STONE, 3, true),
	TREE_1 (Assets.TREE_GRASS_TRANSITION[0], TileType.TREE, 4, false),
	TREE_2 (Assets.TREE_GRASS_TRANSITION[1], TileType.TREE, 5, false),
	TREE_3 (Assets.TREE_GRASS_TRANSITION[2], TileType.TREE, 6, false),
	TREE_4 (Assets.TREE_GRASS_TRANSITION[3], TileType.TREE, 7, false),
	TREE_5 (Assets.TREE_GRASS_TRANSITION[4], TileType.TREE, 8, false),
	TREE_6 (Assets.TREE_GRASS_TRANSITION[5], TileType.TREE, 9, false),
	TREE_7 (Assets.TREE_GRASS_TRANSITION[6], TileType.TREE, 10, false),
	TREE_8 (Assets.TREE_GRASS_TRANSITION[7], TileType.TREE, 11, false),
	TREE_9 (Assets.TREE_GRASS_TRANSITION[8], TileType.TREE, 12, false),
	FOLIAGE_1 (Assets.FOLIAGE[0], TileType.FOLIAGE, 13, false),
	FOLIAGE_2 (Assets.FOLIAGE[1], TileType.FOLIAGE, 14, false),
	FOLIAGE_3 (Assets.FOLIAGE[2], TileType.FOLIAGE, 15, false),
	FOLIAGE_4 (Assets.FOLIAGE[3], TileType.FOLIAGE, 16, false),
	FOLIAGE_5 (Assets.FOLIAGE[4], TileType.FOLIAGE, 17, false),
	FOLIAGE_6 (Assets.FOLIAGE[5], TileType.FOLIAGE, 18, false),
	FOLIAGE_7 (Assets.FOLIAGE[6], TileType.FOLIAGE, 19, false),
	FOLIAGE_8 (Assets.FOLIAGE[7], TileType.FOLIAGE, 20, false),
	FOLIAGE_9 (Assets.FOLIAGE[8], TileType.FOLIAGE, 21, false),
	FOLIAGE_10 (Assets.FOLIAGE[9],TileType.FOLIAGE, 22, false),
	COPPER_1 (Assets.COPPER[0], TileType.COPPER, 23, false),
	COPPER_2 (Assets.COPPER[1], TileType.COPPER, 24, false),
	COPPER_3 (Assets.COPPER[2], TileType.COPPER, 25, false),
	COPPER_4 (Assets.COPPER[3], TileType.COPPER, 26, false),
	SILVER_1 (Assets.SILVER[0], TileType.SILVER, 27, false),
	SILVER_2 (Assets.SILVER[1], TileType.SILVER, 28, false),
	SILVER_3 (Assets.SILVER[2], TileType.SILVER, 29, false),
	SILVER_4 (Assets.SILVER[3], TileType.SILVER, 30, false),
	IRON_1 (Assets.IRON[0], TileType.IRON, 31, false),
	IRON_2 (Assets.IRON[1], TileType.IRON, 32, false),
	IRON_3 (Assets.IRON[2], TileType.IRON, 33, false),
	IRON_4 (Assets.IRON[3], TileType.IRON, 34, false),
	TUNGSTEN_1 (Assets.TUNGSTEN[0], TileType.TUNGSTEN, 35, false),
	TUNGSTEN_2 (Assets.TUNGSTEN[1], TileType.TUNGSTEN, 36, false),
	TUNGSTEN_3 (Assets.TUNGSTEN[2], TileType.TUNGSTEN, 37, false),
	TUNGSTEN_4 (Assets.TUNGSTEN[3], TileType.TUNGSTEN, 38, false),
	STONE_2 (Assets.STONE[1], TileType.STONE, 39, true),
	STONE_3 (Assets.STONE[2], TileType.STONE, 40, true),
	STONE_4 (Assets.STONE[3], TileType.STONE, 41, true),
	STONE_5 (Assets.STONE[4], TileType.STONE, 42, true),
	WATER_2 (Assets.WATER[5], TileType.WATER, 43, false),
	WATER_3 (Assets.WATER[6], TileType.WATER, 44, false),
	WATER_4 (Assets.WATER[7], TileType.WATER, 45, false),
	WATER_5 (Assets.WATER[8], TileType.WATER, 46, false),
	WATER_6 (Assets.WATER[9], TileType.WATER, 47, false);
	
	public static final int TILE_SIZE = 32;
	public TextureRegion WATER_TEXTURE;
	
	TileType type;
	int ID;
	TextureRegion texture;
	boolean isSolid;
	
	private Tile(TextureRegion texture, TileType type, int ID, boolean isSolid) {
		
		this.type = type;
		this.ID = ID;
		this.isSolid = isSolid;
		
		// Texture
		this.texture = texture;
		if(name() == "WATER") {
			WATER_TEXTURE = texture;
			
			new ThreadRequest(new SequencedEvent() {
				@Override
				public void onStart() {}
				@Override
				public void onLoop() {
					WATER_TEXTURE = Assets.WATER[MathUtils.random(0, 4)];
				}
				@Override
				public void onKill() {}
			}, 100);
			
		}
	}
	
	public int getID() { return ID; }
	
	public boolean isSolid() { return isSolid; }
	
	public TileType getType() { return type; }
	
	public void tick() {}
	
	public void render(byte[] a, Batch b, int x, int y) {
		if(name() != "WATER") b.draw(texture, x, y, TILE_SIZE, TILE_SIZE);
		else b.draw(WATER_TEXTURE, x, y, TILE_SIZE, TILE_SIZE);
		
		
		/*
		 * Surrounding Indexes
		 * 
		 * 0 - Upper right hand
		 * 1 - Upper center
		 * 2 - Upper left hand
		 * 3 - Center left
		 * 4 - Center right
		 * 5 - Lower right hand
		 * 6 - Lower center
		 * 7 - Lower left hand
		 * 
		 */
		
		// Draw Grass -> Water transitions
		if(name() == "SAND") {
			
			if(a[1] == WATER.getID() && a[3] == WATER.getID()) 
				b.draw(Assets.WATER_GRASS_TRANSITION[0], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[3] == WATER.getID() && a[6] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[1], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[1] == WATER.getID() && a[4] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[2], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[6] == WATER.getID() && a[4] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[3], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[3] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[4], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[4] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[5], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[1] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[6], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[6] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[7], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[7] == WATER.getID()) 
				b.draw(Assets.WATER_GRASS_TRANSITION[13], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[2] == WATER.getID()) 
				b.draw(Assets.WATER_GRASS_TRANSITION[14], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[5] == WATER.getID()) 
				b.draw(Assets.WATER_GRASS_TRANSITION[15], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[0] == WATER.getID()) 
				b.draw(Assets.WATER_GRASS_TRANSITION[16], x, y, TILE_SIZE, TILE_SIZE);
			if(a[1] == WATER.getID() && a[6] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[17], x, y, TILE_SIZE, TILE_SIZE);
			if(a[3] == WATER.getID() && a[4] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[18], x, y, TILE_SIZE, TILE_SIZE);
			
			if(a[3] == WATER.getID() && a[6] == WATER.getID() && a[4] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[8], x, y, TILE_SIZE, TILE_SIZE);
			if(a[3] == WATER.getID() && a[1] == WATER.getID() && a[6] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[9], x, y, TILE_SIZE, TILE_SIZE);
			if(a[3] == WATER.getID() && a[1] == WATER.getID() && a[4] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[10], x, y, TILE_SIZE, TILE_SIZE);
			if(a[1] == WATER.getID() && a[4] == WATER.getID() && a[6] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[11], x, y, TILE_SIZE, TILE_SIZE);
			if(a[1] == WATER.getID() && a[4] == WATER.getID() && a[6] == WATER.getID() 
					&& a[3] == WATER.getID())
				b.draw(Assets.WATER_GRASS_TRANSITION[12], x, y, TILE_SIZE, TILE_SIZE);
			
			// GRASS -> STONE transition
			
		}
		
		if(name() == "GRASS") {
			if(a[1] == STONE.getID() && a[3] == STONE.getID()) 
				b.draw(Assets.GRASS_STONE_TRANSITION[0], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[3] == STONE.getID() && a[6] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[1], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[1] == STONE.getID() && a[4] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[2], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[6] == STONE.getID() && a[4] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[3], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[3] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[4], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[4] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[5], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[1] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[6], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[6] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[7], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[7] == STONE.getID()) 
				b.draw(Assets.GRASS_STONE_TRANSITION[13], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[2] == STONE.getID()) 
				b.draw(Assets.GRASS_STONE_TRANSITION[14], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[5] == STONE.getID()) 
				b.draw(Assets.GRASS_STONE_TRANSITION[15], x, y, TILE_SIZE, TILE_SIZE);
			else if(a[0] == STONE.getID()) 
				b.draw(Assets.GRASS_STONE_TRANSITION[16], x, y, TILE_SIZE, TILE_SIZE);
			if(a[1] == STONE.getID() && a[6] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[17], x, y, TILE_SIZE, TILE_SIZE);
			if(a[3] == STONE.getID() && a[4] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[18], x, y, TILE_SIZE, TILE_SIZE);
			
			if(a[3] == STONE.getID() && a[6] == STONE.getID() && a[4] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[8], x, y, TILE_SIZE, TILE_SIZE);
			if(a[3] == STONE.getID() && a[1] == STONE.getID() && a[6] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[9], x, y, TILE_SIZE, TILE_SIZE);
			if(a[3] == STONE.getID() && a[1] == STONE.getID() && a[4] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[10], x, y, TILE_SIZE, TILE_SIZE);
			if(a[1] == STONE.getID() && a[4] == STONE.getID() && a[6] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[11], x, y, TILE_SIZE, TILE_SIZE);
			if(a[1] == STONE.getID() && a[4] == STONE.getID() && a[6] == STONE.getID() 
					&& a[3] == STONE.getID())
				b.draw(Assets.GRASS_STONE_TRANSITION[12], x, y, TILE_SIZE, TILE_SIZE);
		}
	}
}
