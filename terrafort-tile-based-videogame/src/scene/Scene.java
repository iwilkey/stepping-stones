package dev.iwilkey.terrafort.scene;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class Scene {
	
	public static Scene currentScene;
	
	public String name;
	
	public Scene(String name) {
		this.name = name;
	}
	
	public abstract void onTick(double dt);
	public abstract void onRender(Batch b);
	public abstract void onGUI(Batch b);
	public abstract void onResize(int width, int height);

}
