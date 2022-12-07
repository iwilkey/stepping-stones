package dev.iwilkey.terrafort.entity.creature;

import java.io.Serializable;

// For server
public class AbstractPlayer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int ID;
	public String name;
	public float x, y;
	public boolean facingLeft;
	public float[] color;

	public AbstractPlayer(int ID, String name, float[] color, float x, float y) {
		this.ID = ID;
		this.name = name;
		this.color = color;
		this.x = x; this.y = y;
	}
	
	public void info() {
		System.out.println(this.ID + " " + this.name + " " + this.color + " " + this.x + " " + this.y);
	}
	
}
