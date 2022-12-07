package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.assets.IOUtils;
import dev.iwilkey.terrafort.clock.Clock;
import dev.iwilkey.terrafort.clock.SequencedEvent;
import dev.iwilkey.terrafort.clock.ThreadRequest;
import dev.iwilkey.terrafort.gfx.Geometry.GeometryRequest;
import dev.iwilkey.terrafort.scene.Scene;
import dev.iwilkey.terrafort.ui.Text;

public class Renderer {
	
	public final static int DEFAULT_WIDTH = Gdx.graphics.getWidth(), 
		DEFAULT_HEIGHT = Gdx.graphics.getHeight();
	public static float SCALEX = 1.0f,
			SCALEY = 1.0f;
	
	SpriteBatch mainBatch;
	SpriteBatch guiBatch;
	
	public static ShaderProgram BLUR_SHADER;
    public static ShaderProgram DEFAULT_SHADER;
	
	final Text debugFPS;
	
	public Renderer() {
		
		mainBatch = new SpriteBatch();
		guiBatch = new SpriteBatch();
		
		initShaders();
		
		debugFPS = new Text("");
		debugFPS.centerScreenX();
		debugFPS.setY((int)(60 * Settings.UI_SCALE));
		debugFPS.setSize(18);
		debugFPS.setSize(30);
		
		new ThreadRequest(new SequencedEvent() {
			@Override
			public void onStart() {}
			@Override
			public void onLoop() {
				debugFPS.message = "Terrafort POC [Engine p13]\nFPS: " + Clock.FPS + " BD: 1/18/2020";
				debugFPS.centerScreenX();
			}
			@Override
			public void onKill() {}
		}, 200);
	}
	
	 private void initShaders() {
    	try {
    		
    		final String VERT = IOUtils.readFile(Gdx.files.internal("shaders/standard.vert").read());
    		final String BLUR_FRAG = IOUtils.readFile(Gdx.files.internal("shaders/blur.frag").read());
    		final String DEFAULT = IOUtils.readFile(Gdx.files.internal("shaders/default.frag").read());
    		BLUR_SHADER = new ShaderProgram(VERT, BLUR_FRAG);
    		DEFAULT_SHADER = new ShaderProgram(VERT, DEFAULT);
 
    		if(BLUR_SHADER.getLog().length() != 0 || DEFAULT_SHADER.getLog().length() != 0) {
    			System.out.println(BLUR_SHADER.getLog());
    			System.out.println(DEFAULT_SHADER.getLog());
    			System.exit(-1);
    		}
    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
    	
    	ShaderProgram.pedantic = false;
    	
    }

	
	public void render() {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mainBatch.begin();
		Scene.currentScene.onRender(mainBatch);
		mainBatch.end();
		
		guiBatch.begin();
		debugFPS.render(guiBatch);
		Scene.currentScene.onGUI(guiBatch);
		guiBatch.end();

		for(GeometryRequest g : Geometry.requests) g.render();
		Geometry.requests.clear();
		
	}
	
	public void resize(int width, int height) {	
		SCALEX = (width / (float)DEFAULT_WIDTH);
		SCALEY = (height / (float)DEFAULT_HEIGHT);
	}
	
	public void dispose() {
		mainBatch.dispose();
		guiBatch.dispose();
	}
	
}
