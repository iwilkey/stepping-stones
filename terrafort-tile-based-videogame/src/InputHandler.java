package dev.iwilkey.terrafort;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;

import dev.iwilkey.terrafort.gfx.Renderer;

public class InputHandler {
	
	private static boolean[] keys, jp, cp;
    private static boolean lmbd, rmbd, jcl, ccl, jcr, ccr;

    public static double dMouse, dx, dy;
    public static Rectangle cursorCollider;
    public static float cursorX, cursorY;
    public static boolean rightMouseButton, leftMouseButton, rightMouseButtonDown,
            leftMouseButtonDown, mouseCurrentlyMoving, rightMouseButtonUp, leftMouseButtonUp;
    public static float scrollWheelRequestValue;
    public static boolean isTyping;
    public static int currentKey;
    
    // Game controls
    public static boolean moveRight, moveDown, moveLeft, moveUp,
    	inventoryRequest, inventoryRight, inventoryDown,
    		inventoryLeft, inventoryUp, changeInventoryTable, cleanUpTable;

    public static void init() {
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            keys = new boolean[256];
            jp = new boolean[256];
            cp = new boolean[256];
            cursorCollider = new Rectangle(0, 0, 1, 1);
            setCursorAs("textures/cursor.png");
            initInput();
        }
    }

    public static void setCursorAs(String path) {
        Pixmap pix = new Pixmap(Gdx.files.internal(path));
        Cursor cursor = Gdx.graphics.newCursor(pix, 4, 4);
        Gdx.graphics.setCursor(cursor);
        pix.dispose();
    }

    public static void initInput() {
        switch(Gdx.app.getType()) {
            case Desktop:
                Gdx.input.setInputProcessor(new InputAdapter() {

                    @Override
                    public boolean keyDown(int key) {
                    	isTyping = true;
                    	currentKey = key;
                        keyJustPressed(key);
                        if(key == Settings.MOVE_RIGHT) moveRight = true;
                        if(key == Settings.MOVE_DOWN) moveDown = true;
                        if(key == Settings.MOVE_LEFT) moveLeft = true;
                        if(key == Settings.MOVE_UP) moveUp = true;
                        if(key == Settings.OPEN_CLOSE_INVENTORY) inventoryRequest = true;
                        if(key == Settings.INVENTORY_RIGHT) inventoryRight = true;
                        if(key == Settings.INVENTORY_DOWN) inventoryDown = true;
                        if(key == Settings.INVENTORY_LEFT) inventoryLeft = true;
                        if(key == Settings.INVENTORY_UP) inventoryUp = true;
                        if(key == Settings.CHANGE_INVENTORY_TABLE) changeInventoryTable = true;
                        if(key == Settings.CLEAN_UP_TABLE) cleanUpTable = true;
                        return true;
                    }

                    @Override
                    public boolean keyUp(int key) {
                    	if(key == Settings.MOVE_RIGHT) moveRight = false;
                        if(key == Settings.MOVE_DOWN) moveDown = false;
                        if(key == Settings.MOVE_LEFT) moveLeft = false;
                        if(key == Settings.MOVE_UP) moveUp = false;
                        if(key == Settings.OPEN_CLOSE_INVENTORY) inventoryRequest = false;
                        if(key == Settings.INVENTORY_RIGHT) inventoryRight = false;
                        if(key == Settings.INVENTORY_DOWN) inventoryDown = false;
                        if(key == Settings.INVENTORY_LEFT) inventoryLeft = false;
                        if(key == Settings.INVENTORY_UP) inventoryUp = false;
                        if(key == Settings.CHANGE_INVENTORY_TABLE) changeInventoryTable = false;
                        if(key == Settings.CLEAN_UP_TABLE) cleanUpTable = false;
                        return true;
                    }
                    
                    @Override
                    public boolean scrolled(float x, float y) {
                    	scrollWheelRequestValue = y;
                    	return true;
                    }

                    @Override
                    public boolean mouseMoved(int x, int y) {
                        cursorX = x; cursorY = Gdx.graphics.getHeight() - y;
                        cursorCollider.x = cursorX / Renderer.SCALEX; cursorCollider.y = cursorY / Renderer.SCALEY;
                        return true;
                    }

                    @Override
                    public boolean touchDragged(int x, int y, int pointer) {
                        cursorX = x; cursorY = Gdx.graphics.getHeight() - y;
                        cursorCollider.x = cursorX / Renderer.SCALEX; cursorCollider.y = cursorY / Renderer.SCALEY;
                        return true;
                    }

                    @Override
                    public boolean touchDown(int x, int y, int pointer, int button) {
                        justClicked(button);
                        if(button == Input.Buttons.LEFT) {
                            lmbd = true;
                            leftMouseButton = true;
                        }
                        if(button == Input.Buttons.RIGHT) {
                            rmbd = true;
                            rightMouseButton = true;
                        }
                        return true;
                    }

                    @Override
                    public boolean touchUp(int x, int y, int pointer, int button) {
                        if(button == Input.Buttons.LEFT) {
                            lmbd = false;
                            leftMouseButton = false;
                            leftMouseButtonUp = true;
                        }
                        if(button == Input.Buttons.RIGHT) {
                            rmbd = false;
                            rightMouseButton = false;
                            rightMouseButtonUp = true;
                        }
                        return true;
                    }

                    public boolean keyJustPressed(int KeyCode) {
                        if(KeyCode < 0 || KeyCode >= keys.length) return false;
                        return true;
                    }

                    public boolean justClicked(int button) {
                        switch(button) {
                            case 0: return jcl;
                            case 1: return jcr;
                            default: return false;
                        }
                    }

                });
                break;

            case Android:
            case iOS:
                break;
		default:
			break;
        }
    }

    static int[][] mousePosTwoFrames = new int[2][2];
    static byte frameCounter = 0, frameCounter2 = 0;
    public static void tick() {
        frameCounter++;
        if(frameCounter == 1) {
            mousePosTwoFrames[0][0] = (int)(cursorX * Renderer.SCALEX); 
            mousePosTwoFrames[0][1] = (int)(cursorY * Renderer.SCALEY);
        } else if (frameCounter == 2) {
            mousePosTwoFrames[1][0] = (int)(cursorX * Renderer.SCALEX); 
            mousePosTwoFrames[1][1] = (int)(cursorY * Renderer.SCALEY);
            dx = mousePosTwoFrames[1][0] - mousePosTwoFrames[0][0];
            dy = mousePosTwoFrames[1][1] - mousePosTwoFrames[0][1];
            dMouse = Math.sqrt(Math.pow((mousePosTwoFrames[1][0] - mousePosTwoFrames[0][0]), 2) +
                    Math.pow((mousePosTwoFrames[1][1] - mousePosTwoFrames[0][1]), 2));
            frameCounter = 0;
            mouseCurrentlyMoving = dMouse != 0.0f;
        }

        if(rightMouseButtonUp || leftMouseButtonUp) {
            frameCounter2++;
            if(rightMouseButtonUp && frameCounter2 == 2) {
                rightMouseButtonUp = false;
                frameCounter2 = 0;
            } else if (leftMouseButtonUp && frameCounter2 == 2) {
                leftMouseButtonUp = false;
                frameCounter2 = 0;
            }
        }

        if (Gdx.app.getType() == ApplicationType.Desktop) {
            for(int i = 0; i < keys.length; i++) {
                if(cp[i] && !keys[i]) cp[i] = false;
                else if (jp[i]) {
                    cp[i] = true; jp[i] = false;
                }
                if(!cp[i] && keys[i]) jp[i] = true;
            }
            if(ccl && !lmbd) ccl = false;
            else if (jcl) {
                ccl = true; jcl = false;
            }
            if(!ccl && lmbd) jcl = true;
            if(ccr && !rmbd) ccr = false;
            else if (jcr) {
                ccr = true; jcr = false;
            }
            if(!ccr && rmbd) jcr = true;

            leftMouseButton = lmbd;
            rightMouseButton = rmbd;
            leftMouseButtonDown = jcl;
            rightMouseButtonDown = jcr;
        }

    }

}
