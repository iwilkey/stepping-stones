package dev.iwilkey.terrafort.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Particle {
	
	public String name;
	public ParticleEffect particleEffect;
	public int x, y;
	
	public Particle(String path, String name) {
		this.name = name;
		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.local("particle/" + path),
				Gdx.files.local("particle/images"));
	}
	
	public void setPosition(int x, int y) {
		this.x = x; this.y = y;
		particleEffect.getEmitters().first().setPosition(x, y);
	}
	
	public boolean isDone() { return particleEffect.isComplete(); }
	
	public void setScale(float scaleFactor) {
		particleEffect.scaleEffect(scaleFactor);
	}
	
	public void setColor(Color color) {
		for(int i = 0; i < particleEffect.getEmitters().size - 1; i++) {
			particleEffect.getEmitters().get(i).getTint().setColors(new float[] {color.r, color.g, color.b, color.a});
		}
	}
	 
	public void start() {
		particleEffect.start();
	}
	
	public void stop() {
		particleEffect.reset();
	}
	
	public void update() {
		particleEffect.update(Gdx.graphics.getDeltaTime());
	}
	
	public void render(Batch b) {
		particleEffect.draw(b);
	}
	
}
