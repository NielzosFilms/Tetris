package objects;

import system.ColorPalette;
import system.Game;
import system.GameObject;
import system.ID;

import java.awt.*;

public class Effect_Special_Text extends GameObject {
	private float lifetime = 60f;
	private float original_lifetime = lifetime;
	private String text;
	public Effect_Special_Text(String text) {
		super(12* Game.TILESIZE+8, 18*Game.TILESIZE+8, ID.effect);
		this.lifetime = lifetime;
		original_lifetime = lifetime;
		this.text = text;
	}

	@Override
	public void tick() {
		lifetime--;
		if(lifetime <= 0) {
			Game.handler.removeObject(this);
		}
	}

	@Override
	public void render(Graphics g) {
		g.setFont(new Font("Tetris", Font.PLAIN, 20));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1f/original_lifetime*(lifetime))));
		g.setColor(ColorPalette.text_effect.color);
		g.drawString(text, x, y);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1f)));
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}
}
