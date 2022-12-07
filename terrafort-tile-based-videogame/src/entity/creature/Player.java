package dev.iwilkey.terrafort.entity.creature;

import java.awt.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.gfx.Geometry;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.gfx.Geometry.GeometryRequest;
import dev.iwilkey.terrafort.scene.MultiplayerClientWorldScene;
import dev.iwilkey.terrafort.scene.Scene;
import dev.iwilkey.terrafort.scene.WorldScene;
import dev.iwilkey.terrafort.tile.Tile;
import dev.iwilkey.terrafort.ui.Text;
import dev.iwilkey.terrafort.world.World;

public class Player extends Flybot {
	
	public static final int INTERACTION_RADIUS = 6 * Tile.TILE_SIZE,
			PLAYER_WIDTH = 32,
			PLAYER_HEIGHT = 32;
	// Server
	public int ID;
	
	// Get from account
	public String name;
	public Color color, mid; 
	
	Text namePlate;

	public Player(final WorldScene scene, int ID, String name, Color color) {
		super(scene, null, 0, 0, PLAYER_WIDTH, PLAYER_WIDTH, Assets.PLAYER);
		this.ID = ID;
		this.name = name;
		this.color = color;
		mid = new Color(color).mul(1.50f);
		
		if(scene instanceof MultiplayerClientWorldScene) {
			new Thread() {
				public void run() {
					while(true) {
						if(((MultiplayerClientWorldScene)scene).connected) {
							if(Math.abs(dx) > 0.0f || Math.abs(dy) > 0.0f) 
								((MultiplayerClientWorldScene)scene).client.sendPlayerUpdate();
						}
						
						try {
							sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}
	
	public void setWorld(World world) { this.world = world; }
	public void setLocation(Point p) { 
		if(world == null) return;
		position.x = p.x;
		position.y = p.y;
	}
	
	private void control() {
		
		if(InputHandler.moveRight) dx = speed;
		else if(InputHandler.moveLeft) dx = -speed;
		if(InputHandler.moveUp) dy = speed;
		else if(InputHandler.moveDown) dy = -speed;
			
	}
	
	public void tick() {
		if(world == null) return;
		
		((WorldScene)Scene.currentScene).camera.centerOnEntity(this);
		if(!scene.inventory.isActive) {
			if(InputHandler.leftMouseButtonDown) startParticle("sparks", color);
			if(InputHandler.leftMouseButton) {
				drawLaser();
				updateParticle("sparks");
				extraction();
			}
			if(InputHandler.leftMouseButtonUp) endParticle("sparks");
			control();
			((WorldScene)Scene.currentScene).camera.allowedZoom = true;
		} else ((WorldScene)Scene.currentScene).camera.allowedZoom = false;
		
		super.tick();
	}
	
	@Override
	public void die() {
		if(world == null) return;
	}
	
	@Override
	public void render(Batch b) {
		if(world == null) return;
		
		if(!scene.inventory.isActive) {
			if(b.getShader() != Renderer.DEFAULT_SHADER) b.setShader(Renderer.DEFAULT_SHADER);
		} else if(b.getShader() != Renderer.BLUR_SHADER) b.setShader(Renderer.BLUR_SHADER);
		
		if(namePlate == null) {
			namePlate = new Text();
			namePlate.message = name;
			namePlate.setSize(14);
		}
		
		namePlate.render(b, (int)((position.x + (position.width / 2f)) - 
				(namePlate.getLayoutWidth() / 2f)), (int)position.y + 
				(int)position.height + (namePlate.getLayoutHeight()) + 6);

		super.render(b);
		
	}
	
	public void render(Batch b, float x, float y) {
		if(world == null) return;
		
		if(!scene.inventory.isActive) {
			if(b.getShader() != Renderer.DEFAULT_SHADER) b.setShader(Renderer.DEFAULT_SHADER);
		} else if(b.getShader() != Renderer.BLUR_SHADER) b.setShader(Renderer.BLUR_SHADER);
		
		if(namePlate == null) {
			namePlate = new Text();
			namePlate.message = name;
			namePlate.setSize(14);
		}
		
		namePlate.render(b, (int)((x + (position.width / 2f)) - 
				(namePlate.getLayoutWidth() / 2f)), (int)y + 
				(int)position.height + (namePlate.getLayoutHeight()) + 6);

		super.render(b, x, y);
		
	}
	
	public void onGUI(Batch b) {}
	
	Tile currentTileSelected;
	public void extraction() {
		currentTileSelected = world.tileAt(cursorInRadiusX / Tile.TILE_SIZE, cursorInRadiusY / Tile.TILE_SIZE);
		System.out.println(currentTileSelected.getType().name());
	}
	
	int playerX, playerY, cursorInRadiusX, cursorInRadiusY,
		roundedTileStartX, roundedTileStartY, roundedTileEndX, roundedTileEndY;
	private void drawLaser() {
		
		playerX = (int)(position.x + (position.width / 2f)) + 1;
		playerY = (int)(position.y + (position.height / 2f));
		cursorInRadiusX = (int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).x;
		cursorInRadiusY = (int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).y;
		roundedTileStartX = (((int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).x) / Tile.TILE_SIZE) * Tile.TILE_SIZE;
		roundedTileStartY = (((int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).y) / Tile.TILE_SIZE) * Tile.TILE_SIZE;
		roundedTileEndX = ((((int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).x) / Tile.TILE_SIZE) * Tile.TILE_SIZE) + Tile.TILE_SIZE;
		roundedTileEndY = ((((int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).y) / Tile.TILE_SIZE) * Tile.TILE_SIZE) + Tile.TILE_SIZE;
		
		Geometry.requests.add(new GeometryRequest(Geometry.Shape.LINE, 
				playerX, playerY, cursorInRadiusX, cursorInRadiusY, 
					(int)(4 / ((World)world).camera.zoom), 
				color, ((World)world).camera.combined));
		
		Geometry.requests.add(new GeometryRequest(Geometry.Shape.LINE, 
				playerX, playerY, cursorInRadiusX, cursorInRadiusY,
					(int)(2 / ((World)world).camera.zoom), 
				mid, ((World)world).camera.combined));
		
		Geometry.requests.add(new GeometryRequest(Geometry.Shape.OUTLINE_RECTANGLE,
				roundedTileStartX, roundedTileStartY, roundedTileEndX, roundedTileEndY, 
				(int)(4 / ((World)world).camera.zoom),
				mid, ((World)world).camera.combined));
		
		
	}
	
	@SuppressWarnings({ "unused", "static-access" })
	private void startParticle(String name) {
		scene.particleHandler.start(name, (int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).x, 
				(int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).y);
	}
	
	@SuppressWarnings({ "unused", "static-access" })
	private void startParticle(String name, Color color) {
		scene.particleHandler.start(name, color, (int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).x, 
				(int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).y);
	}
	
	@SuppressWarnings({ "unused", "static-access" })
	private void updateParticle(String name) {
		scene.particleHandler.update(name, (int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).x, 
				(int)((World)world).cursorOnWithinRadius(this, INTERACTION_RADIUS).y);
	}
	
	@SuppressWarnings({ "static-access" })
	private void endParticle(String name) {
		scene.particleHandler.end(name);
	}
	
	@Override
	protected TextureRegion getCurrentSprite() {
		if(lookingLeft) return WALKING[2].getCurrentFrame();
		else return WALKING[0].getCurrentFrame();
	}

}
