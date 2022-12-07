package dev.iwilkey.terrafort.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.inventory.Inventory;
import dev.iwilkey.terrafort.item.Item;

public class ItemSlotList extends Container {

	public static class ItemSlot extends Container {
		
		public static final int SLOT_HEIGHT = (int)(24 * Settings.UI_SCALE);
		
		int off;
		int numInList;
		public byte count;
		public Item item;
		public boolean selected;
		public Text selector,
			name;
		
		ItemSlotList parent;
		
		public ItemSlot(ItemSlotList itemSlotList, Item i, int numInList, int off) {
			super(0, 0, (int)itemSlotList.DEFAULT_COLLIDER.width, SLOT_HEIGHT);
			itemSlotList.addContainer(this);
			itemSlotList.setContainerY(this, (int)(itemSlotList.DEFAULT_COLLIDER.height / Settings.UI_SCALE) - ((int)((numInList * SLOT_HEIGHT) / Settings.UI_SCALE) + off));
			
			this.parent = itemSlotList;
			this.off = off;
			
			selector = new Text(">");
			selector.setX((int)DEFAULT_COLLIDER.x + (int)(35 * Settings.UI_SCALE));
			selector.setY((int)DEFAULT_COLLIDER.y + (SLOT_HEIGHT / 2));
			
			this.count = 1;
			name = new Text(i.getName() + " x" + this.count);
			name.setSize(12);
			name.centerContainerY(this);

			this.item = i;
			this.numInList = numInList;
			selected = false;
		}
		
		public void moveDown() {
			numInList--;
			parent.setContainerY(this, (int)(parent.DEFAULT_COLLIDER.height / Settings.UI_SCALE) - 
					((int)((numInList * SLOT_HEIGHT) / Settings.UI_SCALE) + off));
			selector.setX((int)DEFAULT_COLLIDER.x + (int)(35 * Settings.UI_SCALE));
			selector.setY((int)DEFAULT_COLLIDER.y + (SLOT_HEIGHT / 2));
			name.centerContainerY(this);
		}
		
		@Override
		public void tick() {}

		@Override
		public void render(Batch b) {
			if(selected && parent.tableSelected) selector.render(b);
			b.draw(item.getTexture(), DEFAULT_COLLIDER.x + (50 * Settings.UI_SCALE), DEFAULT_COLLIDER.y, (16 * Settings.UI_SCALE), (16 * Settings.UI_SCALE));
			name.render(b, (int)DEFAULT_COLLIDER.x + (int)((50 + 24) * (Settings.UI_SCALE)), (int)(DEFAULT_COLLIDER.y + (10 * Settings.UI_SCALE)));
		}
		
	}
	
	public String tableName;
	public boolean tableSelected = false;
	public ArrayList<ItemSlot> slots;
	
	Inventory inventory;
	byte selected = 0;
	int off;
	
	Text emptyText;
	
	public ItemSlotList(Inventory inventory, String name, int x, int y, int width, int height, int off) {
		super(x, y, width, height);
		slots = new ArrayList<>();
		this.off = off;
		this.tableName = name;
		this.inventory = inventory;
		setBackgroundTexture(Assets.ONE_BY_ONE_MENU);
		
		
		emptyText = new Text("");
		emptyText.message = "View selected, but there \nseems to be nothing in here! \nPress " + Input.Keys.toString(Settings.CHANGE_INVENTORY_TABLE) + " to switch views!";
		emptyText.color = Color.GRAY;
		emptyText.setSize(12);
		
		switch (name) {
			case "items":
				emptyText.centerContainerY(inventory.inventoryContainer);
				emptyText.centerContainerX(inventory.inventoryContainer);
				break;
			case "input":
				emptyText.centerContainerY(inventory.engineeringContainer);
				emptyText.centerContainerX(inventory.engineeringContainer);
				emptyText.setY(emptyText.yy + (int)(60 * Settings.UI_SCALE));
				break;
			case "output":
				emptyText.centerContainerY(inventory.engineeringContainer);
				emptyText.centerContainerX(inventory.engineeringContainer);
				emptyText.setY(emptyText.yy - (int)(104 * Settings.UI_SCALE));
				break;
		}
		
	}
	
