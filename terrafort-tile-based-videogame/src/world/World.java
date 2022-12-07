package dev.iwilkey.terrafort.world;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.entity.creature.AbstractPlayer;
import dev.iwilkey.terrafort.entity.creature.Player;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.math.Vector2D;
import dev.iwilkey.terrafort.scene.WorldScene;
import dev.iwilkey.terrafort.tile.Tile;

public class World extends Space {
	
	public Camera camera;
	
	public Player mainPlayer;
	public ArrayList<Player> players;
	
	public World(WorldScene scene, int size) {
		super(scene, size);
		
		if(size == 0) {
			TILES = new byte[1][1];
		} else {
			TILES = new byte[size][size];
			TILES = WorldGeneration.GenerateWorld(TILES, size, size, 
					MathUtils.random(1000000, 100000000));
		}
		
		players = new ArrayList<>();
	}
	
	public World(WorldScene scene, int size, boolean server) {
		super(scene, size);
		
		if(server) {
			TILES = new byte[size][size];
			players = new ArrayList<>();
		} else {
			if(size == 0) {
				TILES = new byte[1][1];
			} else {
				TILES = new byte[size][size];
				TILES = WorldGeneration.GenerateWorld(TILES, size, size, 
						MathUtils.random(1000000, 100000000));
			}
			
			players = new ArrayList<>();
		}
	}
	
	@SuppressWarnings("static-access")
	public void setWorldFromAbstract(AbstractWorld world) {
		if(this.SIZE == 0) this.SIZE = world.SIZE;
	
		// Set tiles
		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				if(this.TILES[x][y] != world.TILES[x][y]) 
					this.TILES[x][y] = world.TILES[x][y];
			}
		}
		
	}
	
	public void setPlayersFromAbstract(AbstractWorld world) {
		player: for(AbstractPlayer player : world.players) {
			for(Player p : players) {
				if(p.name.equals(player.name)) {
					// Set all attributes needed
					p.position.x = player.x;
					p.position.y = player.y;
					p.lookingLeft = player.facingLeft;
					continue player;
				}
			}
		
			addPlayer(createPlayerFromAbstract(player), (int)player.x, (int)player.y);
			setPlayersFromAbstract(world);
		}
	}
	
	public Player createPlayerFromAbstract(AbstractPlayer player) {
		Color color = new Color(player.color[0], player.color[1], 
				player.color[2], 1.0f);
		return new Player(scene, player.ID, player.name, color);
	}
	
	public void setMainPlayerFromAbstract(AbstractPlayer player) {
		setMainPlayer(createPlayerFromAbstract(player));
	}
	
	public void setMainPlayer(Player player) {
		System.out.println(player.color + " MAIN PLAYER!");
		player.setWorld(this);
		players.add(player);
		mainPlayer = player;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public Player addPlayer(Player player, int x, int y) {
		player.setWorld(this);
		player.setLocation(new Point(x, y));
		players.add(player);
		return player;
	}
	
	public Point cursorOn() {
		int differenceX = (int)(InputHandler.cursorCollider.x - (Renderer.DEFAULT_WIDTH / 2f));
		int differenceY = (int)(InputHandler.cursorCollider.y - (Renderer.DEFAULT_HEIGHT / 2f));
		return new Point(
			(int)(camera.position.x + (differenceX * camera.zoom)),
			(int)(camera.position.y + (differenceY * camera.zoom))
		);
	}
	
	public Point cursorOnWithinRadius(Player player, int maxRadius) {
		Point currentPlayerCoord = new Point((int)(player.position.x + (player.position.width / 2f) + 1), 
				(int)(player.position.y + (player.position.height / 2f)));
		Point currentWorldCoord = cursorOn();
		
		Vector2D direction = new Vector2D(currentWorldCoord.x - currentPlayerCoord.x, 
				currentWorldCoord.y - currentPlayerCoord.y);
		float theta = direction.angleRad();
		
		float r = direction.magnitude();
		r = MathUtils.clamp(r, direction.magnitude(), maxRadius);
		
		Point allowedPoint = new Point();
		allowedPoint.x = (int)(currentPlayerCoord.x + (r * Math.cos(theta)));
		allowedPoint.y = (int)(currentPlayerCoord.y + (r * Math.sin(theta)));
		
		return allowedPoint;
	}
	
	public void createNewWorld() {
		TILES = WorldGeneration.GenerateWorld(TILES, SIZE, SIZE, 
				MathUtils.random(1000000, 100000000));
	}
	
	public void tick() {
		if(mainPlayer != null) {
			mainPlayer.tick();
		}
	}
	
	byte[] around;
	public void render(Batch b) {
		if(camera == null) return;
		
		xStart = (int) Math.max(0, ((((camera.position.x) - 
				(Renderer.DEFAULT_WIDTH / 2f) * camera.zoom) / Tile.TILE_SIZE) - 6));
        xEnd = (int) Math.min(SIZE, (((camera.position.x + 
        		(Renderer.DEFAULT_WIDTH / 2f) * camera.zoom) / Tile.TILE_SIZE) + 6));
        yStart = (int) Math.max(0, ((camera.position.y - 
        		(Renderer.DEFAULT_HEIGHT / 2f) * camera.zoom) / Tile.TILE_SIZE) - 6);
        yEnd = (int) Math.min(SIZE, (((camera.position.y + 
        		(Renderer.DEFAULT_HEIGHT / 2f) * camera.zoom) / Tile.TILE_SIZE) + 6));
		
		for(int x = xStart; x < xEnd; x++) 
			for(int y = yStart; y < yEnd; y++) 
				for(Tile t : Tile.values()) 
					if(t.getID() == TILES[x][y]) {
						around = new byte[8];
						around = surrounding(around, t, x, y);
						t.render(around, b, x * Tile.TILE_SIZE, y * Tile.TILE_SIZE);
					}
		
		for(Player p : players) {
			if(p != mainPlayer) {
				p.render(b, p.position.x, p.position.y);
			} else p.render(b);
		}
	}
	
	public void onGUI(Batch b) {
		mainPlayer.onGUI(b);
	}
	
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
	private byte[] surrounding(byte[] input, Tile t, int x, int y) {
		if(x - 1 >= 0 && y + 1 < SIZE) input[0] = tileIDAt(x - 1, y + 1);
		if(y + 1 < SIZE) input[1] = tileIDAt(x, y + 1);
		if(x + 1 < SIZE && y + 1 < SIZE) input[2] = tileIDAt(x + 1, y + 1);
		if(x - 1 >= 0) input[3] = tileIDAt(x - 1, y);
		if(x + 1 < SIZE) input[4] = tileIDAt(x + 1, y);
		if(x - 1 >= 0 && y - 1 >= 0) input[5] = tileIDAt(x - 1, y - 1);
		if(y - 1 >= 0) input[6] = tileIDAt(x, y - 1);
		if(x + 1 < SIZE && y - 1 >= 0) input[7] = tileIDAt(x + 1, y - 1);
		return input;
	}
	
}
