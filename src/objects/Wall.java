package objects;

import system.ColorPalette;
import system.GameObject;
import system.ID;

import java.awt.*;

public class Wall extends GameObject {
	public Wall(int x, int y) {
		super(x, y, ID.wall);
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {
		g.setColor(ColorPalette.brown.color);
		g.fillRect(x, y, TILESIZE, TILESIZE);
		g.setColor(ColorPalette.dark_red.color);
		g.drawRect(x, y, TILESIZE, TILESIZE);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, TILESIZE, TILESIZE);
	}
}
