package objects.tetrominos;

import objects.Tetromino_Cube;
import system.ColorPalette;
import system.GameObject;
import system.ID;

import java.awt.*;
import java.util.LinkedList;

public class Tetromino_I extends GameObject {
	private final Color COLOR = ColorPalette.light_blue.color;
	private final Color BORDER_COLOR = ColorPalette.blue.color;
	private LinkedList<Tetromino_Cube> cubes = new LinkedList<Tetromino_Cube>();

	private int timer = 0;

	public Tetromino_I(int x, int y) {
		super(x, y, ID.tetromino_I);

		cubes.add(new Tetromino_Cube(-TILESIZE, 0, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(0, 0, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(TILESIZE, 0, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(TILESIZE*2, 0, COLOR, BORDER_COLOR, this));
	}

	@Override
	public void tick() {
		if(timer >= 60) {
			timer = 0;
			y += TILESIZE;
		}
		for(Tetromino_Cube cube : cubes) {
			cube.tick();
		}
		timer++;
	}

	@Override
	public void render(Graphics g) {
		for(Tetromino_Cube cube : cubes) {
			cube.render(g);
		}
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}
}
