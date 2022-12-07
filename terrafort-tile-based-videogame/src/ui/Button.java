package dev.iwilkey.terrafort.ui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.gfx.Renderer;


/*
 * 
 * Sample use
 * 
	button = new Button("Button!", new Event() {
		@Override
		public void onClick() {
			
		}
	});
 * 
 */

public class Button extends UIObject {
	
	Text label;
	
	Texture background, outline;
	Color outlineColor, backgroundColor;
	ArrayList<Color> colors;
	
	Event event;
	
	boolean hovering;
	
	// Constructor
	public Button(String label, Event event, int width, int height) {
		
		Text text = new Text();
		this.label = text;
		text.color = Color.WHITE;
		text.message = label;
		
		this.event = event;
		
		DEFAULT_COLLIDER.width = width * Settings.UI_SCALE;
		DEFAULT_COLLIDER.height = height * Settings.UI_SCALE;
		
		colors = new ArrayList<>();
        colors.add(new Color(196f/255,196f/255,196f/255,1)); // Normal background color
        colors.add(new Color(100f/255,100f/255,100f/255,1)); // Outline color
        colors.add(new Color(120f/255,120f/255,120f/255,1)); // Hovering color
        colors.add(new Color(80f/255,80f/255,80f/255,1)); // Clicked color
        
        this.backgroundColor = colors.get(0);
        this.outlineColor = colors.get(1);
        outline = new Texture("textures/ui/button_outline.png");
        background = new Texture("textures/ui/button_background.png");
        
        hovering = false;
        
	}
	
	// Text methods
	public void setTextColor(Color color) {
		label.setColor(color);
	}
	
	public void setTextSize(int size) {
		label.setSize(size);
	}
	
	public void setTextMessage(String message) {
		label.message = message;
	}
	
	// Colors
	public void setColorScheme(Color color) {
		Color outline = new Color(color);
		outline.mul(0.98f);
		outline.a = 1.0f;
		Color hover = new Color(color);
		hover.mul(0.912f);
		hover.a = 1.0f;
		Color click = new Color(color);
		click.mul(0.76f);
		click.a = 1.0f;
	
		setDefaultBackgroundColor(color);
		setOutlineColor(outline);
		setHoveringColor(hover);
		setClickedColor(click);
	}
	
	public void setDefaultBackgroundColor(Color color) {
		colors.set(0, color);
		resetColor();
	}
	
	public void setOutlineColor(Color color) {
		colors.set(1, color);
		resetColor();
	}
	
	public void setHoveringColor(Color color) {
		colors.set(2, color);
		resetColor();
	}
	
	public void setClickedColor(Color color) {
		colors.set(3, color);
		resetColor();
	}
	
	private void resetColor() {
		this.backgroundColor = colors.get(0);
        this.outlineColor = colors.get(1);
	}
	
	// Ticking
	@Override
	public void tick() {
		if(DEFAULT_COLLIDER.contains(InputHandler.cursorCollider)) hovering = true;
		else hovering = false;
		
		if(hovering) {
            if(InputHandler.leftMouseButton) backgroundColor = colors.get(3);
            else backgroundColor = colors.get(2);
     
            if(InputHandler.leftMouseButtonUp) event.onClick();
            
        } else backgroundColor = colors.get(0);
		
	}
	
	// Rendering
	@Override
	public void render(Batch b) {
		
		/*
		 * Uncomment to render outline
		b.setColor(outlineColor);
	    b.draw(outline, DEFAULT_COLLIDER.x, DEFAULT_COLLIDER.y, 
	    		DEFAULT_COLLIDER.width, DEFAULT_COLLIDER.height);
	    */
		b.setColor(backgroundColor);
	    b.draw(background, DEFAULT_COLLIDER.x, DEFAULT_COLLIDER.y, 
	    		DEFAULT_COLLIDER.width, DEFAULT_COLLIDER.height);
	    b.setColor(Color.WHITE);
	   
	    
	    int relTextX = (int)((DEFAULT_COLLIDER.width / 2) - (label.getLayoutWidth() / 2)),
	    		relTextY = (int)((DEFAULT_COLLIDER.height / 2) + (label.getLayoutHeight() / 2));
	    
	    label.render(b, (int)DEFAULT_COLLIDER.x + relTextX, (int)DEFAULT_COLLIDER.y + relTextY);
		
	}

}
