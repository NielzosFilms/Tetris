package objects;

import system.ColorPalette;
import system.Game;
import system.GameObject;
import system.ID;

import java.awt.*;

public class Effect_Clear_Cube extends GameObject {
    private float lifetime = 60;
    private float original_lifetime = lifetime;
    public Effect_Clear_Cube(int x, int y, float lifetime) {
        super(x, y, ID.effect);
        this.lifetime = lifetime;
        original_lifetime = lifetime;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (0.3f/original_lifetime*(lifetime))));
        Game.renderCube(g, x, y, ColorPalette.white.color, ColorPalette.text_highlight.color);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        //g.fillRect(x, y, TILESIZE, TILESIZE);
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }
}
