package objects;

import system.GameObject;
import system.ID;

import java.awt.*;

public class Tetromino_Cube extends GameObject {
	private Color color, border_color;
	private GameObject parent;
	private int offset_x, offset_y;
	public Tetromino_Cube(int x, int y, Color color, Color border_color, GameObject parent) {
		super(x, y, ID.tetromino_cube);
		this.offset_x = x;
		this.offset_y = y;
		this.color = color;
		this.border_color = border_color;
		this.parent = parent;
	}

	@Override
	public void tick() {
		x = parent.getX() + offset_x;
		y = parent.getY() + offset_y;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, TILESIZE, TILESIZE);
		g.setColor(border_color);
		g.drawRect(x, y, TILESIZE, TILESIZE);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, TILESIZE, TILESIZE);
	}
}
