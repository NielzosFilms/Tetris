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
		super(parent.getX() + x, parent.getX() + y, ID.tetromino_cube);
		this.offset_x = x;
		this.offset_y = y;
		this.color = color;
		this.border_color = border_color;
		this.parent = parent;
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
		Game.renderCube(g, x, y, color, border_color);

		g.setColor(ColorPalette.purple.color);
		Rectangle bnds = getBounds();
		g.drawRect(bnds.x, bnds.y, bnds.width, bnds.height);
		/*g.setColor(ColorPalette.white.color);
		g.drawString("" + y, x, y+10);*/
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
