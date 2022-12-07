package dev.iwilkey.terrafort.scene;

import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.inventory.Inventory;
import dev.iwilkey.terrafort.particle.ParticleHandler;
import dev.iwilkey.terrafort.world.World;

public abstract class WorldScene extends Scene {
	
	public World world;
	public Camera camera;
	public static ParticleHandler particleHandler;
	public Inventory inventory;
	
	public WorldScene(String name, int size) {
		super(name);
		particleHandler = new ParticleHandler();
		inventory = new Inventory();
	}
	
	@Override
	public void onTick(double dt) {
		inventory.tick();
		particleHandler.tick();
		camera.tick();
		world.tick();
	}

	@Override
	public void onRender(Batch b) {
		b.setProjectionMatrix(camera.combined);
		world.render(b);
		particleHandler.render(b);
	}

	@Override
	public void onGUI(Batch b) {
		if(inventory.isActive) inventory.render(b);
	}

	@Override
	public void onResize(int width, int height) {}
	
}
