package dev.iwilkey.terrafort.gfx;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class Geometry {
	
	public enum Shape {
		LINE, FILLED_RECTANGLE, OUTLINE_RECTANGLE;
	}
	
	static ShapeRenderer shapeRenderer = new ShapeRenderer();
	public static ArrayList<GeometryRequest> requests = new ArrayList<>();
	
	public static class GeometryRequest {
		Shape shape;
		int x, y, x2, y2, width, height, 
			lineWidth;
		Color color;
		Matrix4 combined;
		public GeometryRequest(Shape shape, int x, int y, int x2, int y2, int lw, Color color) {
			this.shape = shape;
			this.lineWidth = lw;
			this.color = color;
			this.x = x; this.y = y;
			this.x2 = x2; this.y2 = y2;
			width = (x2 - x); height = (y2 - y);
			this.combined = null;
		}
		
		public GeometryRequest(Shape shape, int x, int y, int x2, int y2, int lw, Color color, Matrix4 combined) {
			this.shape = shape;
			this.lineWidth = lw;
			this.color = color;
			this.x = x; this.y = y;
			this.x2 = x2; this.y2 = y2;
			width = (x2 - x); height = (y2 - y);
			this.combined = combined;
		}
		
		public void setWidth(int width) { this.width = width; }
		public void setHeight(int height) { this.height = height; }

		public void render() {
			Gdx.gl.glLineWidth(lineWidth);
			shapeRenderer.setColor(color);
			switch(shape) {
				case LINE:
					shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
					if(combined != null) shapeRenderer.setProjectionMatrix(combined);
		            shapeRenderer.line(x, y, x2, y2);
					break;
				case FILLED_RECTANGLE:
					shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
					if(combined != null) shapeRenderer.setProjectionMatrix(combined);
					shapeRenderer.rect(x, y, width, height);
					break;
				case OUTLINE_RECTANGLE:
					shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
					if(combined != null) shapeRenderer.setProjectionMatrix(combined);
					shapeRenderer.rect(x, y, width, height);
					break;
			}
			
			shapeRenderer.end();
		}
	}
	
	
	
	

}
