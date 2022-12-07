package dev.iwilkey.terrafort.world;

import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.math.PerlinNoise;

public class WorldGeneration {
	
	public final static int OCTAVE_COUNT = 7,
			TREE_OCTAVE_COUNT = 6, // How frequent should the terrain change? (Low = most)
			FOLIAGE_OCTAVE_COUNT = 4,
			ORE_OCTAVE_COUNT = 8;
	public final static float PERSISTANCE = 0.45f,
			TREE_PERSISTANCE_COUNT = 0.45f, // How dull or sharp should the changes be? (High = sharp)
			FOLIAGE_PERSISTANCE_COUNT = 0.45f,
			ORE_PERSISTANCE_COUNT = 0.45f,
			TREE_LIKELYHOOD = 0.65f, // How likely should it be that trees spawn? [BETWEEN 1 and 0!] (Low = HIGH)
			FOLIAGE_LIKELYHOOD = 0.50f;

	public static byte[][] GenerateWorld(byte[][] tiles, int width, int height, long seed) {
		float[][] noise = PerlinNoise.generatePerlinNoise(width, height, OCTAVE_COUNT, PERSISTANCE, seed);
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				float val = noise[x][y];
				
				if(val >= 0.0f && val < 0.16f) tiles[x][y] = (byte)47;
				else if(val >= 0.16f && val < 0.20f) tiles[x][y] = (byte)46;
				else if(val >= 0.20f && val < 0.24f) tiles[x][y] = (byte)45;
				else if(val >= 0.24f && val < 0.28f) tiles[x][y] = (byte)44;
				else if(val >= 0.28f && val < 0.32f) tiles[x][y] = (byte)43;
				else if(val >= 0.32f &&	val	< 0.37f) tiles[x][y] = (byte)0;
				else if(val >= 0.37f && val < 0.42f) tiles[x][y] = (byte)1;
				else if(val >= 0.42f && val < 0.65f) tiles[x][y] = (byte)2;
				else if(val >= 0.65f && val < 0.70f) tiles[x][y] = (byte)3;
				else if(val >= 0.70f && val < 0.75f) tiles[x][y] = (byte)39;
				else if(val >= 0.75f && val < 0.80f) tiles[x][y] = (byte)40;
				else if(val >= 0.80f && val < 0.85f) tiles[x][y] = (byte)41;
				else tiles[x][y] = (byte)42;
				
			}
		}
		
		float[][] treeNoise = PerlinNoise.generatePerlinNoise(width, height, 
				TREE_OCTAVE_COUNT, TREE_PERSISTANCE_COUNT, MathUtils.random(1000000, 100000000) + seed);
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				float val = treeNoise[x][y];
				if(tiles[x][y] == (byte)2) {
					
					if(x + 1 < width) if(tiles[x + 1][y] == 3 || tiles[x + 1][y] == 1 || (tiles[x + 1][y] <= 4 && tiles[x + 1][y] >= 12)) continue;
					if(x - 1 > 0) if(tiles[x - 1][y] == 3 || tiles[x - 1][y] == 1 || (tiles[x - 1][y] <= 4 && tiles[x - 1][y] >= 12)) continue;
					if(y - 1 > 0) if(tiles[x][y - 1] == 3 || tiles[x][y - 1] == 1 || (tiles[x][y - 1] <= 4 && tiles[x][y - 1] >= 12)) continue;
					if(y + 1 < height) if(tiles[x][y + 1] == 3 || tiles[x][y + 1] == 1 || (tiles[x][y + 1] <= 4 && tiles[x][y + 1] >= 12)) continue;

					if(val >= TREE_LIKELYHOOD) tiles[x][y] = (byte)MathUtils.random(4, 12);
				}
			}
		}
		
		float[][] foliageNoise = PerlinNoise.generatePerlinNoise(width, height, 
				FOLIAGE_OCTAVE_COUNT, FOLIAGE_PERSISTANCE_COUNT, MathUtils.random(1000000, 100000000) + seed);
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				float val = foliageNoise[x][y];
				if(tiles[x][y] == (byte)2) {
					if(x + 1 < width) if(tiles[x + 1][y] == 3 || (tiles[x + 1][y] <= 13 && tiles[x + 1][y] >= 22)) continue;
					if(x - 1 > 0) if(tiles[x - 1][y] == 3 || (tiles[x - 1][y] <= 13 && tiles[x - 1][y] >= 22)) continue;
					if(y - 1 > 0) if(tiles[x][y - 1] == 3 || (tiles[x][y - 1] <= 13 && tiles[x][y - 1] >= 22)) continue;
					if(y + 1 < height) if(tiles[x][y + 1] == 3 || (tiles[x][y + 1] <= 13 && tiles[x][y + 1] >= 22)) continue;
					if(tiles[x][y] >= 4 && tiles[x][y] <= 12) continue;
					if(val >= FOLIAGE_LIKELYHOOD) tiles[x][y] = (byte)MathUtils.random(13, 22);
				}
			}
		}
		
		float[][] oreNoise = PerlinNoise.generatePerlinNoise(width, height, 
				ORE_OCTAVE_COUNT, ORE_PERSISTANCE_COUNT, MathUtils.random(1000000, 100000000) + seed);
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				float val = oreNoise[x][y];
				if(tiles[x][y] == (byte)2) {
					if(x + 1 < width) if(tiles[x + 1][y] == 3 || tiles[x + 1][y] == 1 || (tiles[x + 1][y] <= 23 && tiles[x + 1][y] >= 38)) continue;
					if(x - 1 > 0) if(tiles[x - 1][y] == 3 || tiles[x - 1][y] == 1 || (tiles[x - 1][y] <= 23 && tiles[x - 1][y] >= 38)) continue;
					if(y - 1 > 0) if(tiles[x][y - 1] == 3 || tiles[x][y - 1] == 1 || (tiles[x][y - 1] <= 23 && tiles[x][y - 1] >= 38)) continue;
					if(y + 1 < height) if(tiles[x][y + 1] == 3 || tiles[x][y + 1] == 1 || (tiles[x][y + 1] <= 23 && tiles[x][y + 1] >= 38)) continue;
					if(tiles[x][y] >= 4 && tiles[x][y] <= 12) continue;
					
					if(val >= 0.64f && val < 0.68f) tiles[x][y] = (byte)MathUtils.random(23, 26);
					else if (val >= 0.72f && val < 0.76f) tiles[x][y] = (byte)MathUtils.random(27, 30);
					else if (val >= 0.76f && val < 0.80f) tiles[x][y] = (byte)MathUtils.random(31, 34);
					else if (val >= 0.80f && val < 0.84f) tiles[x][y] = (byte)MathUtils.random(35, 38);
				}
			}
		}
		
		return tiles;
	}

}
