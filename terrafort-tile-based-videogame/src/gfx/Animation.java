package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {

	private final TextureRegion[] frames;
	public short speed;
	private byte index;
	
	public Animation(TextureRegion[] frames, short speed) {
		this.frames = frames;
		this.speed = speed;
	}
	
	short timer;
	public void tick() {
		if(timer < speed) 
			timer++;
		else {
			timer = 0;
			nextFrame();
		}
	}
	
	private void nextFrame() {
		index++;
        if(index >= frames.length) 
        	index = 0;
	}
	
	public TextureRegion getCurrentFrame() {
		return frames[index];
	}

}
