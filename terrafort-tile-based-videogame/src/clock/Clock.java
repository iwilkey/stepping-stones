package dev.iwilkey.terrafort.clock;

import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.scene.Scene;

public class Clock {
	
	public static int FPS;
	
	long now = System.nanoTime();
	long lastTime = 0;
	long dt = 0;
	short ticks = 0;
	
	public Clock() {
		FPS = 60;
	}
	
	public void tick() {
		
		lastTime = now;
		now = System.nanoTime();
		dt += (now - lastTime);
		ticks++;
		
		if(dt >= 1000000000) {
			FPS = ticks;
			ticks = 0;
			dt = 0;
		}
		
		InputHandler.tick();
		Scene.currentScene.onTick(Gdx.graphics.getDeltaTime());
		
	}
	
	public void dispose() {
		for(ThreadRequest r : ThreadRequest.requests) r.kill();
	}
	
}
