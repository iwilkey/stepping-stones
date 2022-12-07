package dev.iwilkey.terrafort.assets;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.gfx.SpriteSheet;
import dev.iwilkey.terrafort.particle.Particle;

public class Assets {
	
	public static Texture SIGNAL;
	
	// Environment textures
	public static TextureRegion[]
		WATER_GRASS_TRANSITION,
		GRASS,
		WATER,
		GRASS_STONE_TRANSITION,
		STONE,
		FOLIAGE,
		TREE_GRASS_TRANSITION,
		COPPER,
		SILVER,
		IRON,
		TUNGSTEN;
	
	public static TextureRegion
		SAND;
		
	// Character textures
	public static TextureRegion[]
		PLAYER;
	
	// UI
	public static TextureRegion
		SIX_BY_FOUR_MENU,
		TWO_BY_THREE_MENU,
		ONE_BY_ONE_MENU,
	
	// Items
		TEST_ITEM_TEXTURE,
		WOOD_ITEM;
	
	// Particles
	public static Particle
		SMOKE;
	
	public static void init() {
		initTextures();
	}
	
	public static ArrayList<Particle> initParticles() {
		ArrayList<Particle> particles = new ArrayList<Particle>();
		
		// Reference particles here
		particles.add(new Particle("packs/Particle Park Sparks/Particle Park Sparks.p", "sparks"));
		
		return particles;
		
	}
	
