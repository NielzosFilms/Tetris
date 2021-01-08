package objects.tetrominos;

import objects.Tetromino_Cube;
import system.ColorPalette;
import system.GameObject;
import system.ID;
import system.Tetromino;

import java.awt.*;
import java.util.LinkedList;

public class Tetromino_J extends GameObject implements Tetromino {
	private final Color COLOR = ColorPalette.blue.color;
	private final Color BORDER_COLOR = ColorPalette.black_dark_blue.color;
	private LinkedList<GameObject> cubes = new LinkedList<GameObject>();

	public Tetromino_J(int x, int y) {
		super(x, y, ID.tetromino);

		cubes.add(new Tetromino_Cube(0, -TILESIZE, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(0, 0, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(0, TILESIZE, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(-TILESIZE, TILESIZE, COLOR, BORDER_COLOR, this));
	}

	@Override
	public void tick() {
		for(GameObject cube : cubes) {
			cube.tick();
		}
	}

	@Override
	public void render(Graphics g) {
		for(GameObject cube : cubes) {
			cube.render(g);
		}
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public LinkedList<GameObject> getCubes() {
		return cubes;
	}
}
