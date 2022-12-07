package dev.iwilkey.terrafort.inventory;

import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.ui.Container;
import dev.iwilkey.terrafort.ui.ItemSlotList;
import dev.iwilkey.terrafort.ui.Text;

public class Inventory {
	
	public static final int ITEM_STACK_MAX = 10,
		AMOUNT_ITEMS_INVENTORY = 9,
		AMOUNT_ITEMS_INPUT = 5;
	
	public final int DEFAULT_MENU_WIDTH = 800;
	
	public boolean isActive = false;
	public Container masterContainer,
		inventoryContainer,
		engineeringContainer;
	
	public ItemSlotList inventorySlotList,
		engineeringInputList,
		craftingList;
	
	Text t, tt, ttt, tttt, ttttt;
	public Inventory() {
		
		masterContainer = new Container(0, 0, DEFAULT_MENU_WIDTH, (int)(DEFAULT_MENU_WIDTH * (4 / 6f)));
		masterContainer.centerScreenX();
		masterContainer.centerScreenY();
		masterContainer.setBackgroundTexture(Assets.SIX_BY_FOUR_MENU);
		
		inventoryContainer = masterContainer.addContainer(new Container(0, 0, 300, (int)(300 * (3 / 2f))));
		inventoryContainer.centerContainerY(masterContainer);
		inventoryContainer.centerContainerX(masterContainer);
		inventoryContainer.setX((int)(inventoryContainer.DEFAULT_COLLIDER.x - (inventoryContainer.DEFAULT_COLLIDER.width / 2)) - (int)(35 * Settings.UI_SCALE));
		inventoryContainer.setBackgroundTexture(Assets.TWO_BY_THREE_MENU);
		
		engineeringContainer = masterContainer.addContainer(new Container(0, 0, 300, (int)(300 * (3 / 2f))));
		engineeringContainer.centerContainerY(masterContainer);
		engineeringContainer.centerContainerX(masterContainer);
		engineeringContainer.setX((int)(engineeringContainer.DEFAULT_COLLIDER.x + (engineeringContainer.DEFAULT_COLLIDER.width / 2)) + (int)(35 * Settings.UI_SCALE));
		engineeringContainer.setBackgroundTexture(Assets.TWO_BY_THREE_MENU);
		
		t = inventoryContainer.addText(new Text("Inventory"));
		inventoryContainer.centerTextX(t);
		inventoryContainer.setTextY(t, 35);
		
		inventorySlotList = inventoryContainer.addItemSlotList(new ItemSlotList(this, "items", 0, 0, 275, 325, 45));
		inventorySlotList.centerContainerX(inventoryContainer);
		inventorySlotList.centerContainerY(inventoryContainer);
		inventorySlotList.setY((int)inventorySlotList.DEFAULT_COLLIDER.y - (int)(20 * Settings.UI_SCALE));
		tttt = inventorySlotList.addText(new Text("Items"));
		tttt.setSize(12);
		inventorySlotList.centerTextX(tttt);
		inventorySlotList.setTextY(tttt, 10);
		
		tt = engineeringContainer.addText(new Text("Engineering"));
		engineeringContainer.centerTextX(tt);
		engineeringContainer.setTextY(tt, 35);
		
		engineeringInputList = engineeringContainer.addItemSlotList(new ItemSlotList(this, "input", 0, 0, 275, 162, 20));
		engineeringInputList.centerContainerX(engineeringContainer);
		engineeringInputList.centerContainerY(engineeringContainer);
		engineeringInputList.setY((int)engineeringInputList.DEFAULT_COLLIDER.y + (int)(60 * Settings.UI_SCALE));
		ttt = engineeringInputList.addText(new Text("Input"));
		ttt.setSize(12);
		engineeringInputList.centerTextX(ttt);
		engineeringInputList.setTextY(ttt, 10);
		
		craftingList = engineeringContainer.addItemSlotList(new ItemSlotList(this, "output", 0, 0, 275, 162, 20));
		craftingList.centerContainerX(engineeringContainer);
		craftingList.centerContainerY(engineeringContainer);
		craftingList.setY((int)craftingList.DEFAULT_COLLIDER.y - (int)(104 * Settings.UI_SCALE));
		ttttt = craftingList.addText(new Text("Output"));
		ttttt.setSize(12);
		craftingList.centerTextX(ttttt);
		craftingList.setTextY(ttttt, 10);
	
		//for(int i = 0; i < 999; i++)inventorySlotList.addItem(Item.WOOD_ITEM);
		
		inventorySlotList.set((byte)(0));
		inventorySlotList.tableSelected = true;
		
	}
	
	public void tick() {
		if(InputHandler.inventoryRequest) {
			if(isActive) isActive = false;
			else isActive = true;
			InputHandler.inventoryRequest = false;
		}
		
		if(isActive) masterContainer.tick();
	}
	
	public void render(Batch b) {
		if(isActive)
			masterContainer.render(b);
	}

}
