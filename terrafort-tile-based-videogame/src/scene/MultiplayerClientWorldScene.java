package dev.iwilkey.terrafort.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.entity.creature.AbstractPlayer;
import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.networking.Packets;
import dev.iwilkey.terrafort.networking.client.Client;
import dev.iwilkey.terrafort.ui.Container;
import dev.iwilkey.terrafort.ui.InputField;
import dev.iwilkey.terrafort.ui.Text;
import dev.iwilkey.terrafort.ui.TextLog;
import dev.iwilkey.terrafort.world.AbstractWorld;
import dev.iwilkey.terrafort.world.World;

public class MultiplayerClientWorldScene extends WorldScene {
	
	Text info;
	
	// This is a client world scene. It will render the abstract world to the screen
	// so you can see what all other clients see, but focused on your player.
	public Client client;
	public boolean connected = false;
	boolean chatUp;
	
	// Server data
	AbstractWorld abstractWorld;
	public AbstractPlayer abstractPlayer;
	
	Container chatContainer;
	InputField input; // Debug
	
	public MultiplayerClientWorldScene(String SERVER_IP, int SERVER_PORT) {
		super("Multiplayer World Scene", 0);
		camera = null; world = null;
		initUI();
		client = new Client(this, SERVER_IP, SERVER_PORT, 1024 * 20);
	}
	
	private void initUI() {
		info = new Text("");
		info.message = "Not connected to a server!";
		info.centerScreenX();  
		info.centerScreenY();
		info.setSize(22);
		info.setColor(Color.WHITE);
		
		chatUp = true;
		chatContainer = new Container(10,10, 480, 500);
		chatContainer.setBackgroundColor(new Color(1,1,1,0.3f));
		Text t = new Text("Console / Chat");
		t.setColor(Color.WHITE);
		chatContainer.addText(t);
		t.centerContainerX(chatContainer);
		chatContainer.setTextY(t, 10);
		
		InputField i = chatContainer.addInputField(new InputField(0,0, 500, 500, 12));
		i.setCharLimitInContainer(chatContainer);
		chatContainer.setInputFieldX(i, 5);
		chatContainer.setInputFieldY(i, (int)((chatContainer.DEFAULT_COLLIDER.height - 20) / Settings.UI_SCALE));
		
		TextLog c = (TextLog)(chatContainer.addContainer(new TextLog(0,0, 470, 400)));
		c.setBackgroundColor(Color.CLEAR);
		c.centerContainerX(chatContainer);
		c.centerContainerY(chatContainer);
		
		i.attachTextLog(c);
		
	}
	
	// Called upon a successful connection
	public void connection(AbstractPlayer p, int worldSize) {
		connected = true;
		abstractPlayer = p;
	
		// An abstract world is the thing that's updated from the server.
		abstractWorld = new AbstractWorld(worldSize);
		// Then, the abstract world is turned into a real one with this as the vehicle.
		world = new World(this, worldSize, true);
		// A new camera is created.
		camera = new Camera(world, Renderer.DEFAULT_WIDTH, Renderer.DEFAULT_HEIGHT);
		world.setMainPlayerFromAbstract(abstractPlayer);
		world.setCamera(camera);
		
	}
	
	public void setTile(Packets.ByteInfo info) {
		abstractWorld.TILES[info.x][info.y] = info.data;
	}
	
	public void setPlayer(AbstractPlayer p) {
		for(AbstractPlayer pp : abstractWorld.players) {
			if(p.name.equals(pp.name)) {
				pp.x = p.x; pp.y = p.y;
				return;
			}
		}
		
		abstractWorld.addPlayer(p);
		setPlayer(p);
	}
	
	// Called after connection has been established
	public void update() {
		world.setWorldFromAbstract(abstractWorld);
		world.setPlayersFromAbstract(abstractWorld);
	}
	
	@Override
	public void onTick(double dt) {
		chatContainer.tick();
		if(!connected) return;
		if(world == null || camera == null) return;
		super.onTick(dt);
	}

	@Override
	public void onRender(Batch b) {
		if(!connected) return;
		if(world == null || camera == null) return;
		super.onRender(b);
	}

	@Override
	public void onGUI(Batch b) {
		if(chatUp) chatContainer.render(b);
		if(!connected) info.render(b);
		super.onGUI(b);
	}

	@Override
	public void onResize(int width, int height) {}

}
