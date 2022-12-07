package dev.iwilkey.terrafort.math;

import com.badlogic.gdx.math.Vector2;

public class Vector2D extends Vector2 {
	
	private static final long serialVersionUID = 1L;
	
	public Vector2D() {
		super();
	}
	
	public Vector2D(int x, int y) {
		super(x, y);
	}
	
	public float magnitude() {
		return (float)Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
	}

}
