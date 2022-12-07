package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheet {
	
	public static byte SLOT_SIZE = 16;
	public Texture sheet;
	
	public SpriteSheet(Texture sheet) {
		this.sheet = sheet;
	}
	
	public TextureRegion crop(int x, int y) {
		return new TextureRegion(sheet, x, y, SLOT_SIZE, SLOT_SIZE);
	}
	
	public TextureRegion crop(int x, int y, int width, int height) {
		return new TextureRegion(sheet, x, y, width, height);
	}
	
}
