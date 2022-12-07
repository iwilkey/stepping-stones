package dev.iwilkey.terrafort.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.gfx.Renderer;

public abstract class UIObject {
	
	public final Rectangle DEFAULT_COLLIDER;
	
	public UIObject() {
		DEFAULT_COLLIDER = new Rectangle();
	}
	
	public UIObject(int x, int y, int width, int height) {
		DEFAULT_COLLIDER = new Rectangle(x, y, width, height);
	}
	
	public abstract void tick();
	public abstract void render(Batch b);
	public void onResize(int width, int height) {}
	
	public void setX(int x) {
		DEFAULT_COLLIDER.x = x;
	}
	
	public void setY(int y) {
		DEFAULT_COLLIDER.y = y;
	}
	
	public void setWidth(int width) {
		DEFAULT_COLLIDER.width = (width * Settings.UI_SCALE);
	}
	
	public void setHeight(int height) {
		DEFAULT_COLLIDER.height = (height * Settings.UI_SCALE);
	}
	
	public int centerScreenX() {
		DEFAULT_COLLIDER.x = ((int)((Renderer.DEFAULT_WIDTH / 2) - (DEFAULT_COLLIDER.width / 2)));
		return (int)DEFAULT_COLLIDER.x;
	}
	
	public int centerScreenY() {
		DEFAULT_COLLIDER.y = ((int)((Renderer.DEFAULT_HEIGHT / 2) - (DEFAULT_COLLIDER.height / 2)));
		return (int)DEFAULT_COLLIDER.y;
	}
	
	public int centerContainerX(Container c) {
		DEFAULT_COLLIDER.x = (int)(c.DEFAULT_COLLIDER.x + 
				(c.DEFAULT_COLLIDER.width / 2)) - (DEFAULT_COLLIDER.width / 2);
		return (int)DEFAULT_COLLIDER.x;
	}
	
	public int centerContainerY(Container c) {
		DEFAULT_COLLIDER.y = (int)(c.DEFAULT_COLLIDER.y + 
				(c.DEFAULT_COLLIDER.height / 2)) + (DEFAULT_COLLIDER.height / 2);
		return (int)DEFAULT_COLLIDER.y;
	}
	
}
