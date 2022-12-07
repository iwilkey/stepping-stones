package dev.iwilkey.terrafort.item;

import java.util.ArrayList;

public class Recipe {
	
	public static class Component {
		public String itemName;
		public int amount;
		public Component(String itemName, int amount) {
			this.itemName = itemName;
			this.amount = amount;
		}
	}
	
	
	ArrayList<Component> components;
	
	public Recipe() {
		components = new ArrayList<>();
	}
	
	public void add(String itemName, int amount) {
		components.add(new Component(itemName, amount));
	}

}
