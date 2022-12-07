package dev.iwilkey.terrafort;

import com.badlogic.gdx.ApplicationAdapter;

import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.clock.Clock;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.scene.DebugScene;
import dev.iwilkey.terrafort.scene.MultiplayerClientWorldScene;
import dev.iwilkey.terrafort.scene.Scene;

@SuppressWarnings("unused")
public class Game extends ApplicationAdapter {
	
	Clock clock;
	Renderer renderer;
	
	@Override
	public void create () {
		Settings.init();
		InputHandler.init();
		Assets.init();	
		clock = new Clock();
		renderer = new Renderer();
		// Scene.currentScene = new MultiplayerClientWorldScene("localhost", 8080);
		Scene.currentScene = new DebugScene();
	}
	
	public void tick() {
		clock.tick();
	}

	@Override
	public void render () {
		tick();
		renderer.render();
	}
	
	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
	}
	
	@Override
	public void dispose () {
		clock.dispose();
		renderer.dispose();
	}
	
}
