package dev.iwilkey.terrafort.particle;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.assets.Assets;

public class ParticleHandler {
	
	public ArrayList<Particle> particles;
    public ArrayList<Particle> runningParticles;

    public ParticleHandler(){
        particles = Assets.initParticles();
        runningParticles = new ArrayList<>();
    }

    public void start(String name, int x, int y) {
        for(Particle part : particles)
            if(part.name.equals(name)) {
                part.setPosition(x, y);
                part.setColor(Color.WHITE);
                part.start();
                runningParticles.add(part);
                return;
            }
    }
    
    public void start(String name, Color color, int x, int y) {
        for(Particle part : particles)
            if(part.name.equals(name)) {
                part.setPosition(x, y);
                part.setColor(color);
                part.start();
                runningParticles.add(part);
                return;
            }
    }
    
    public void end(String name) {
    	for(Particle p : particles) if(p.name.equals(name)) p.stop();
    	for(Particle part : runningParticles)
            if(part.name.equals(name)) {
            	runningParticles.remove(part);
                return;
            }
    }
    
    public void update(String name, int x, int y) {
    	for(Particle part : runningParticles)
            if(part.name.equals(name)) {
            	part.setPosition(x, y);
                return;
            }
    }

    public void tick() {
        for(Particle part : runningParticles) {
            part.update();
            if(part.isDone()) {
                runningParticles.remove(part);
                return;
            }
        }
    }

    public void render(Batch b) {
        for(Particle part : runningParticles) part.render(b);
    }

}
