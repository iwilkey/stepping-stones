package dev.iwilkey.terrafort.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.assets.Assets;

public enum Item {
	
	TEST_ITEM (
			"Test Item",
			-1, 
			Assets.TEST_ITEM_TEXTURE,
			new Mixer() {
				@Override
				public void mix(Recipe recipe) {
					recipe.add("TEST_ITEM", 2);
				}
			}
	),
	
	WOOD_ITEM (
			"Wood",
			0,
			Assets.WOOD_ITEM,
			new Mixer() {
				@Override
				public void mix(Recipe recipe) {}
			} // Means that this item has no recipe.
	);
	
	private String name;
	private final int ID;
	private TextureRegion texture;
	private Recipe recipe;
	
	private Item(String name, int ID, TextureRegion texture, Mixer mixer) {
		this.name = name;
		this.ID = ID;
		this.texture = texture;
		recipe = new Recipe();
		mixer.mix(recipe);
	}
	
	public String getName() { return name; }
	public int getID() { return ID; }
	public TextureRegion getTexture() { return texture; }
	public Recipe getRecipe() { return recipe; }

}
