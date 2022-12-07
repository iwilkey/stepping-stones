package dev.iwilkey.terrafort.ui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;

public class TextLog extends Container {
	
	public static class Log {
		public Text text;
		public String username,
			message;
		public int index;
		public byte height;
		public Log(int index, String username, String message) {
			this.username = username;
			this.message = message;
			this.index = index;
			text = new Text(log());
			text.setSize(12);
			height = (byte)text.getLayoutHeight();
		}
		public String log() {
			return "\n[" + username + "] >" + message; 
		}
	}
	
	public ArrayList<Log> logs;

	public TextLog(int x, int y, int width, int height) {
		super(x, y, width, height);
		logs = new ArrayList<>();
	}
	
	public void addLog(String username, String message) {
		logs.add(new Log(logs.size(), username, message));
	}
	
	public void removeLog(int index) {
		logs.remove(index);
	}
	
	@Override
	public void render(Batch b) {
		super.render(b);
		for(Log l : logs) {
			System.out.println(l.index);
			l.text.render(b, (int)DEFAULT_COLLIDER.x, (int)DEFAULT_COLLIDER.y + ((logs.size() - l.index) * l.height));
		}
	}
}
