package dev.iwilkey.terrafort.ui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.Settings;
import dev.iwilkey.terrafort.assets.Assets;
import dev.iwilkey.terrafort.gfx.Renderer;

public class Container extends UIObject {

	ArrayList<Container> containers;
	ArrayList<Text> texts;
	ArrayList<Button> buttons;
	ArrayList<InputField> inputFields;
	Color backgroundColor;
	TextureRegion backgroundTexture = null;
	
	// Construction
	public Container(int x, int y, int width, int height) {
		super(x, y, (int)(width * Settings.UI_SCALE), (int)(height * Settings.UI_SCALE));
		texts = new ArrayList<>();
		buttons = new ArrayList<>();
		containers = new ArrayList<>();
		inputFields = new ArrayList<>();
		backgroundColor = new Color(1, 1, 1, 1);
	}
	
	// Physical attributes
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}
	
	// Location
	public void setX(int x) {
		int diff = (int)DEFAULT_COLLIDER.x - x;
		DEFAULT_COLLIDER.x = x;
		for(Text t : texts) t.setX(t.xx - diff);
		for(Button b : buttons) b.setX((int)b.DEFAULT_COLLIDER.x - diff);
		for(InputField i : inputFields) i.setX((int)i.DEFAULT_COLLIDER.x - diff);
		for(Container c : containers) c.setX((int)c.DEFAULT_COLLIDER.x - diff);
	}
	
	public void setY(int y) {
		int diff = (int)DEFAULT_COLLIDER.y - y;
		DEFAULT_COLLIDER.y = y;
		for(Text t : texts) t.setY(t.yy - diff);
		for(Button b : buttons) b.setY((int)b.DEFAULT_COLLIDER.y - diff);
		for(InputField i : inputFields) i.setY((int)i.DEFAULT_COLLIDER.y - diff);
		for(Container c : containers) c.setY((int)c.DEFAULT_COLLIDER.y - diff);
	}
	
	// Dimensions
	public void setWidth(int width) {
		DEFAULT_COLLIDER.width = (width * Settings.UI_SCALE);
	}
	
	public void setHeight(int height) {
		DEFAULT_COLLIDER.height = (height * Settings.UI_SCALE);
	}
	
	public int centerScreenX() {
		setX((int)((Renderer.DEFAULT_WIDTH / 2) - (DEFAULT_COLLIDER.width / 2)));
		return (int)DEFAULT_COLLIDER.x;
	}
	
	public int centerScreenY() {
		setY((int)((Renderer.DEFAULT_HEIGHT / 2) - (DEFAULT_COLLIDER.height / 2)));
		return (int)DEFAULT_COLLIDER.y;
	}
	
	// Text methods
	public Text addText(Text text) {
		texts.add(text);
		this.setTextX(text, 0);
		this.setTextY(text, 0);
		return text;
	}
	
	public void removeText(Text text) {
		texts.remove(text);
	}
	
	public void setTextX(Text text, int xx) {
		for(Text t : texts)
			if(t == text)
				t.setX((int)DEFAULT_COLLIDER.x + (int)(xx * Settings.UI_SCALE));
	}
	
	public void setTextY(Text text, int yy) {
		for(Text t : texts)
			if(t == text)
				t.setY((int)(DEFAULT_COLLIDER.y + DEFAULT_COLLIDER.height) - (int)(yy * Settings.UI_SCALE));
	}
	
	public void centerTextX(Text text) {
		for(Text t : texts)
			if(t == text) 
				t.centerContainerX(this);
	}
	
	public void centerTextY(Text text) {
		for(Text t : texts)
			if(t == text) 
				t.centerContainerY(this);
	}
	
	// Button methods
	public Button addButton(Button button) {
		buttons.add(button);
		this.setButtonX(button, (int)button.DEFAULT_COLLIDER.x);
		this.setButtonY(button, (int)button.DEFAULT_COLLIDER.y + (int)button.DEFAULT_COLLIDER.height);
		return button;
	}
	
	public void removeButton(Button button) {
		buttons.remove(button);
	}
	
	public void setButtonX(Button button, int xx) {
		for(Button b : buttons)
			if(b == button)
				b.setX((int)DEFAULT_COLLIDER.x + 
						(int)(xx * Settings.UI_SCALE));
	}
	
	public void setButtonY(Button button, int yy) {
		for(Button b : buttons)
			if(b == button)
				b.setY((int)(DEFAULT_COLLIDER.y + DEFAULT_COLLIDER.height) - 
						(int)(yy * Settings.UI_SCALE));
	}
	
	public void centerButtonX(Button button) {
		for(Button b : buttons)
			if(b == button)
				b.centerContainerX(this);
	}
	
	public void centerButtonY(Button button) {
		for(Button b : buttons)
			if(b == button)
				b.centerContainerY(this);
	}
	
	// Container methods
	public Container addContainer(Container container) {
		containers.add(container);
		this.setContainerX(container, (int)container.DEFAULT_COLLIDER.x);
		this.setContainerY(container, (int)container.DEFAULT_COLLIDER.y - (int)container.DEFAULT_COLLIDER.height);
		return container;
	}
	
	public void removeContainer(Container container) {
		containers.remove(container);
	}
	
	public void setContainerX(Container container, int xx) {
		for(Container c : containers)
			if(c == container)
				c.setX((int)DEFAULT_COLLIDER.x + 
						(int)(xx * Settings.UI_SCALE));
	}
	
	public void setContainerY(Container container, int yy) {
		for(Container c : containers)
			if(c == container)
				c.setY((int)DEFAULT_COLLIDER.y + 
						(int)(yy * Settings.UI_SCALE));
	}
	
	// Input Field
	public InputField addInputField(InputField input) {
		inputFields.add(input);
		this.setInputFieldX(input, (int)input.DEFAULT_COLLIDER.x);
		this.setInputFieldY(input, (int)input.DEFAULT_COLLIDER.y + (int)input.DEFAULT_COLLIDER.height);
		return input;
	}
	
	public void removeInputField(InputField input) {
		inputFields.remove(input);
	}
	
	public void setInputFieldX(InputField input, int xx) {
		for(InputField i : inputFields)
			if(i == input)
				i.setX((int)DEFAULT_COLLIDER.x + 
						(int)(xx * Settings.UI_SCALE));
	}
	
	public void setInputFieldY(InputField input, int yy) {
		for(InputField i : inputFields)
			if(i == input)
				i.setY((int)(DEFAULT_COLLIDER.y + DEFAULT_COLLIDER.height) - 
						(int)(yy * Settings.UI_SCALE));
	}
	
	public void centerInputFieldX(InputField input) {
		for(InputField i : inputFields)
			if(i == input)
				i.centerContainerX(this);
	}
	
	public void centerInputFieldY(InputField input) {
		for(InputField i : inputFields)
			if(i == input)
				i.centerContainerY(this);
	}
	
	// Item Slot List
	public ItemSlotList addItemSlotList(ItemSlotList container) {
		containers.add(container);
		this.setContainerX(container, (int)container.DEFAULT_COLLIDER.x);
		this.setContainerY(container, (int)container.DEFAULT_COLLIDER.y - (int)container.DEFAULT_COLLIDER.height);
		return container;
	}
	
	// Local
	public int centerContainerX(Container c) {
		DEFAULT_COLLIDER.x = (int)(c.DEFAULT_COLLIDER.x + 
				(c.DEFAULT_COLLIDER.width / 2)) - (DEFAULT_COLLIDER.width / 2);
		return (int)DEFAULT_COLLIDER.x;
	}
	
	public int centerContainerY(Container c) {
		DEFAULT_COLLIDER.y = (int)(c.DEFAULT_COLLIDER.y + 
				(c.DEFAULT_COLLIDER.height / 2)) - (DEFAULT_COLLIDER.height / 2);
		return (int)DEFAULT_COLLIDER.y;
	}
	
	public void setBackgroundTexture(TextureRegion t) {
		this.backgroundTexture = t;
	}
	
	// Ticking
	@Override
	public void tick() {
		for(Container c : containers) c.tick();
		for(Button b : buttons) b.tick();
		for(InputField i : inputFields) i.tick();
	}

	// Rendering
	@Override
	public void render(Batch b) {
		
		if(backgroundTexture == null) {
			b.setColor(backgroundColor);
			b.draw(Assets.SIGNAL, DEFAULT_COLLIDER.x, 
					DEFAULT_COLLIDER.y, DEFAULT_COLLIDER.width, DEFAULT_COLLIDER.height);
			b.setColor(Color.WHITE);
		} else b.draw(backgroundTexture, DEFAULT_COLLIDER.x, 
					DEFAULT_COLLIDER.y, DEFAULT_COLLIDER.width, DEFAULT_COLLIDER.height);
		
		if(!(this instanceof ItemSlotList)) for(Container c : containers) c.render(b);
		for(InputField i : inputFields) i.render(b);
		for(Button bb : buttons) bb.render(b);
		if(!(this instanceof TextLog)) for(Text t : texts) 
			t.render(b, t.xx, t.yy);
	}
	
}
