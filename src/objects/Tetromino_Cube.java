package objects;

import system.ColorPalette;
import system.Game;
import system.GameObject;
import system.ID;

import java.awt.*;

public class Tetromino_Cube extends GameObject implements Cloneable {
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
		if(this.parent != null) {
			this.x = this.parent.getX() + offset_x;
			this.y = this.parent.getY() + offset_y;
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void tick() {
		if(parent != null) {
			x = parent.getX() + offset_x;
			y = parent.getY() + offset_y;
		}
	}

	@Override
	public void render(Graphics g) {
		int line_width = (int) Game.stroke.getLineWidth();

		g.setColor(color);
		g.fillRect(x+ line_width/2, y+line_width/2, Game.TILESIZE-line_width, Game.TILESIZE-line_width);
		g.setColor(border_color);
		g.drawRect(x+line_width/2, y+line_width/2, Game.TILESIZE-line_width, Game.TILESIZE-line_width);
		//g.setColor(color);
		//g.drawRect(x, y, Game.TILESIZE, Game.TILESIZE);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, TILESIZE, TILESIZE);
	}

	public int getOffset_y() {
		return offset_y;
	}
	public int getOffset_x() { return offset_x; }

	public void clearParent() {
		this.parent = null;
	}
}
