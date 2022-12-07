package dev.iwilkey.terrafort.entity.creature;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.clock.Clock;
import dev.iwilkey.terrafort.entity.Entity;
import dev.iwilkey.terrafort.gfx.Animation;
import dev.iwilkey.terrafort.scene.WorldScene;
import dev.iwilkey.terrafort.tile.Tile;
import dev.iwilkey.terrafort.world.Space;
import dev.iwilkey.terrafort.world.World;

public abstract class Flybot extends Entity {
	
	
	public static final float DEFAULT_SPEED = 10.0f,
		AIR_RESISTANCE = 1.0f;
	public static final short DEFAULT_ANIMATION_SPEED = 1;
	
	
	/*
	 * Animation guide
	 * 0 - Walk right
	 * 1 - Walk down
	 * 2 - Walk left
	 * 3 - Walk right
	 */
	protected final Animation[] WALKING;
	
	/*
	 * Idle guide
	 * 0 - Idle right
	 * 1 - Idle down
	 * 2 - Idle left
	 * 3 - Idle right
	 */
	protected final TextureRegion[] IDLE;
	
	public float speed;
	protected float dx, dy;
	
	/*
	 * Direction guide:
	 * 0 - Right
	 * 1 - Down
	 * 2 - Left
	 * 3 - Up
	 */
	
	protected byte direction = 0;
	
	// States
	public boolean isMoving,
		lookingRight, lookingLeft;
	
	public Flybot(WorldScene scene, World world, int x, int y, int width, int height, TextureRegion[] sprites) {
		super(scene, world);
		position.x = x;
		position.y = y;
		position.width = width;
		position.height = height;
		speed = DEFAULT_SPEED;
		
		// Set animations and sprites
		TextureRegion[] walkRight = new TextureRegion[3];
		walkRight[0] = sprites[1];
		walkRight[1] = sprites[0];
		walkRight[2] = sprites[2];
		
		TextureRegion[] walkDown = new TextureRegion[3];
		walkDown[0] = sprites[4];
		walkDown[1] = sprites[3];
		walkDown[2] = sprites[5];
		
		TextureRegion[] walkLeft = new TextureRegion[3];
		walkLeft[0] = sprites[7];
		walkLeft[1] = sprites[6];
		walkLeft[2] = sprites[8];
		
		TextureRegion[] walkUp = new TextureRegion[3];
		walkUp[0] = sprites[10];
		walkUp[1] = sprites[9];
		walkUp[2] = sprites[11];
		
		WALKING = new Animation[4];
		WALKING[0] = new Animation(walkRight, DEFAULT_ANIMATION_SPEED);
		WALKING[1] = new Animation(walkDown, DEFAULT_ANIMATION_SPEED);
		WALKING[2] = new Animation(walkLeft, DEFAULT_ANIMATION_SPEED);
		WALKING[3] = new Animation(walkUp, DEFAULT_ANIMATION_SPEED);
		
		IDLE = new TextureRegion[4];
		IDLE[0] = sprites[0];
		IDLE[1] = sprites[3];
		IDLE[2] = sprites[6];
		IDLE[3] = sprites[9];
		
		
	}
	
	private void translate() {
		byte addX = 0;
		if(Math.abs(dx) > 0.0f) {
			if(dx < 0.0f) {
				dx += AIR_RESISTANCE;
				if(dx >= 0.0f) dx = 0;
				direction = 2;
				lookingLeft = true;
				addX = -1;
			}
			else if (dx > 0.0f) {
				dx -= AIR_RESISTANCE;
				if(dx <= 0.0f) dx = 0;
				direction = 0;
				lookingLeft = false;
				addX = 1;
			}
			if(!world.isSolidAtPixel((int)(position.x + dx + (addX * 6)), (int)position.y)) {
				position.x += (position.x + dx >= ((Space.SIZE * Tile.TILE_SIZE) - position.width) 
						|| position.x + dx <= 0) ? 0 : dx * (60 / Clock.FPS);
				isMoving = true;
			}
		}
		byte addY = 0;
		if(Math.abs(dy) > 0.0f) {
			if(dy < 0.0f) {
				dy += AIR_RESISTANCE;
				if(dy >= 0.0f) dy = 0;
				direction = 1;
				addY = -1;
			}
			else if (dy > 0.0f) {
				dy -= AIR_RESISTANCE;
				if(dy <= 0.0f) dy = 0;
				direction = 3;
				addY = 1;
			}
			if(!world.isSolidAtPixel((int)position.x, (int)(position.y + dy + (addY * 6)))) {
				position.y += (position.y + dy >= ((Space.SIZE * Tile.TILE_SIZE) - position.height) 
						|| position.y + dy <= 0) ? 0 : dy * (60 / Clock.FPS);
				isMoving = true;
			}
		}
		if(dy == 0.0f && dx == 0.0f) isMoving = false;
		lookingRight = !lookingLeft;
	}
	
	public void tick() {
		translate();
		for(Animation a : WALKING) a.tick();
	}
	
	
	Color shadow = new Color(0,0,0, 0.1f);
	public void render(Batch b) {
		b.setColor(shadow);
		b.draw(getCurrentSprite(), position.x + 12, position.y - 8, position.width, position.height);	
		if(this instanceof Player) b.setColor(((Player)this).color);
		b.draw(getCurrentSprite(), position.x, position.y, position.width, position.height);
		b.setColor(Color.WHITE);
	}
	
	public void render(Batch b, float x, float y) {
		b.setColor(shadow);
		b.draw(getCurrentSprite(), x + 12, y - 8, position.width, position.height);
		if(this instanceof Player) b.setColor(((Player)this).color);
		b.draw(getCurrentSprite(), x, y, position.width, position.height);
		b.setColor(Color.WHITE);
	}
	
	protected TextureRegion getCurrentSprite() {
		return WALKING[direction].getCurrentFrame();
	}
	
}
