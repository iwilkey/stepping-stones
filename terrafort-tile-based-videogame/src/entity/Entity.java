package dev.iwilkey.terrafort.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import dev.iwilkey.terrafort.scene.WorldScene;
import dev.iwilkey.terrafort.world.Space;

public abstract class Entity {
	
	public static final int DEFAULT_ENTITY_HEALTH = 10;
	
	public WorldScene scene;
	public Space world;
	public Rectangle position;
	// Multiplayer rendering
	public float oldPosX, oldPosY, targetX, targetY,
		timeSinceLastUpdate;
	public int health;
	
	public Entity(WorldScene scene, Space world) {
		position = new Rectangle();
		health = DEFAULT_ENTITY_HEALTH;
		this.scene = scene;
		this.world = world;
	}
	
	public abstract void tick();
	public abstract void render(Batch b);
	
	public void hurt(int amt) {
		health -= amt;
		if(health <= 0) die();
	}
	
	public abstract void die();

}
