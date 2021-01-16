package objects;

import system.ColorPalette;
import system.Game;
import system.GameObject;
import system.ID;

import java.awt.*;

public class Effect_Score_Text extends GameObject {
	private float lifetime = 60f;
	private float original_lifetime = lifetime;
	private String text;
	private float velY;
	public Effect_Score_Text(String text) {
		super(14*Game.TILESIZE+8, 13*Game.TILESIZE+8, ID.effect);
		this.lifetime = lifetime;
		original_lifetime = lifetime;
		this.text = text;
		velY = -1;
	}

	@Override
	public void tick() {
		lifetime--;
		if(lifetime <= 0) {
			handler.removeObject(this);
		}
		y += velY;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(ColorPalette.white.color);
		g.setFont(new Font("Tetris", Font.PLAIN, 15));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1f/original_lifetime*(lifetime))));
		g.drawString(text, x, y);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1f)));
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}
}
