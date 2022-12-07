package dev.iwilkey.terrafort.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.gfx.Renderer;

public class Text {
	
	BitmapFont font;
	Color color;
	int xx, yy;
	
	public String message;
	
	// Construction
	public Text() {
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);
		font.getData().setScale((Settings.FONT_SIZE * Settings.UI_SCALE) / 128.0f);
	}
	
	public Text(String message) {
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);
		font.getData().setScale((Settings.FONT_SIZE * Settings.UI_SCALE) / 128.0f);
		this.message = message;
	}
	
	public Text(String message, int x, int y) {
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), false);
		font.getData().setScale((Settings.FONT_SIZE * Settings.UI_SCALE) / 128.0f);
		this.message = message;
		this.xx = x; this.yy = y;
	}
	
	// Physical attributes
	public void setColor(Color color) {
		font.setColor(color);
	}
	
	public void setSize(int size) {
		font.getData().setScale((size * Settings.UI_SCALE) / 128.0f);
	}
	
	// Graphing
	public void setX(int x) {
		xx = x;
	}
	
	public void setY(int y) {
		yy = y;
	}
	
	public int centerScreenX() {
		GlyphLayout layout = new GlyphLayout(font, message);
		int textWidth = (int)layout.width;
		xx = (Renderer.DEFAULT_WIDTH / 2) - (textWidth / 2);
		return xx;
	}
	
	public int centerScreenY() {
		GlyphLayout layout = new GlyphLayout(font, message);
		int textHeight = (int)layout.height;
		yy = (Renderer.DEFAULT_HEIGHT / 2) + (textHeight / 2);
		return yy;
	}
	
	public int centerContainerX(Container c) {
		GlyphLayout layout = new GlyphLayout(font, message);
		int textWidth = (int)layout.width;
		xx = (int)(c.DEFAULT_COLLIDER.x + (c.DEFAULT_COLLIDER.width / 2)) - (textWidth / 2);
		return xx;
	}
	
	public int centerContainerY(Container c) {
		GlyphLayout layout = new GlyphLayout(font, message);
		int textHeight = (int)layout.height;
		yy = (int)(c.DEFAULT_COLLIDER.y + (c.DEFAULT_COLLIDER.height / 2)) + (textHeight / 2);
		return yy;
	}
	
	public int getLayoutWidth() {
		GlyphLayout layout = new GlyphLayout(font, message);
		return (int)layout.width;
	}
	
	public int getLayoutHeight() {
		GlyphLayout layout = new GlyphLayout(font, message);
		return (int)layout.height;
	}
	
	// Rendering
	public void render(Batch b) {
		font.draw(b, message, xx, yy);
	}

	public void render(Batch b, int x, int y) {
		font.draw(b, message, x, y);
	}

}
