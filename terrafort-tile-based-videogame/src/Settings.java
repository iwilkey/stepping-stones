package dev.iwilkey.terrafort;

import com.badlogic.gdx.Input;

public class Settings {
	
	
	public static int 
		// GUI
		FONT_SIZE,
	
		// Key Binds
		MOVE_RIGHT,
		MOVE_DOWN,
		MOVE_LEFT,
		MOVE_UP,
		OPEN_CLOSE_INVENTORY,
		INVENTORY_RIGHT,
		INVENTORY_DOWN,
		INVENTORY_LEFT,
		INVENTORY_UP,
		CHANGE_INVENTORY_TABLE,
		CLEAN_UP_TABLE;
	
	public static float
		// GUI
		UI_SCALE,
		
		// CAMERA
		CAMERA_ZOOM_SENSITIVITY;
	
	public static void init() {
		initDefaults();
	}
	
	public static void initDefaults() {
		// GUI
		FONT_SIZE = 22;
		UI_SCALE = 1.0f;
		
		// Key Binds
		MOVE_RIGHT = Input.Keys.D;
		MOVE_DOWN = Input.Keys.S;
		MOVE_LEFT = Input.Keys.A;
		MOVE_UP = Input.Keys.W;
		OPEN_CLOSE_INVENTORY = Input.Keys.TAB;
		INVENTORY_RIGHT = Input.Keys.RIGHT;
		INVENTORY_DOWN = Input.Keys.DOWN;
		INVENTORY_LEFT = Input.Keys.LEFT;
		INVENTORY_UP = Input.Keys.UP;
		CHANGE_INVENTORY_TABLE = Input.Keys.SHIFT_RIGHT;
		CLEAN_UP_TABLE = Input.Keys.ENTER;
		
		// CAMERA
		CAMERA_ZOOM_SENSITIVITY = 0.75f;
	}


}
