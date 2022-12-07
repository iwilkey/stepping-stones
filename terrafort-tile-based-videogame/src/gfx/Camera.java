package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.entity.Entity;
import dev.iwilkey.terrafort.tile.Tile;
import dev.iwilkey.terrafort.world.World;

public class Camera extends OrthographicCamera {
	
	public static final float CAMERA_SPEED = 4.0f;
	public final float MAX_ZOOM = 6.0f,
			MIN_ZOOM = 0.5f;
	public float hx, hy;
	
	public boolean allowedZoom = true;
	
	World world;
	
	public Camera(World world, int width, int height) {
		super(width, height);
		position.set(viewportWidth / 2f, viewportHeight / 2f, 0);
		update();
		this.world = world;
	}
	
	public void tick() {
		update();
		if(allowedZoom) zoomHandler();
	}
	
	float targetZoom = 1.0f;
	private void zoomHandler() {
		if(Math.abs(InputHandler.scrollWheelRequestValue) > 0.0f) {
			targetZoom = zoom + (InputHandler.scrollWheelRequestValue / (1 / Settings.CAMERA_ZOOM_SENSITIVITY));
			InputHandler.scrollWheelRequestValue = 0.0f;
		}
		targetZoom = MathUtils.clamp(targetZoom, MIN_ZOOM, MAX_ZOOM);
		zoom += (targetZoom - zoom) * CAMERA_SPEED * Gdx.graphics.getDeltaTime();
	}
	
	float targxOffset, targyOffset;
	@SuppressWarnings("static-access")
	public void centerOnEntity(Entity e) {
		targxOffset = e.position.x + (e.position.width / 2);
        targyOffset = e.position.y + (e.position.height / 2);
       
        translate(((int)targxOffset - position.x) * CAMERA_SPEED * Gdx.graphics.getDeltaTime(), 
        		((int)targyOffset - position.y) * CAMERA_SPEED * Gdx.graphics.getDeltaTime());
        
        position.x = MathUtils.clamp(position.x, (Renderer.DEFAULT_WIDTH / 2f) * zoom, 
        		((world.SIZE * Tile.TILE_SIZE) - ((Renderer.DEFAULT_WIDTH / 2f) * zoom)));
        position.y = MathUtils.clamp(position.y, (Renderer.DEFAULT_HEIGHT / 2f) * zoom, 
        		((world.SIZE * Tile.TILE_SIZE) - ((Renderer.DEFAULT_HEIGHT / 2f) * zoom)));
        
	}
	

}
