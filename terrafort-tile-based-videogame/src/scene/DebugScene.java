package dev.iwilkey.terrafort.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.entity.creature.Player;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.tile.Tile;
import dev.iwilkey.terrafort.ui.Button;
import dev.iwilkey.terrafort.ui.Event;
import dev.iwilkey.terrafort.world.World;

public class DebugScene extends WorldScene {
	
	Button button;

	public DebugScene() {
		super("Debug Scene", 512);
		
		world = new World(this, 512);
		camera = new Camera(world, Renderer.DEFAULT_WIDTH, Renderer.DEFAULT_HEIGHT);
		world.setCamera(camera);
		world.addPlayer(new Player(this, 0, "Debug Player", new Color(MathUtils.random(0,255) / 255f, 
				MathUtils.random(0,255) / 255f, MathUtils.random(0,255) / 255f, 1.0f)), (World.SIZE / 2) * Tile.TILE_SIZE, 
				(World.SIZE / 2) * Tile.TILE_SIZE);
		world.setMainPlayer(world.players.get(0));
		
		button = new Button("New Terrain", new Event() {
			@Override
			public void onClick() {
				world.createNewWorld();
			}
		}, 100, 50);
		button.setColorScheme(new Color(MathUtils.random(0,255) / 255f, 
				MathUtils.random(0,255) / 255f, MathUtils.random(0,255) / 255f, 1.0f));
		button.setTextSize(10);
		button.centerScreenX();
		button.setX(10);
		button.setY(10);
		
	}

	@Override
	public void onTick(double dt) {
		super.onTick(dt);
		button.tick();
	}
	
	@Override
	public void onGUI(Batch b) {
		super.onGUI(b);
		world.onGUI(b);
		button.render(b);
	}

}