	public static void initTextures() {
		SpriteSheet ss = new SpriteSheet(new Texture("textures/spritesheet.png"));
		
		SIGNAL = new Texture("textures/signal.png");
		
		WATER_GRASS_TRANSITION = new TextureRegion[19];
			/*
			 * Index Guide
			 * 
			 * 0 - Upper Left Hand Corner
			 * 1 - Lower Left Hand Corner
			 * 2 - Upper Right Hand Corner
			 * 3 - Lower Right Hand Corner
			 * 4 - Straight Left Transition
			 * 5 - Straight Right Transition
			 * 6 - Straight Up Transition
			 * 7 - Straight Down Transition
			 * 8 - Center Left, Lower Center, Center Right
			 * 9 - Center left, Upper Center, Lower Center
			 * 10 - Center left, Upper center, Center right
			 * 11 - Upper center, Center right, Lower center
			 * 12 - Surrounded
			 * 13 - Upper left diag
			 * 14 - Lower left diag
			 * 15 - Upper right diag
			 * 16 - Lower right diag
			 * 17 - Up-Down Canal
			 * 18 - Side-side Canal
			 * 
			 */
		WATER_GRASS_TRANSITION[0] = ss.crop(10, 26);
		WATER_GRASS_TRANSITION[1] = ss.crop(10, 70);
		WATER_GRASS_TRANSITION[2] = ss.crop(54, 26);
		WATER_GRASS_TRANSITION[3] = ss.crop(54, 70);
		WATER_GRASS_TRANSITION[4] = ss.crop(8, 48);
		WATER_GRASS_TRANSITION[5] = ss.crop(56, 32);
		WATER_GRASS_TRANSITION[6] = ss.crop(32, 25);
		WATER_GRASS_TRANSITION[7] = ss.crop(32, 71);
		WATER_GRASS_TRANSITION[8] = ss.crop(96, 112);
		WATER_GRASS_TRANSITION[9] = ss.crop(96, 112 + 16);
		WATER_GRASS_TRANSITION[10] = ss.crop(96, 112 + 32);
		WATER_GRASS_TRANSITION[11] = ss.crop(96, 112 + 48);
		WATER_GRASS_TRANSITION[12] = ss.crop(112, 112);
		WATER_GRASS_TRANSITION[13] = ss.crop(112, 128);
		WATER_GRASS_TRANSITION[14] = ss.crop(112, 144);
		WATER_GRASS_TRANSITION[15] = ss.crop(128, 128);
		WATER_GRASS_TRANSITION[16] = ss.crop(128, 144);
		WATER_GRASS_TRANSITION[17] = ss.crop(128, 112);
		WATER_GRASS_TRANSITION[18] = ss.crop(144, 112);
		
		/*
		 * 
		 * Index Guide
		 * 0 - Upper Left Hand Corner
		 * 1 - Upper center
		 * 2 - Upper Right Hand Corner
		 * 3 - Center Left
		 * 4 - Center
		 * 5 - Center Right
		 * 6 - Lower Left Hand Corner
		 * 7 - Lower Center
		 * 8 - Lower Right Hand Corner
		 * 9 - Alone
		 * 
		 */
		TREE_GRASS_TRANSITION = new TextureRegion[10];
		TREE_GRASS_TRANSITION[0] = ss.crop(192, 144);
		TREE_GRASS_TRANSITION[1] = ss.crop(208, 144);
		TREE_GRASS_TRANSITION[2] = ss.crop(208 + 16, 144);
		TREE_GRASS_TRANSITION[3] = ss.crop(192, 144 + 16);
		TREE_GRASS_TRANSITION[4] = ss.crop(208, 144 + 16);
		TREE_GRASS_TRANSITION[5] = ss.crop(208 + 16, 144 + 16);
		TREE_GRASS_TRANSITION[6] = ss.crop(192, 144 + 16 + 16);
		TREE_GRASS_TRANSITION[7] = ss.crop(208, 144 + 16 + 16);
		TREE_GRASS_TRANSITION[8] = ss.crop(208 + 16, 144 + 16 + 16);
		TREE_GRASS_TRANSITION[9] = ss.crop(160, 80);
		
		GRASS = new TextureRegion[9];
		GRASS[0] = ss.crop(96, 32);
		GRASS[1] = ss.crop(32, 32);
		GRASS[2] = ss.crop(48, 32);
		GRASS[3] = ss.crop(16, 48);
		GRASS[4] = ss.crop(32, 48);
		GRASS[5] = ss.crop(48, 48);
		GRASS[6] = ss.crop(16, 64);
		GRASS[7] = ss.crop(32, 64);
		GRASS[8] = ss.crop(48, 64);
		
		SAND = ss.crop(16, 0);
		
		WATER = new TextureRegion[10];
		WATER[0] = ss.crop(0, 96);
		WATER[1] = ss.crop(16, 96);
		WATER[2] = ss.crop(32, 96);
		WATER[3] = ss.crop(48, 96);
		WATER[4] = ss.crop(64, 96);
		WATER[5] = ss.crop(0, 112);
		WATER[6] = ss.crop((16 * 1), 112);
		WATER[7] = ss.crop((16 * 2), 112);
		WATER[8] = ss.crop((16 * 3), 112);
		WATER[9] = ss.crop((16 * 4), 112);
		
		GRASS_STONE_TRANSITION = new TextureRegion[19];
			/*
			 * Index Guide
			 * 
			 * 0 - Upper Left Hand Corner
			 * 1 - Lower Left Hand Corner
			 * 2 - Upper Right Hand Corner
			 * 3 - Lower Right Hand Corner
			 * 4 - Straight Left Transition
			 * 5 - Straight Right Transition
			 * 6 - Straight Up Transition
			 * 7 - Straight Down Transition
			 * 8 - Center Left, Lower Center, Center Right
			 * 9 - Center left, Upper Center, Lower Center
			 * 10 - Center left, Upper center, Center right
			 * 11 - Upper center, Center right, Lower center
			 * 12 - Surrounded
			 * 13 - Upper left diag
			 * 14 - Lower left diag
			 * 15 - Upper right diag
			 * 16 - Lower right diag
			 * 17 - Up-Down Canal
			 * 18 - Side-side Canal
			 * 
			 */
		GRASS_STONE_TRANSITION[0] = ss.crop(86, 22);
		GRASS_STONE_TRANSITION[1] = ss.crop(86, 74);
		GRASS_STONE_TRANSITION[2] = ss.crop(138, 22);
		GRASS_STONE_TRANSITION[3] = ss.crop(138, 74);
		GRASS_STONE_TRANSITION[4] = ss.crop(80, 32);
		GRASS_STONE_TRANSITION[5] = ss.crop(144, 32);
		GRASS_STONE_TRANSITION[6] = ss.crop(108, 22);
		GRASS_STONE_TRANSITION[7] = ss.crop(108, 74);
		GRASS_STONE_TRANSITION[8] = ss.crop(144, 128);
		GRASS_STONE_TRANSITION[9] = ss.crop(144, 128 + 16);
		GRASS_STONE_TRANSITION[10] = ss.crop(144, 128 + 32);
		GRASS_STONE_TRANSITION[11] = ss.crop(144, 128 + 48);
		GRASS_STONE_TRANSITION[12] = ss.crop(160, 128);
		GRASS_STONE_TRANSITION[13] = ss.crop(112, 160);
		GRASS_STONE_TRANSITION[14] = ss.crop(112, 160 + 16);
		GRASS_STONE_TRANSITION[15] = ss.crop(112 + 16, 160);
		GRASS_STONE_TRANSITION[16] = ss.crop(112 + 16, 160 + 16);
		GRASS_STONE_TRANSITION[17] = ss.crop(160, 144);
		GRASS_STONE_TRANSITION[18] = ss.crop(160, 144 + 16);
		
		STONE = new TextureRegion[5];
		STONE[0] = ss.crop(0 + 80, 96);
		STONE[1] = ss.crop(176, 112);
		STONE[2] = ss.crop(176 + 16, 112);
		STONE[3] = ss.crop(176 + 16 + 16, 112);
		STONE[4] = ss.crop(176 + 16 + 16 + 16, 112);
		
		FOLIAGE = new TextureRegion[10];
		FOLIAGE[0] = ss.crop(176, 80);
		FOLIAGE[1] = ss.crop(176 + 16, 80);
		FOLIAGE[2] = ss.crop(176 + (16 * 2), 80);
		FOLIAGE[3] = ss.crop(176 + (16 * 3), 80);
		FOLIAGE[4] = ss.crop(176, 80 + 16);
		FOLIAGE[5] = ss.crop(176 + 16, 80 + 16);
		FOLIAGE[6] = ss.crop(176 + (16 * 2), 80 + 16);
		FOLIAGE[7] = ss.crop(176 + (16 * 3), 80 + 16);
		FOLIAGE[8] = ss.crop(160, 96);
		FOLIAGE[9] = ss.crop(160, 96 + 16);
		
		// Ores
		COPPER = new TextureRegion[4];
		COPPER[0] = ss.crop(160, 16);
		COPPER[1] = ss.crop(160, 16 + (16 * 1));
		COPPER[2] = ss.crop(160, 16 + (16 * 2));
		COPPER[3] = ss.crop(160, 16 + (16 * 3));
		SILVER = new TextureRegion[4];
		SILVER[0] = ss.crop(160 + (16 * 1), 16);
		SILVER[1] = ss.crop(160 + (16 * 1), 16 + (16 * 1));
		SILVER[2] = ss.crop(160 + (16 * 1), 16 + (16 * 2));
		SILVER[3] = ss.crop(160 + (16 * 1), 16 + (16 * 3));
		IRON = new TextureRegion[4];
		IRON[0] = ss.crop(160 + (16 * 2), 16);
		IRON[1] = ss.crop(160 + (16 * 2), 16 + (16 * 1));
		IRON[2] = ss.crop(160 + (16 * 2), 16 + (16 * 2));
		IRON[3] = ss.crop(160 + (16 * 2), 16 + (16 * 3));
		TUNGSTEN = new TextureRegion[4];
		TUNGSTEN[0] = ss.crop(160 + (16 * 3), 16);
		TUNGSTEN[1] = ss.crop(160 + (16 * 3), 16 + (16 * 1));
		TUNGSTEN[2] = ss.crop(160 + (16 * 3), 16 + (16 * 2));
		TUNGSTEN[3] = ss.crop(160 + (16 * 3), 16 + (16 * 3));
		 
		// Character textures
		PLAYER = new TextureRegion[12];
		/*
		 * Index Guide
		 * 
		 * 0 - Idle right
		 * 1 - Right walk frame 1
		 * 2 - Right walk frame 2
		 * 3 - Idle front
		 * 4 - Front walk frame 1
		 * 5 - Front walk frame 2
		 * 6 - Idle left
		 * 7 - Left walk frame 1
		 * 8 - Left walk frame 2
		 * 9 - Idle back
		 * 10 - Back walk frame 1
		 * 11 - Back walk frame 2
		 * 
		 */
		PLAYER[0] = ss.crop(0, 144, 22, 22);
		PLAYER[1] = ss.crop(48, 144, 22, 22);
		PLAYER[2] = ss.crop(48, 192, 22, 22);
		PLAYER[3] = ss.crop(0 + 22, 144, 22, 22);
		PLAYER[4] = ss.crop(48 + 22, 144, 22, 22);
		PLAYER[5] = ss.crop(48 + 22, 192, 22, 22);
		PLAYER[6] = ss.crop(0 + 22, 144, 22, 22);
		PLAYER[7] = ss.crop(48 + 22, 144, 22, 22);
		PLAYER[8] = ss.crop(48 + 22, 192, 22, 22);
		PLAYER[9] = ss.crop(0, 144, 22, 22);
		PLAYER[10] = ss.crop(48, 144, 22, 22);
		PLAYER[11] = ss.crop(48, 192, 22, 22);
		
		// UI
		SpriteSheet ui = new SpriteSheet(new Texture("textures/ui-spritesheet.png"));
		
		SIX_BY_FOUR_MENU = ui.crop(32, 32, 96, 64);
		TWO_BY_THREE_MENU = ui.crop(32, 112, 32, 48);
		ONE_BY_ONE_MENU = ui.crop(64, 128, 32, 32);
		
		// Item Textures
		TEST_ITEM_TEXTURE = ui.crop(0, 0);
		WOOD_ITEM = ui.crop(16, 0);

	}

}
