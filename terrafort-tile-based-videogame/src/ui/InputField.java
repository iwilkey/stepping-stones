package dev.iwilkey.terrafort.ui;

import java.util.Scanner;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import dev.iwilkey.terrafort.InputHandler;

// Very primitive input field.
// TODO: Make this faster, more efficient, and all-key-encompassing.
public class InputField extends UIObject {
	
	Scanner scanner;
	boolean isFocused = true,
			inputForTextLog = false;
	TextLog textLog = null;
	int charLimit = 60,
			textSize;
	Text currentText;
	Text cursor;
	
	public InputField(int x, int y, int width, int height, int textSize) {
		super(x, y, width, height);
		scanner = new Scanner(System.in);
		currentText = new Text("> ");
		currentText.setSize(textSize);
		currentText.setColor(Color.WHITE);
		this.textSize = textSize;
		cursor = new Text(" |");
		cursor.setSize(textSize);
		cursor.setColor(Color.WHITE);
	}
	
	public TextLog attachTextLog(TextLog textLog) {
		inputForTextLog = true;
		this.textLog = textLog;
		return textLog;
	}
	
	char c;
	@Override
	public void tick() {
		if(isFocused) {
			if(InputHandler.isTyping) {
				System.out.println(Input.Keys.toString(InputHandler.currentKey)); // Debug
				if((Input.Keys.toString(InputHandler.currentKey)).toCharArray().length == 1) {
					c = Input.Keys.toString(InputHandler.currentKey).toCharArray()[0];
				} else if(Input.Keys.toString(InputHandler.currentKey).equals("Space")) {
					if(currentText.message.length() + 1 > charLimit) return;
					currentText.message += " ";
					cursor.message = addChar(cursor.message, ' ', 0);
					InputHandler.isTyping = false;
					return;
				} else if(Input.Keys.toString(InputHandler.currentKey).equals("Delete")) {
					if(currentText.message.length() - 1 >= 2) {
						currentText.message = currentText.message.substring(0, currentText.message.length() - 1);
						cursor.message = cursor.message.substring(1);
					}
					InputHandler.isTyping = false;
					return;
				} else if(Input.Keys.toString(InputHandler.currentKey).equals("Enter")) {
					if(inputForTextLog) textLog.addLog("Debug", currentText.message);
					currentText.message = "> ";
					cursor.message = " |";
					InputHandler.isTyping = false;
					return;
				} else c = '/';
		
				if(currentText.message.length() + 1 > charLimit) return;
				currentText.message += (isLetterOrDigit(c)) ? c : "";
				if((isLetterOrDigit(c)))
					cursor.message = addChar(cursor.message, ' ', 0);
				InputHandler.isTyping = false;
			}
			
		}
	}
	
	Text t = new Text(" ");
	public void setCharLimitInContainer(Container c) {
		t.setSize(textSize);
		int w = t.getLayoutWidth();
		charLimit = (int)(c.DEFAULT_COLLIDER.width / w) - 3;
	}

	@Override
	public void render(Batch b) {
		currentText.render(b, (int)DEFAULT_COLLIDER.x, (int)DEFAULT_COLLIDER.y);
		cursor.render(b, (int)DEFAULT_COLLIDER.x + 6, (int)DEFAULT_COLLIDER.y);
	}
	
	private boolean isLetterOrDigit(char c) {
	    return (c >= 'a' && c <= 'z') ||
	           (c >= 'A' && c <= 'Z') ||
	           (c >= '0' && c <= '9');
	}
	
	public String addChar(String str, char ch, int position) {
	    return str.substring(0, position) + ch + str.substring(position);
	}

}
