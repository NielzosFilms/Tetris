package system;

import objects.Tetromino_Cube;
import objects.tetrominos.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class Handler {
	LinkedList<GameObject> objects = new LinkedList<>();
	private GameObject current_tetromino;
	private GameObject current_tetromino_ghost;
	private int timer = 0;

	public Handler() {}

	public void tick() {
		if(current_tetromino != null) {
			if (timer >= 60) {
				timer = 0;
				if (canMove(0,1)) {
					current_tetromino.setY(current_tetromino.getY() + Game.TILESIZE);
				} else {
					plantTetromino();
				}
			}
			timer++;
		}
		for(int i=0; i<objects.size(); i++) {
			objects.get(i).tick();
		}
		if(current_tetromino != null) current_tetromino.tick();
		checkFilledRow();
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for(int i=0; i<objects.size(); i++) {
			objects.get(i).render(g);
		}
		if(current_tetromino != null) {
			current_tetromino.render(g);
			for(GameObject cube : new LinkedList<>(((Tetromino)current_tetromino).getCubes())) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				try {
					Tetromino_Cube cloned = (Tetromino_Cube) ((Tetromino_Cube) cube).clone();
					int y_offset = 1;
					while(canMove(0, y_offset)) {
						y_offset++;
					}
					y_offset -=1;
					cloned.setY(cloned.getY() + (y_offset * cube.TILESIZE));
					cloned.render(g);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addObject(GameObject object) {
		this.objects.add(object);
	}

	public void removeObject(GameObject object) {
		this.objects.remove(object);
	}

	public void setCurrent_tetromino(GameObject tetromino) {
		this.current_tetromino = tetromino;
	}

	public GameObject getCurrent_tetromino() {
		return this.current_tetromino;
	}

	private boolean canMove(int x_offset, int y_offset) {
		for(GameObject cube : ((Tetromino)current_tetromino).getCubes()) {
			for(GameObject object : objects) {
				if(object.getId() == ID.wall || object.getId() == ID.tetromino_cube) {
					if(object.getY() == cube.getY() + (y_offset * Game.TILESIZE) && object.getX() == cube.getX() + (x_offset * Game.TILESIZE)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void moveTetromino(int x_offset, int y_offset) {
		if(canMove(x_offset, y_offset)) {
			current_tetromino.setY(current_tetromino.getY() + y_offset*Game.TILESIZE);
			current_tetromino.setX(current_tetromino.getX() + x_offset*Game.TILESIZE);
		}
	}

	public void moveTetrominoToBottom() {
		int y_offset = 1;
		while(canMove(0, y_offset)) {
			y_offset++;
		}
		moveTetromino(0, y_offset-1);
		plantTetromino();
		timer = 0;
	}

	public void rotateTetromino(boolean cw) {
		LinkedList<GameObject> rotated = ((Tetromino)current_tetromino).getRotatedInstance(cw);
		boolean canRotate = true;
		for(GameObject cube : rotated) {
			for(GameObject object : objects) {
				if(object.getId() == ID.wall || object.getId() == ID.tetromino_cube) {
					if(cube.getBounds().intersects(object.getBounds())) {
						canRotate = false;
					}
				}
			}
		}
		if(canRotate) {
			Tetromino current = (Tetromino) current_tetromino;
			current.setCubes(rotated);
			int rotation = cw? current.getRotation() + 90 : current.getRotation() - 90;
			if(rotation >= 360) rotation -= 360;
			if(rotation < 0) rotation += 360;
			current.setRotation(rotation);
		}
	}

	private void plantTetromino() {
		for(GameObject c : ((Tetromino)current_tetromino).getCubes()) {
			Tetromino_Cube cube = (Tetromino_Cube) c;
			cube.tick();
			cube.clearParent();
		}
		objects.addAll(((Tetromino)current_tetromino).getCubes());
		setCurrent_tetromino(getNextTetromino(64, 64));
	}

	private void checkFilledRow() {
		int rows_cleared = 0;
		for(int y=Game.TILESIZE; y<Game.SCREEN_HEIGHT-Game.TILESIZE; y+=Game.TILESIZE) {
			LinkedList<GameObject> cubes_on_row = new LinkedList<>();
			for(GameObject object : objects) {
				if(object.getId() == ID.tetromino_cube) {
					if(object.getY() == y) cubes_on_row.add(object);
				}
			}
			if(cubes_on_row.size() >= 10) {
				rows_cleared++;
				objects.removeAll(cubes_on_row);
				for(GameObject object : objects) {
					if(object.getId() == ID.tetromino_cube) {
						if(object.getY() <= y) {
							object.setY(object.getY() + Game.TILESIZE);
						}
					}
				}
			}
		}
		Game.current_score += calculateScore(rows_cleared, Game.current_level);
	}

	public GameObject getNextTetromino(int x, int y) {
		GameObject ret = new Tetromino_I(x, y);
		switch(new Random().nextInt(7)) {
			case 0:
				ret = new Tetromino_I(x, y);
				break;
			case 1:
				ret =  new Tetromino_J(x, y);
				break;
			case 2:
				ret =  new Tetromino_L(x, y);
				break;
			case 3:
				ret =  new Tetromino_O(x, y);
				break;
			case 4:
				ret =  new Tetromino_S(x, y);
				break;
			case 5:
				ret =  new Tetromino_T(x, y);
				break;
			case 6:
				ret =  new Tetromino_Z(x, y);
				break;
		}
		return ret;
	}

	public int calculateScore(int lines_cleared, int level) {
		int line_score = 0;
		switch(lines_cleared) {
			case 1:
				line_score = 40;
				break;
			case 2:
				line_score = 100;
				break;
			case 3:
				line_score = 300;
				break;
			case 4:
				line_score = 1200;
				break;
		}

		return (level + 1) * line_score;
	}
}
