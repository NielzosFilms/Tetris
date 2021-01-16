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
		int line_width = (int) Game.stroke.getLineWidth();

		g.setColor(ColorPalette.wall.color);
		g.fillRect(x+ line_width/2, y+line_width/2, Game.TILESIZE-line_width, Game.TILESIZE-line_width);
		g.setColor(ColorPalette.wall_border.color);
		g.drawRect(x+line_width/2, y+line_width/2, Game.TILESIZE-line_width, Game.TILESIZE-line_width);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, TILESIZE, TILESIZE);
	}
}
