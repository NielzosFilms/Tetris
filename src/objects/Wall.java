package objects;

import system.ColorPalette;
import system.Game;
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
		Game.renderCube(g, x, y, ColorPalette.gray.color, ColorPalette.light_gray.color);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, TILESIZE, TILESIZE);
	}
}