	public void control() {
		if(tableSelected) {
			
			switch(tableName) {
				
				case "items":
					
					if(slots.size() > 0) {
						if(InputHandler.inventoryUp) {
							up();
							InputHandler.inventoryUp = false;
						}
						
						if(InputHandler.inventoryDown) {
							down();
							InputHandler.inventoryDown = false;
						}
						
						if(InputHandler.inventoryRight) {
							if(inventory.engineeringInputList.addItem(returnSelected().item))
								this.editSlot(returnSelected(), returnSelected().item, returnSelected().count - 1);
							InputHandler.inventoryRight = false;
						}
						
						if(InputHandler.cleanUpTable) {
							cleanUpTable();
							InputHandler.cleanUpTable = false;
						}
					}
					
					if(InputHandler.changeInventoryTable) {

						inventory.engineeringInputList.tableSelected = true;
						this.tableSelected = false;
						inventory.craftingList.tableSelected = false;
						inventory.engineeringInputList.set((byte)0);
						
						InputHandler.changeInventoryTable = false;
					}
						
					break;
					
				case "input":
					
					if(slots.size() > 0) {
						if(InputHandler.inventoryLeft) {
							if(inventory.inventorySlotList.addItem(returnSelected().item))
								this.editSlot(returnSelected(), returnSelected().item, returnSelected().count - 1);
							InputHandler.inventoryLeft = false;
						}
						
						if(InputHandler.inventoryUp) {
							up();
							InputHandler.inventoryUp = false;
						}
						
						if(InputHandler.inventoryDown) {
							down();
							InputHandler.inventoryDown = false;
						}
					}
					
					if(InputHandler.cleanUpTable) {
						cleanUpTable();
						InputHandler.cleanUpTable = false;
					}
					
					if(InputHandler.changeInventoryTable) {
						
						inventory.craftingList.tableSelected = true;
						inventory.inventorySlotList.tableSelected = false;
						this.tableSelected = false;
						inventory.inventorySlotList.set((byte)0);
						
						InputHandler.changeInventoryTable = false;
					}
					
					break;
				
				case "output":
					if(slots.size() > 0) {
						if(InputHandler.cleanUpTable) {
							cleanUpTable();
							InputHandler.cleanUpTable = false;
						}
					}
					
					if(InputHandler.changeInventoryTable) {
						
						inventory.craftingList.tableSelected = false;
						inventory.inventorySlotList.tableSelected = true;
						this.tableSelected = false;
						inventory.craftingList.set((byte)0);
						
						InputHandler.changeInventoryTable = false;
					}
				
					break;
					
			}
		}
	}
	
	private void down() {
		selected++;
		if(selected >= slots.size()) selected = 0;
		for(ItemSlot s : slots) s.selected = false;
		if(slots.size() > 0) slots.get(selected).selected = true;
	}
	
	private void up() {
		selected--;
		if(selected < 0) selected = (byte)(slots.size() - 1);
		for(ItemSlot s : slots) s.selected = false;
		if(slots.size() > 0) slots.get(selected).selected = true;
	}
	
	public void set(byte i) {
		selected = i;
		if(selected < 0) selected = (byte)(slots.size() - 1);
		else if(selected >= slots.size()) selected = 0;
		for(ItemSlot s : slots) s.selected = false;
		if(slots.size() > 0) slots.get(selected).selected = true;
	}
	
	public void editSlot(ItemSlot s, Item i, int count) {
		s.item = i;
		s.count = (byte)count;
		s.name.message = s.item.getName() + " x" + s.count;
		if(s.count <= 0) removeSlot(s);
	}
	
	@Override
	public void tick() {
		super.tick();
		control();
	}
	
	@Override
	public void render(Batch b) {
		super.render(b);
		
		if(slots.size() == 0 && tableSelected) emptyText.render(b);
		// Own way of rendering item slots
		for(int i = 0; i < slots.size(); i++) {
			slots.get(i).render(b);
		}
	}
	
	public ItemSlot returnSelected() {
		if(tableSelected && slots.size() > 0) 
			return slots.get(selected);
		return null;
	}
	
	public void cleanUpTable() {
		HashMap<Item, Integer> contents = new HashMap<>();
		for(ItemSlot s : slots) {
			if(!contents.containsKey(s.item)) contents.put(s.item, (int)s.count);
			else contents.put(s.item, contents.get(s.item) + (int)s.count);
		}
		slots.clear();
		for(Map.Entry<Item, Integer> item : contents.entrySet()) {
			for(int i = 0; i < item.getValue(); i++) {
				addItem(item.getKey());
			}
		}
		set((byte)0);
	}
	
	public boolean addItem(Item i) {
		for(ItemSlot s : slots) {
			if(s.item == i) {
				if(s.count + 1 > Inventory.ITEM_STACK_MAX) continue;
				s.count++;
				s.name.message = s.item.getName() + " x" + s.count;
				return true;
			}
		}
		
		if(this.tableName == "items") {
			if(slots.size() + 1 > Inventory.AMOUNT_ITEMS_INVENTORY) return false;
		} else if (this.tableName == "input" ) {
			if(slots.size() + 1 > Inventory.AMOUNT_ITEMS_INPUT) return false;
		}
		slots.add(new ItemSlot(this, i, slots.size() + 1, off));
		return true;
	}
	
	private void removeSlot(ItemSlot s) {
		for(int ii = 0; ii < slots.size(); ii++) {
			if(slots.get(ii) == s) {
				for(int iii = ii; iii < slots.size(); iii++) {
					slots.get(iii).moveDown();
				}
				slots.remove(s);
			}
		}
		set((byte)(selected));
	}
	
	public void removeItem(Item i) {
		for(ItemSlot s : slots) {
			if(s.item == i) {
				if((s.count - 1 <= 0)) break;
				s.count--;
			}
		}
		
		for(int ii = 0; ii < slots.size(); ii++) {
			if(slots.get(ii).item == i) {
				for(int iii = ii; iii < slots.size(); iii++) {
					slots.get(iii).moveDown();
				}
				slots.remove(ii);
			}
		}
	}
}
