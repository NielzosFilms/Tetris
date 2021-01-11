package objects.tetrominos;

import objects.Tetromino_Cube;
import system.ColorPalette;
import system.GameObject;
import system.ID;
import system.Tetromino;

import java.awt.*;
import java.util.LinkedList;

public class Tetromino_O extends GameObject implements Tetromino {
	private final Color COLOR = ColorPalette.yellow.color;
	private final Color BORDER_COLOR = ColorPalette.orange.color;
	private LinkedList<GameObject> cubes = new LinkedList<GameObject>();

	private int rotation = 0;

	public Tetromino_O(int x, int y) {
		super(x, y, ID.tetromino);

		cubes.add(new Tetromino_Cube(0, 0, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(TILESIZE, 0, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(TILESIZE, TILESIZE, COLOR, BORDER_COLOR, this));
		cubes.add(new Tetromino_Cube(0, TILESIZE, COLOR, BORDER_COLOR, this));
	}

	@Override
	public void tick() {
		for(int i=0; i<cubes.size(); i++) {
			cubes.get(i).tick();
		}
	}

	@Override
	public void render(Graphics g) {
		for(int i=0; i<cubes.size(); i++) {
			cubes.get(i).render(g);
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

	@Override
	public void setCubes(LinkedList<GameObject> cubes) {
		this.cubes = cubes;
	}

	@Override
	public LinkedList<GameObject> getRotatedInstance(boolean cw) {
		int tmp_rot = cw? rotation + 90 : rotation - 90;
		if(tmp_rot >= 360) tmp_rot -= 360;
		if(tmp_rot < 0) tmp_rot += 360;

		LinkedList<GameObject> ret = new LinkedList<GameObject>();
		ret.add(new Tetromino_Cube(0, 0, COLOR, BORDER_COLOR, this));
		ret.add(new Tetromino_Cube(TILESIZE, 0, COLOR, BORDER_COLOR, this));
		ret.add(new Tetromino_Cube(TILESIZE, TILESIZE, COLOR, BORDER_COLOR, this));
		ret.add(new Tetromino_Cube(0, TILESIZE, COLOR, BORDER_COLOR, this));
		return ret;
	}

	@Override
	public int getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
}
